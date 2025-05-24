package de.hydrum.toonworld.util

import de.hydrum.toonworld.player.database.model.PlayerUnitAbility
import de.hydrum.toonworld.player.database.model.RelicTier
import de.hydrum.toonworld.unit.database.model.UnitAbility

open class Gain<T>(
    open val fromValue: T,
    open val toValue: T,
    val absGain: T,
    val pctGain: Double? = null
) {
    open fun hasChanged() = fromValue != toValue
    open fun changeText() = "" + formatValue(toValue) + if (hasChanged()) " (+${formatValue(absGain)})" else ""

    fun toDiffText(): String = "${formatValue(absGain)} | ${toPctText()}"
    fun toValueProgressText() = "${formatValue(fromValue)} | ${formatValue(toValue)} "

    fun toPctText(): String = (if (pctGain != null && fromValue != null) ((pctGain - 1) * 100).round(2).toString() else "---") + " %"

    open fun formatValue(value: T?): String = value?.toString() ?: "---"

    open fun fromValueString(): String = formatValue(fromValue)
    open fun toValueString(): String = formatValue(toValue)
    open fun absGainString(): String = formatValue(absGain)

    open fun sortByValue(): Comparable<*> = toValue as Comparable<*>
    open fun sortByChangeValue(): Comparable<*> = absGain as Comparable<*>
}

class GainRelicTier(
    override val fromValue: RelicTier?,
    override val toValue: RelicTier?,
) : Gain<RelicTier?>(
    fromValue = fromValue,
    toValue = toValue,
    absGain = if (toValue == null) toValue else RelicTier.entries.find { it -> it.ordinal == (toValue.ordinal - (fromValue?.ordinal ?: 2) + 2) }) {

    override fun hasChanged() = fromValue != toValue && toValue != null && toValue.ordinal > RelicTier.LOCKED.ordinal
    override fun changeText(): String {
        val toValueOrdinal = toValue?.ordinal ?: 0
        if (toValueOrdinal <= RelicTier.LOCKED.ordinal) {
            return ""
        }
        val change = RelicTier.TIER_1.ordinal.coerceAtLeast(toValue?.ordinal ?: 0) - RelicTier.TIER_0.ordinal.coerceAtLeast(fromValue?.ordinal ?: 0)
        return "" + toValue?.label + if (hasChanged()) " (+$change)" else ""
    }

    override fun sortByValue(): Comparable<*> = (if ((toValue?.ordinal ?: 0) < RelicTier.TIER_0.ordinal) 0 else toValue?.ordinal) as Comparable<*>
    override fun sortByChangeValue(): Comparable<*> = (if ((absGain?.ordinal ?: 0) <= RelicTier.TIER_0.ordinal) 0 else absGain?.ordinal) as Comparable<*>
}

class GainUnitAbility(
    val fromAbility: PlayerUnitAbility?,
    val toAbility: PlayerUnitAbility?,
    val ability: UnitAbility
) : Gain<Int?>(
    fromValue = fromAbility?.tier,
    toValue = toAbility?.tier,
    absGain = (toAbility?.tier ?: 0) - (fromAbility?.tier ?: 0)
) {

    override fun hasChanged() = hasZetaChanged() || hasOmicronChanged()

    fun getAbilityName() = ability.name

    fun hasZetaChanged() = fromAbility?.hasZeta != toAbility?.hasZeta && toAbility?.hasZeta == true
    fun hasOmicronChanged() = fromAbility?.hasOmicron != toAbility?.hasOmicron && toAbility?.hasOmicron == true
}

class GainLong(
    override val fromValue: Long?,
    override val toValue: Long?
) : Gain<Long?>(
    fromValue = fromValue,
    toValue = toValue,
    absGain = (toValue ?: 0L) - (fromValue ?: 0L),
    pctGain = if (fromValue == null || toValue == null || fromValue == 0L) null else toValue / fromValue.toDouble()
) {
    override fun absGainString(): String = (if (absGain != null && absGain > 0) "+" else "") + super.absGainString()
    override fun formatValue(value: Long?): String = value.abvFormatting()
}

class GainInt(
    override val fromValue: Int?,
    override val toValue: Int?
) : Gain<Int?>(
    fromValue = fromValue,
    toValue = toValue,
    absGain = (toValue ?: 0) - (fromValue ?: 0),
    pctGain = if (fromValue == null || toValue == null || fromValue == 0) null else toValue / fromValue.toDouble()
) {
    override fun absGainString(): String = (if (absGain != null && absGain > 0) "+" else "") + super.absGainString()
}

class GainDouble(
    override val fromValue: Double?,
    override val toValue: Double?
) : Gain<Double?>(
    fromValue = fromValue ?: 0.0,
    toValue = toValue ?: 0.0,
    absGain = (toValue ?: 0.0) - (fromValue ?: 0.0),
    pctGain = if (fromValue == null || toValue == null || fromValue == 0.0) null else toValue / fromValue.toDouble()
) {
    override fun absGainString(): String = (if (absGain != null && absGain > 0) "+" else "") + super.absGainString()
    override fun formatValue(value: Double?): String = value?.round(2).toString()
}

infix fun Double?.gainToDouble(to: Double?): Gain<Double?> = GainDouble(fromValue = this, toValue = to)
infix fun Int?.gainToInt(to: Int?): Gain<Int?> = GainInt(fromValue = this, toValue = to)
infix fun Int?.gainToLong(to: Int?): Gain<Long?> = GainLong(fromValue = this?.toLong(), toValue = to?.toLong())
infix fun Long?.gainToLong(to: Long?): Gain<Long?> = GainLong(fromValue = this, toValue = to)
infix fun String?.gainToString(to: String?): Gain<String?> = Gain(fromValue = this, toValue = to, absGain = to)
infix fun Boolean?.gainToBoolean(to: Boolean?): Gain<Boolean?> = Gain(fromValue = this, toValue = to, absGain = this != to && (to == true))
infix fun RelicTier?.gainToRelicTier(to: RelicTier): GainRelicTier = GainRelicTier(fromValue = this, toValue = to)

fun Long?.abvFormatting(): String {
    if (this == null) return "---"
    if (this > 10_000_000) return "%4.2f".format(this / 1_000_000.0) + "m"
    if (this > 1_000_000) return "%4.3f".format(this / 1_000_000.0) + "m"
    if (this > 100_000) return "%4.1f".format(this / 1_000.0) + "k"
    if (this > 10_000) return "%4.2f".format(this / 1_000.0) + "k"
    else return "%6d".format(this)
}

fun Int?.abvFormatting(): String {
    if (this == null) return "---"
    if (this > 10_000_000) return "%4.2f".format(this / 1_000_000.0) + "m"
    if (this > 1_000_000) return "%4.3f".format(this / 1_000_000.0) + "m"
    if (this > 100_000) return "%4.1f".format(this / 1_000.0) + "k"
    if (this > 10_000) return "%4.2f".format(this / 1_000.0) + "k"
    else return "%6d".format(this)
}