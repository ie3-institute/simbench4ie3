package edu.ie3.simbench.model.datamodel

import edu.ie3.simbench.exception.io.SimbenchDataModelException
import edu.ie3.simbench.model.RawModelData
import edu.ie3.simbench.model.datamodel.SimbenchModel.SimbenchCompanionObject
import edu.ie3.simbench.model.datamodel.types.LineType
import edu.ie3.simbench.model.datamodel.types.LineType.{ACLineType, DCLineType}

sealed trait Line[T <: LineType] extends EntityModel {
  val nodeA: Node
  val nodeB: Node
  val lineType: T
  val length: BigDecimal
  val loadingMax: BigDecimal
}

object Line extends SimbenchCompanionObject[Line[_ <: LineType]] {

  /**
    * AC line model
    *
    * @param id         Identifier
    * @param nodeA      The node at end A
    * @param nodeB      The node at end B
    * @param lineType   The line type
    * @param length     Length of the line in km
    * @param loadingMax Maximum permissible loading in %
    * @param subnet     Subnet it belongs to
    * @param voltLvl    Voltage level
    */
  case class ACLine(id: String,
                    nodeA: Node,
                    nodeB: Node,
                    lineType: ACLineType,
                    length: BigDecimal,
                    loadingMax: BigDecimal,
                    subnet: String,
                    voltLvl: Int)
      extends Line[ACLineType]

  /**
    * DC line model
    *
    * @param id         Identifier
    * @param nodeA      The node at end A
    * @param nodeB      The node at end B
    * @param lineType   The line type
    * @param length     Length of the line in km
    * @param loadingMax Maximum permissible loading in %
    * @param subnet     Subnet it belongs to
    * @param voltLvl    Voltage level
    */
  case class DCLine(id: String,
                    nodeA: Node,
                    nodeB: Node,
                    lineType: DCLineType,
                    length: BigDecimal,
                    loadingMax: BigDecimal,
                    subnet: String,
                    voltLvl: Int)
      extends Line[DCLineType]

  val NODE_A = "nodeA"
  val NODE_B = "nodeB"
  val LINE_TYPE = "type"
  val LENGTH = "length"
  val LOADING_MAX = "loadingMax"

  /**
    * Get an Array of table fields denoting the mapping to the model's attributes
    *
    * @return Array of table headings
    */
  override def getFields: Array[String] =
    Array(SimbenchModel.ID,
          NODE_A,
          NODE_B,
          LINE_TYPE,
          LENGTH,
          LOADING_MAX,
          EntityModel.SUBNET,
          EntityModel.VOLT_LVL)

  /**
    * Factory method to build one model from a mapping from field id to value
    *
    * @param rawData mapping from field id to value
    * @return A model
    */
  override def buildModel(rawData: RawModelData): Line[_ <: LineType] =
    throw SimbenchDataModelException(
      s"No basic implementation of model creation available for ${this.getClass.getSimpleName}")

  /**
    * Factory method to build a batch of models from a mapping from field id to value
    *
    * @param rawData    mapping from field id to value
    * @param nodes      Nodes to use for mapping
    * @param lineTypes  Line types to use for mapping
    * @return A [[Vector]] of models
    */
  def buildModels(
      rawData: Vector[RawModelData],
      nodes: Map[String, Node],
      lineTypes: Map[String, LineType]): Vector[Line[_ <: LineType]] =
    for (entry <- rawData) yield {
      val nodeA = nodes.getOrElse(
        entry.get(NODE_A),
        throw SimbenchDataModelException(
          s"Cannot build ${this.getClass.getSimpleName}, as suitable reference to $NODE_A cannot be found."))
      val nodeB = nodes.getOrElse(
        entry.get(NODE_B),
        throw SimbenchDataModelException(
          s"Cannot build ${this.getClass.getSimpleName}, as suitable reference to $NODE_B cannot be found."))
      val lineType = lineTypes.getOrElse(
        entry.get(LINE_TYPE),
        throw SimbenchDataModelException(
          s"Cannot build ${this.getClass.getSimpleName}, as suitable reference to $LINE_TYPE cannot be found.")
      )

      buildModel(entry, nodeA, nodeB, lineType)
    }

  /**
    * Factory method to build one model from a mapping from field id to value
    *
    * @param rawData mapping from field id to value
    * @param nodeA    Node to use at port A
    * @param nodeB    Node to use at port B
    * @param lineType Line type to use for this line
    * @return A model
    */
  def buildModel(rawData: RawModelData,
                 nodeA: Node,
                 nodeB: Node,
                 lineType: LineType): Line[_ <: LineType] = {
    val id = rawData.get(SimbenchModel.ID)
    val subnet = rawData.get(EntityModel.SUBNET)
    val voltLvl = rawData.get(EntityModel.VOLT_LVL).toInt
    val length = BigDecimal(rawData.get(LENGTH))
    val loadingMax = BigDecimal(rawData.get(LOADING_MAX))

    lineType match {
      case acLineType: ACLineType =>
        ACLine(id,
               nodeA,
               nodeB,
               acLineType,
               length,
               loadingMax,
               subnet,
               voltLvl)
      case dcLineType: DCLineType =>
        DCLine(id,
               nodeA,
               nodeB,
               dcLineType,
               length,
               loadingMax,
               subnet,
               voltLvl)
    }
  }
}
