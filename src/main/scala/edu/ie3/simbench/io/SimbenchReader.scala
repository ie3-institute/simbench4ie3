package edu.ie3.simbench.io

import java.nio.file.Path

import edu.ie3.simbench.exception.io.IoException
import edu.ie3.simbench.model.datamodel.types.{
  LineType,
  Transformer2WType,
  Transformer3WType
}
import edu.ie3.simbench.model.datamodel.{
  Coordinate,
  ExternalNet,
  GridModel,
  Load,
  Node,
  RES,
  SimbenchModel,
  Transformer2W,
  Transformer3W
}

import scala.concurrent.duration.Duration
import scala.concurrent.{
  Await,
  ExecutionContext,
  ExecutionContextExecutor,
  Future
}
import scala.util.{Failure, Success}

/**
  * Reading all simbench models from a given data set
  *
  * @param folderPath     Path to the folder, where the de-compressed files do lay
  * @param separator      Separator used in the files
  * @param fileExtension  Extension of the files
  * @param fileEncoding   Encoding of the files
  */
final case class SimbenchReader(folderPath: Path,
                                separator: String = ";",
                                fileExtension: String = "csv",
                                fileEncoding: String = "UTF-8") {

  val checkedFolderPath: String =
    IoUtils.prepareFolderPath(folderPath.toAbsolutePath.toString)

  val checkedFileExtension: String =
    IoUtils.getFileExtensionWithoutDot(fileExtension)

  implicit val ec: ExecutionContextExecutor = ExecutionContext.global

  /* Define the classes to read */
  private val classesToRead = Vector(
    (classOf[Coordinate], Coordinate.getFields),
    (classOf[ExternalNet], ExternalNet.getFields),
    (classOf[LineType], LineType.getFields),
    (classOf[Load], Load.getFields),
    (classOf[Node], Node.getFields),
    (classOf[RES], RES.getFields),
    (classOf[Transformer2W], Transformer2W.getFields),
    (classOf[Transformer2WType], Transformer2WType.getFields),
    (classOf[Transformer3W], Transformer3W.getFields),
    (classOf[Transformer3WType], Transformer3WType.getFields)
  )

  /**
    * Read all models and compose them
    *
    * @return //TODO: Specify
    */
  def read(): GridModel = {
    /* Reading the field to value maps for each of the specified classes
     *
     * Block is discouraged, but the following assembly of classes cannot be parallelized as well, therefore the
     * Await is okay here */
    val rawData = getFieldToValueMaps

    /* Create empty grid model */
    val gridModel = GridModel.apply()

    gridModel
  }

  /**
    * Reading all models of the specified class and returning them
    *
    * @param modelClass     Class of the models to read
    * @param desiredFields  The desired fields to get from file
    * @return               A Vector of maps with fields to values
    */
  private def read(modelClass: Class[_ <: SimbenchModel],
                   desiredFields: Array[String])
    : Future[(Class[_ <: SimbenchModel], Vector[Map[String, String]])] =
    Future {
      /* Determine the matching file name */
      SimbenchFileNamingStrategy.getFileName(modelClass) match {
        case Failure(exception) =>
          throw IoException(
            s"Cannot read the data set, as the file name for $modelClass can not cannot be determined",
            exception)
        case Success(filename) =>
          val csvReader =
            CsvReader(IoUtils.composeFullyQualifiedPath(checkedFolderPath,
                                                        filename,
                                                        checkedFileExtension),
                      separator,
                      fileExtension,
                      fileEncoding)

          /* De-serialise the files */
          modelClass -> csvReader.read(desiredFields)
      }
    }

  /**
    * Get the field to value maps for all of the specified classes to read.
    *
    * @return A map of model class to a vector of maps from field to value
    */
  private def getFieldToValueMaps
    : Map[Class[_ <: SimbenchModel], Vector[Map[String, String]]] = {
    Await
      .result(Future.sequence(for ((clazz, fields) <- classesToRead) yield {
        read(clazz, fields)
      }), Duration("10 s"))
      .toMap
  }
}
