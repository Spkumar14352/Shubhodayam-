package com.example.utilities.calc

enum class UnitCategory(val en: String, val te: String) {
    LENGTH("Length", "పొడవు"),
    WEIGHT("Weight", "బరువు"),
    VOLUME("Volume", "ఘనపరిమాణం"),
    TEMPERATURE("Temperature", "ఉష్ణోగ్రత"),
    SPEED("Speed", "వేగం"),
    AREA("Area", "విస్తీర్ణం")
}

/** [toBase] is how many base units (m, kg, L, °C, m/s, m²) one of this unit is; temperature is special-cased. */
data class MeasureUnit(val id: String, val en: String, val te: String, val toBase: Double)

object UnitConverter {
    const val TOLA_GRAMS = 11.6638
    /** A traditional seer is 80 tolas. */
    const val SEER_GRAMS = 80 * TOLA_GRAMS
    const val SQ_FT_IN_SQ_M = 0.09290304
    const val FOOT_INCH = "ft_in"

    val units: Map<UnitCategory, List<MeasureUnit>> = mapOf(
        UnitCategory.LENGTH to listOf(
            MeasureUnit("mm", "Millimetre", "మిల్లీమీటర్", 0.001),
            MeasureUnit("cm", "Centimetre", "సెంటీమీటర్", 0.01),
            MeasureUnit("m", "Metre", "మీటర్", 1.0),
            MeasureUnit("km", "Kilometre", "కిలోమీటర్", 1000.0),
            MeasureUnit("in", "Inch", "అంగుళం", 0.0254),
            MeasureUnit("ft", "Foot", "అడుగు", 0.3048),
            MeasureUnit(FOOT_INCH, "Feet + inches", "అడుగులు + అంగుళాలు", 0.3048),
            MeasureUnit("yd", "Yard (gajam)", "గజం", 0.9144),
            MeasureUnit("mi", "Mile", "మైలు", 1609.344)
        ),
        UnitCategory.WEIGHT to listOf(
            MeasureUnit("mg", "Milligram", "మిల్లీగ్రాము", 0.000001),
            MeasureUnit("g", "Gram", "గ్రాము", 0.001),
            MeasureUnit("tola", "Tola", "తులం", TOLA_GRAMS / 1000),
            MeasureUnit("kg", "Kilogram", "కిలో", 1.0),
            MeasureUnit("seer", "Seer (80 tola)", "సేరు (80 తులాలు)", SEER_GRAMS / 1000),
            MeasureUnit("quintal", "Quintal", "క్వింటాలు", 100.0),
            MeasureUnit("tonne", "Tonne", "టన్ను", 1000.0),
            MeasureUnit("oz", "Ounce", "ఔన్స్", 0.028349523125),
            MeasureUnit("lb", "Pound", "పౌండ్", 0.45359237)
        ),
        UnitCategory.VOLUME to listOf(
            MeasureUnit("ml", "Millilitre", "మిల్లీలీటర్", 0.001),
            MeasureUnit("l", "Litre", "లీటర్", 1.0),
            MeasureUnit("m3", "Cubic metre", "ఘనపు మీటర్", 1000.0),
            MeasureUnit("cup", "Cup (240 ml)", "కప్పు (240 మి.లీ)", 0.24),
            MeasureUnit("gal_us", "Gallon (US)", "గ్యాలన్ (US)", 3.785411784),
            MeasureUnit("gal_uk", "Gallon (UK)", "గ్యాలన్ (UK)", 4.54609),
            MeasureUnit("ft3", "Cubic foot", "ఘనపు అడుగు", 28.316846592)
        ),
        UnitCategory.TEMPERATURE to listOf(
            MeasureUnit("c", "Celsius", "సెల్సియస్", 1.0),
            MeasureUnit("f", "Fahrenheit", "ఫారెన్‌హీట్", 1.0),
            MeasureUnit("k", "Kelvin", "కెల్విన్", 1.0)
        ),
        UnitCategory.SPEED to listOf(
            MeasureUnit("kmh", "km per hour", "కి.మీ/గంట", 1 / 3.6),
            MeasureUnit("ms", "metres per second", "మీ/సెకను", 1.0),
            MeasureUnit("mph", "miles per hour", "మైళ్లు/గంట", 0.44704),
            MeasureUnit("knot", "Knot", "నాట్", 0.514444)
        ),
        UnitCategory.AREA to listOf(
            MeasureUnit("sqft", "Square foot", "చదరపు అడుగు", SQ_FT_IN_SQ_M),
            MeasureUnit("sqm", "Square metre", "చదరపు మీటర్", 1.0),
            MeasureUnit("sqyd", "Square yard (gajam)", "చదరపు గజం", 0.83612736),
            MeasureUnit("ankanam", "Ankanam (72 sq ft)", "అంకణం (72 చ.అ)", 72 * SQ_FT_IN_SQ_M),
            MeasureUnit("cent", "Cent (435.6 sq ft)", "సెంటు (435.6 చ.అ)", 435.6 * SQ_FT_IN_SQ_M),
            MeasureUnit("guntha", "Guntha (1089 sq ft)", "గుంట (1089 చ.అ)", 1089 * SQ_FT_IN_SQ_M),
            MeasureUnit("acre", "Acre", "ఎకరం", 43560 * SQ_FT_IN_SQ_M),
            MeasureUnit("hectare", "Hectare", "హెక్టారు", 10000.0)
        )
    )

    fun unit(category: UnitCategory, id: String) = units.getValue(category).first { it.id == id }

    fun convert(value: Double, category: UnitCategory, from: String, to: String): Double {
        if (category == UnitCategory.TEMPERATURE) {
            val celsius = when (from) {
                "f" -> (value - 32) * 5 / 9
                "k" -> value - 273.15
                else -> value
            }
            return when (to) {
                "f" -> celsius * 9 / 5 + 32
                "k" -> celsius + 273.15
                else -> celsius
            }
        }
        return value * unit(category, from).toBase / unit(category, to).toBase
    }

    /** Feet + inches to feet. */
    fun feetInches(feet: Double, inches: Double) = feet + inches / 12.0

    /** Feet to a "5 ft 7.2 in" split. */
    fun toFeetInches(feet: Double): Pair<Int, Double> {
        val whole = feet.toInt()
        return whole to (feet - whole) * 12.0
    }
}
