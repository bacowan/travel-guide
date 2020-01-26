package travelGuide.repositories

import org.springframework.data.geo.Distance
import org.springframework.data.geo.Point
import org.springframework.data.mongodb.repository.MongoRepository
import travelGuide.collections.InterestPoint

interface InterestPointRepository : MongoRepository<InterestPoint?, String?>, InterestPointRepositoryCustom {
    fun existsByLocationNear(location: Point, distance: Distance): Boolean
}