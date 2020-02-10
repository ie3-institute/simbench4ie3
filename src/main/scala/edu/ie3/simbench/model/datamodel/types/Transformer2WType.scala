package edu.ie3.simbench.model.datamodel.types

import edu.ie3.simbench.model.datamodel.SimbenchModel
import edu.ie3.simbench.model.datamodel.enums.BranchElementPort

/**
  * Modeling a type of a two winding transformer
  *
  * @param id Identifier
  * @param sR Rated apparent power in MVA
  * @param vmHV Rated voltage magnitude at high voltage side
  * @param vmLV Rated voltage magnitude at low voltage side
  * @param va0 Phase shifting based on the assembly style of the transformer (e.g. Dy5 => 150°) in °
  * @param vmImp Relative voltage magnitude in the short circuit experiment in %
  * @param pCu Copper losses in short circuit experiment in kW
  * @param pFe Iron losses in no load experiment in kW
  * @param iNoLoad No load current in no load experiment in %
  * @param tapable Is the transformer tapable?
  * @param tapside Winding at which side the transformer is installed
  * @param dVm Voltage magnitude increase per step in p.u. / step
  * @param dVa Voltage angle increase per step in ° / step
  * @param tapNeutr Neutral tap position
  * @param tapMin Minimum permissible tap position
  * @param tapMax Maximum permissible tap position
  */
case class Transformer2WType(id: String,
                             sR: BigDecimal,
                             vmHV: BigDecimal,
                             vmLV: BigDecimal,
                             va0: BigDecimal,
                             vmImp: BigDecimal,
                             pCu: BigDecimal,
                             pFe: BigDecimal,
                             iNoLoad: BigDecimal,
                             tapable: Boolean,
                             tapside: BranchElementPort,
                             dVm: BigDecimal,
                             dVa: BigDecimal,
                             tapNeutr: Int,
                             tapMin: Int,
                             tapMax: Int)
    extends SimbenchModel