package com.example.utilities.calc

enum class BmiCategory(val en: String, val te: String) {
    UNDERWEIGHT("Underweight", "తక్కువ బరువు"),
    NORMAL("Healthy weight", "ఆరోగ్యకరమైన బరువు"),
    OVERWEIGHT("Overweight", "అధిక బరువు"),
    OBESE("Obese", "స్థూలకాయం")
}

data class BmiResult(
    val bmi: Double,
    val category: BmiCategory,
    val healthyMinKg: Double,
    val healthyMaxKg: Double,
    /** Positive: kg to lose to reach the healthy range; negative: kg to gain; 0 when inside it. */
    val changeToHealthyKg: Double
)

/** WHO adult BMI bands. Healthy range is BMI 18.5–24.9 for the given height. */
object BmiCalculator {
    const val HEALTHY_MIN = 18.5
    const val HEALTHY_MAX = 24.9

    fun heightCm(feet: Double, inches: Double) = (feet * 12 + inches) * 2.54

    fun calculate(weightKg: Double, heightCm: Double): BmiResult {
        require(weightKg > 0 && heightCm > 0)
        val metres = heightCm / 100.0
        val bmi = weightKg / (metres * metres)
        val minKg = HEALTHY_MIN * metres * metres
        val maxKg = HEALTHY_MAX * metres * metres
        val category = when {
            bmi < HEALTHY_MIN -> BmiCategory.UNDERWEIGHT
            bmi < 25.0 -> BmiCategory.NORMAL
            bmi < 30.0 -> BmiCategory.OVERWEIGHT
            else -> BmiCategory.OBESE
        }
        val change = when {
            weightKg > maxKg -> weightKg - maxKg
            weightKg < minKg -> weightKg - minKg
            else -> 0.0
        }
        return BmiResult(bmi, category, minKg, maxKg, change)
    }
}
