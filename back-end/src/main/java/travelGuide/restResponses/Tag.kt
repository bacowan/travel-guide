package travelGuide.restResponses

data class Translation(
    val language: String,
    val name: String
)

data class Tag(
    val translations: List<Translation>
)