package travelGuide.repositories

import org.springframework.data.domain.Pageable
import org.springframework.data.geo.Distance
import org.springframework.data.geo.Point
import travelGuide.collections.InterestPoint

interface InterestPointRepositoryCustom {
        fun findByLocation(
            lat: Double,
            lon: Double,
            distance: Double,
            tags: List<String>,
            approved: Boolean?,
            pageable: Pageable) : List<InterestPoint>
}