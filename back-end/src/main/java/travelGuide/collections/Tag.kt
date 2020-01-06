package travelGuide.collections

import org.springframework.data.annotation.Id

data class Tag(
    @Id val id: String,
    var name: String
)