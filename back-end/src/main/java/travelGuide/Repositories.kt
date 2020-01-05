package travelGuide

import org.springframework.data.geo.Distance
import org.springframework.data.geo.Point
import org.springframework.data.mongodb.repository.MongoRepository

interface InterestPointRepository : MongoRepository<InterestPoint?, String?> {
    fun findByLocationNear(point: Point, distance: Distance): List<InterestPoint>
}