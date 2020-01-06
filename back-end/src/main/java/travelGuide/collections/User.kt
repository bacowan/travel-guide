package travelGuide.collections

import org.springframework.data.annotation.Id

data class User(
    @Id val id: String? = null,
    var name: String,
    var defaultLanguage: String,
    var permissions: Array<String>)