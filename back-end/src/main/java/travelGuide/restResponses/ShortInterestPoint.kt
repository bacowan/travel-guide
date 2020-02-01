package travelGuide.restResponses

data class ShortInterestPoint(
    val id: String,
    val name: String,
    val sub_name: String,
    val lat: Double,
    val lon: Double
)