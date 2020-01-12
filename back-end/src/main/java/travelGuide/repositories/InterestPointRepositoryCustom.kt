package travelGuide.repositories

import org.springframework.data.domain.Pageable
import org.springframework.data.geo.Distance
import org.springframework.data.geo.Point
import travelGuide.collections.InterestPoint

interface InterestPointRepositoryCustom {
    fun findByLocation(point: Point, distance: Distance, tags: List<String>, pageable: Pageable): List<InterestPoint>
}