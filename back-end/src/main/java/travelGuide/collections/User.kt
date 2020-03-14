package travelGuide.collections

import org.springframework.data.annotation.Id

data class PermissionRequest(
    var permissions: List<String> = listOf(),
    var justification: String)

data class User(
    @Id val id: String? = null,
    var email: String,
    var password: String,
    var defaultLanguage: String,
    var permissions: List<String> = listOf(),
    var permissionRequest: PermissionRequest? = null,
    var defaultTags: List<String> = listOf())