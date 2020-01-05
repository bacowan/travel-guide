package travelGuide

import org.springframework.data.mongodb.repository.MongoRepository

interface InterestPointRepository : MongoRepository<InterestPoint?, String?> {
    fun findByFirstName(firstName: String?): InterestPoint?
    fun findByLastName(lastName: String?): List<InterestPoint?>?
}