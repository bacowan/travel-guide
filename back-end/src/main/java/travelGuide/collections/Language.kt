package travelGuide.collections

import org.springframework.data.annotation.Id

data class Language(
    @Id
    val id: String? = null,
    var name: String
)