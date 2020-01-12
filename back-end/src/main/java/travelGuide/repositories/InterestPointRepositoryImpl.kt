package travelGuide.repositories

import org.springframework.data.domain.Pageable
import org.springframework.data.geo.Distance
import org.springframework.data.geo.Point
import travelGuide.collections.InterestPoint

class InterestPointRepositoryImpl : InterestPointRepositoryCustom {
    override fun findByLocation(point: Point, distance: Distance, tags: List<String>, pageable: Pageable): List<InterestPoint> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}