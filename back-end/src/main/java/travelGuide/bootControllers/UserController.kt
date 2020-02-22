package travelGuide.bootControllers

import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.annotation.JsonNaming
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*
import travelGuide.collections.User
import travelGuide.repositories.LanguageRepository
import travelGuide.repositories.UserRepository
import java.security.Principal


@RestController
open class UserController {
    @Autowired
    lateinit var passwordEncoder: PasswordEncoder

    @Autowired
    lateinit var repository: UserRepository

    @Autowired
    lateinit var languageRepository: LanguageRepository

    @GetMapping("/users/{id}")
    fun getUser(
        @PathVariable id: String): ResponseEntity<travelGuide.restResponses.User> {
        val user = repository.findByIdOrNull(id);
        return if (user != null) {
            if (languageRepository.existsByName(user.defaultLanguage)) {
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
                ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .build()
            }
        }
        else {
            ResponseEntity.status(HttpStatus.NOT_FOUND)
                .build()
        }
    }

    @PutMapping("/users/{id}")
    fun updateUser(
        @PathVariable id: String,
        @RequestBody parameters: UserPutBody): ResponseEntity<String> {

        val user = repository.findByIdOrNull(id);
        return if (user != null) {
            if (parameters.email != null) {
                user.email = parameters.email
            }
            if (parameters.defaultLanguage != null) {
                if (languageRepository.existsByName(parameters.defaultLanguage)) {
                    user.defaultLanguage = parameters.defaultLanguage
                }
                else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("The given language is not supported")
                }
            }
            if (parameters.defaultTags != null) {
                user.defaultTags = parameters.defaultTags
            }
            if (parameters.permissions != null) {
                user.permissions = parameters.permissions
            }
            repository.save(user)
            ResponseEntity.status(HttpStatus.OK)
                .body("Successfully updated user")
        }
        else {
            ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Could not find user with the given ID")
        }
    }

    @PostMapping("/users")
    fun newUser(@RequestBody parameters: UserPostBody): ResponseEntity<String> {
        // TODO: 1. Send validation email before enabling all features.
        // TODO: 2. there's a race condition here. If two of the same conditions are sent one after another,
        // TODO: two of the same user can be created.
        if (repository.existsByEmail(parameters.email)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                .body("User with the given email address already exists.")
        }
        else {
            val newUser = User(
                email = parameters.email,
                password = passwordEncoder.encode(parameters.password),
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
data class UserPostBody(
    val email: String,
    val password: String,
    val defaultLanguage: String)

@JsonNaming(PropertyNamingStrategy.KebabCaseStrategy::class)
data class UserPutBody(
    val email: String?,
    val defaultLanguage: String?,
    val permissions: List<String>?,
    val defaultTags: List<String>?)