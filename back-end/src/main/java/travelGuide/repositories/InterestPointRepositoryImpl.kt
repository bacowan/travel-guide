package travelGuide.repositories

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Pageable
import org.springframework.data.geo.Circle
import org.springframework.data.geo.Point
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import travelGuide.collections.InterestPoint

class InterestPointRepositoryImpl : InterestPointRepositoryCustom {

    @Autowired
    lateinit var mongoTemplate: MongoTemplate

    override fun findByLocation(
            lat: Double,
            lon: Double,
            distance: Double,
            tags: List<String>,
            approved: Boolean?,
            pageable: Pageable) : List<InterestPoint> {

        val query = Query()
        if (approved != null) {
            query.addCriteria(Criteria.where("approved").`is`(approved))
        }
        if (tags.any()) {
            query.addCriteria(Criteria.where("descriptions").elemMatch(
                Criteria.where("tag").`in`(tags)))
        }
        query.addCriteria(Criteria.where("location").withinSphere(Circle(Point(lat, lon), distance)))

        return mongoTemplate.find(query, InterestPoint::class.java)
    }
}