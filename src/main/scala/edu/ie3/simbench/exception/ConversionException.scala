/*
 * © 2020. TU Dortmund University,
 * Institute of Energy Systems, Energy Efficiency and Energy Economics,
 * Research group Distribution grid planning and operation
*/
package edu.ie3.simbench.exception

final case class ConversionException(
    private val msg: String,
    private val cause: Throwable = None.orNull
) extends SimbenchException(msg, cause)
