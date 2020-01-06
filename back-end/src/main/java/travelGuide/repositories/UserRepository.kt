package travelGuide.repositories

import org.springframework.data.mongodb.repository.MongoRepository
import travelGuide.collections.User

interface UserRepository : MongoRepository<User?, String?>