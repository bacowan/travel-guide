package travelGuide.restResponses

data class Translation(
    val language: String,
    val name: String
)

data class Tag(
    val english: String,
    val translations: List<Translation>
)