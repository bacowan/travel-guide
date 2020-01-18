package travelGuide.restResponses

data class InterestPoint(
    val id: String,
    val name: String,
    val sub_name: String,
    val lat: Double,
    val lon: Double,
    val approved: Boolean,
    val descriptions: List<InterestPointDescription>
)