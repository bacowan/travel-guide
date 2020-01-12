package travelGuide.collections

import org.springframework.data.annotation.Id

data class User(
    @Id val id: String? = null,
    var email: String,
    var defaultLanguage: String,
    var permissions: Array<String> = arrayOf(),
    var defaultTags: Array<String> = arrayOf())