package travelGuide.bootControllers

import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.annotation.JsonNaming
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import travelGuide.repositories.InterestPointRepository
import travelGuide.repositories.LanguageRepository
import travelGuide.repositories.UserLikesRepository
import travelGuide.repositories.UserRepository
import travelGuide.restResponses.InterestPoint
import travelGuide.restResponses.UserLike

@RestController
class UserLikesController {
    @Autowired
    private lateinit var userLikesRepository: UserLikesRepository
    @Autowired
    private lateinit var userRepository: UserRepository
    @Autowired
    private lateinit var interestPointRepository: InterestPointRepository

    @GetMapping("/user_likes/{id}")
    fun getUserLikesByUserId(
        @PathVariable id: String): ResponseEntity<List<UserLike>> {
        val likes = userLikesRepository.findByUser(ObjectId(id))
        return if (likes.any()) {
            val ret = likes.flatMap {
                it.likedLocations.map {
                    like -> UserLike(
                        like.location.toHexString(),
                        like.tag,
                        like.liked)
                }
            }
            ResponseEntity.status(HttpStatus.OK)
                .body(ret)
        }
        else {
            ResponseEntity.status(HttpStatus.NOT_FOUND)
                .build()
        }
    }

    @GetMapping("/user_likes/{userId}/locations/{locationId}")
    fun getUserLikesByUserId(
        @PathVariable userId: String,
        @PathVariable locationId: String): ResponseEntity<List<UserLike>> {
        val likes = userLikesRepository.findByUser(ObjectId(userId))
        return if (likes.any()) {
            val ret = likes.mapNotNull {
                val location = it.likedLocations.firstOrNull { like -> like.location.toHexString() == locationId }
                if (location != null) {
                    UserLike(
                        location.location.toHexString(),
                        location.tag,
                        location.liked
                    )
                }
                else {
                    null
                }
            }
            if (ret.any()) {
                ResponseEntity.status(HttpStatus.OK)
                    .body(ret)
            }
            else {
                ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .build()
            }
        }
        else {
            ResponseEntity.status(HttpStatus.NOT_FOUND)
                .build()
        }
    }

    // TODO: this method isn't very thread safe. You could have two requests for the same
    // TODO: location by different people, and it wouldn't increment correctly. It also
    // TODO: isn't very transactional: it could break between the two saves.
    @PutMapping("/user_likes/{userId}/locations/{locationId}")
    fun putUserLikesByUserId(
        @PathVariable userId: String,
        @PathVariable locationId: String,
        @RequestBody parameters: UserLikeBody): ResponseEntity<String> {

        val interestPoint = interestPointRepository.findByIdOrNull(locationId)
            ?: return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Provided interest point does not exist")

        val description = interestPoint.descriptions.firstOrNull { it.tag == parameters.tag }
            ?: return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("Provided interest point does not contain the given tag")

        val userObjectId = ObjectId(userId)

        var likes = userLikesRepository.findByUser(userObjectId).firstOrNull()
        if (likes == null) {
            if (!userRepository.existsById(userId)) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Provided user does not exist")
            }
            else {
                likes = travelGuide.collections.UserLikes(
                    user = userObjectId,
                    likedLocations = mutableListOf()
                )
            }
        }

        val locationObjectId = ObjectId(locationId)
        var like = likes.likedLocations.firstOrNull {
            it.location == locationObjectId
        }

        if (like == null) {
            like = travelGuide.collections.UserLike(
                locationObjectId,
                parameters.tag,
                parameters.like
            )
            likes.likedLocations.add(like)
            if (parameters.like) {
                description.likes++
            }
            else {
                description.dislikes++
            }
        }
        else {
            if (like.liked != parameters.like) {
                if (parameters.like) {
                    description.dislikes--
                    description.likes++
                }
                else {
                    description.dislikes++
                    description.likes--
                }
                like.liked = parameters.like
            }
        }

        userLikesRepository.save(likes)
        interestPointRepository.save(interestPoint)

        return ResponseEntity.status(HttpStatus.OK)
            .body("Successfully updated like")
    }
}

@JsonNaming(PropertyNamingStrategy.KebabCaseStrategy::class)
data class UserLikeBody(
    val tag: String,
    val like: Boolean)