package edu.ie3.simbench.model.profile

import edu.ie3.simbench.exception.io.SimbenchDataModelException
import edu.ie3.simbench.model.RawModelData
import edu.ie3.simbench.model.datamodel.profiles.{
  PowerPlantProfile,
  PowerPlantProfileType
}
import edu.ie3.test.common.UnitSpec
import edu.ie3.util.TimeTools

class PowerPlantProfileSpec extends UnitSpec {
  TimeTools.initialize(TimeTools.DEFAULT_ZONE_ID,
                       TimeTools.DEFAULT_LOCALE,
                       "dd.MM.yyyy HH:mm")

  val rawData = Vector(
    RawModelData(classOf[PowerPlantProfile],
                 Map(
                   "time" -> "01.01.2016 00:00",
                   "pp_16" -> "0.7",
                   "pp_17" -> "0.7"
                 )),
    RawModelData(classOf[PowerPlantProfile],
                 Map(
                   "time" -> "01.01.2016 00:15",
                   "pp_16" -> "0.7",
                   "pp_17" -> "0.7"
                 ))
  )

  val expected = Vector(
    PowerPlantProfile(
      "PowerPlantProfile16",
      PowerPlantProfileType.PowerPlantProfile16,
      Map(
        TimeTools.toZonedDateTime("01.01.2016 00:00") -> BigDecimal(0.7),
        TimeTools.toZonedDateTime("01.01.2016 00:15") -> BigDecimal(0.7),
      )
    ),
    PowerPlantProfile(
      "PowerPlantProfile17",
      PowerPlantProfileType.PowerPlantProfile17,
      Map(
        TimeTools.toZonedDateTime("01.01.2016 00:00") -> BigDecimal(0.7),
        TimeTools.toZonedDateTime("01.01.2016 00:15") -> BigDecimal(0.7),
      )
    )
  )

  "The power plant profile object" should {
    "throw an exception, when the basic single model creation method is called" in {
      val thrown =
        intercept[SimbenchDataModelException](
          PowerPlantProfile.buildModel(rawData(0)))
      thrown.getMessage shouldBe "No basic implementation of model creation available for profiles"
    }

    "build a correct vector of models" in {
      val actual =
        PowerPlantProfile.buildModels(rawData)
      actual.toSet shouldBe expected.toSet
    }
  }
}