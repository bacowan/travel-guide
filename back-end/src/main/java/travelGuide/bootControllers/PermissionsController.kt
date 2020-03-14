package travelGuide.bootControllers

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import travelGuide.collections.PermissionRequest
import travelGuide.enums.Permission
import travelGuide.repositories.UserRepository

@RestController
class PermissionsController {
    @Autowired
    private lateinit var userRepository: UserRepository

    @GetMapping("/permissions")
    fun getPermissions() : ResponseEntity<List<String>> {
        return ResponseEntity.status(HttpStatus.OK)
            .body(Permission.values().map { it.name })
    }

    @PutMapping("/permissions/{userId}")
    fun requestPermissions(
        @PathVariable userId: String,
        @RequestBody parameters: RequestPermissionParameters): ResponseEntity<String> {

        val user = userRepository.findByIdOrNull(userId)
        return checkPreconditions(
            notNullPrecondition(user, HttpStatus.NOT_FOUND, "Could not find the given user in the database"))
        {
            if (user != null) {
                user.permissionRequest = PermissionRequest(
                    parameters.permissions,
                    parameters.justification)

                userRepository.save(user)
                return@checkPreconditions ResponseEntity.status(HttpStatus.OK)
                    .body("Successfully requested permissions")
            }
            else {
                return@checkPreconditions ResponseEntity.status(HttpStatus.FORBIDDEN).body("something went wrong")
            }
        }
    }

    @PutMapping("/permissions/approve/{userId}")
    fun approvePermissions(
        @PathVariable userId: String,
        authentication: Authentication?): ResponseEntity<String> {

        val user = userRepository.findByIdOrNull(userId)
        val requestedPermissions = user?.permissionRequest
        val requestingUser = if (authentication != null) userRepository.findByIdOrNull(authentication.name) else null

        return checkPreconditions(
            notNullPrecondition(user, HttpStatus.NOT_FOUND, "Could not find the given user in the database"),
            notNullPrecondition(requestedPermissions, HttpStatus.FORBIDDEN, "The given user has no permission requests"),
            notNullPrecondition(requestingUser, HttpStatus.FORBIDDEN, "Could not find the the requesting user in the database"),
            Precondition({requestingUser?.permissions?.contains(Permission.Administrator.name) == true},
                HttpStatus.UNAUTHORIZED,
                "Only administrators can approve permission requests")
        )
        {
            if (user != null && requestedPermissions != null && requestingUser != null) {
                user.permissions = requestedPermissions.permissions
                user.permissionRequest = null
                userRepository.save(user)
                return@checkPreconditions ResponseEntity.status(HttpStatus.OK)
                    .body("Successfully updated permissions")
            }
            else {
                return@checkPreconditions ResponseEntity.status(HttpStatus.FORBIDDEN).body("something went wrong")
            }
        }
    }
}

data class RequestPermissionParameters(
    val permissions: List<String>,
    val justification: String
)