package travelGuide.collections

import org.springframework.data.annotation.Id

data class User(
    @Id val id: String? = null,
    var email: String,
    var defaultLanguage: String,
    var permissions: List<String> = listOf(),
    var defaultTags: List<String> = listOf())