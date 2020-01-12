package travelGuide.collections

import org.springframework.data.annotation.Id

data class Translation(
    var name: String,
    var language: String
)

data class Tag(
    @Id val id: String,
    var translations: List<Translation>
)