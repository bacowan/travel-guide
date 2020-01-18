package travelGuide.restResponses

data class InterestPoint(
    val id: String,
    val name: List<TranslationText>,
    val sub_name: List<TranslationText>,
    val lat: Double,
    val lon: Double,
    val approved: Boolean,
    val descriptions: List<InterestPointDescription>
)