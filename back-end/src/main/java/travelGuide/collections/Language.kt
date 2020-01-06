package travelGuide.collections

import org.springframework.data.annotation.Id

data class Language(
    @Id val id: String,
    var name: String
)