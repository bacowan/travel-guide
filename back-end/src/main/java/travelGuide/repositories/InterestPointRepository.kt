package travelGuide.repositories

import org.springframework.data.domain.Pageable
import org.springframework.data.geo.Distance
import org.springframework.data.geo.Point
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import travelGuide.collections.InterestPoint

interface InterestPointRepository : MongoRepository<InterestPoint?, String?> {
    @Query()
    fun findByLocation(point: Point, distance: Distance, pageable: Pageable): List<InterestPoint>
}