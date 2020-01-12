package travelGuide.bootControllers

import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.annotation.JsonNaming
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import travelGuide.collections.User
import travelGuide.repositories.UserRepository

@RestController
class UserController {
    @Autowired
    private lateinit var repository: UserRepository

    @GetMapping("/users/{id}")
    fun getUser(@PathVariable id: String): ResponseEntity<travelGuide.restResponses.User> {
        val user = repository.findByIdOrNull(id);
        return if (user != null) {
            val responseUser = travelGuide.restResponses.User(
                id = user.id ?: "",
                email = user.email,
                defaultLanguage = user.defaultLanguage,
                permissions = user.permissions.toList(),
                defaultTags = user.defaultTags.toList()
            )
            ResponseEntity.status(HttpStatus.OK)
                .body(responseUser)
        }
        else {
            ResponseEntity.status(HttpStatus.NOT_FOUND)
                .build()
        }
    }

    @PostMapping("/users")
    fun newUser(@RequestBody parameters: UserPostBody): ResponseEntity<String> {
        if (repository.existsByEmail(parameters.email)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body("User with the given email address already exists.")
        }
        else {
            val newUser = User(
                email = parameters.email,
                defaultLanguage = parameters.defaultLanguage
            )
            val savedValue = repository.save(newUser)

            return if (savedValue.id == null) {
                ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Database refused to create user.")
            } else {
                ResponseEntity.status(HttpStatus.CREATED)
                    .body(savedValue.id)
            }
        }
    }
}

@JsonNaming(PropertyNamingStrategy.KebabCaseStrategy::class)
data class UserPostBody(val email: String, val defaultLanguage: String)