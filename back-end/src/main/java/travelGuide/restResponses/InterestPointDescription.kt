package travelGuide.restResponses

data class InterestPointDescription(
    val tag: String,
    val likes: Int,
    val dislikes: Int,
    var submitter: String,
    val value: String
)