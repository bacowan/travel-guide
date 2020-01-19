package travelGuide.repositories

import org.bson.types.ObjectId
import org.springframework.data.mongodb.repository.MongoRepository
import travelGuide.collections.UserLikes

interface UserLikesRepository : MongoRepository<UserLikes?, String?> {
    fun findByUser(user: ObjectId) : List<UserLikes>
}