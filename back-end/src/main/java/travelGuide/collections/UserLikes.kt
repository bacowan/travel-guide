package travelGuide.collections

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id

data class UserLike(
    val location: ObjectId,
    val tag: String,
    var liked: Boolean
)

data class UserLikes(
    @Id val id: String? = null,
    val user: ObjectId,
    val likedLocations: MutableList<UserLike>)