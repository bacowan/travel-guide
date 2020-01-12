package travelGuide.restResponses

data class InterestPointLike(
    val location_id: String,
    val tag: String,
    val liked: Boolean
)