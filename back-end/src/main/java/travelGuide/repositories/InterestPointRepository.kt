package travelGuide.repositories

import org.springframework.data.domain.Pageable
import org.springframework.data.geo.Distance
import org.springframework.data.geo.Point
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.data.mongodb.repository.Query
import org.springframework.data.repository.query.Param
import travelGuide.collections.InterestPoint

interface InterestPointRepository : MongoRepository<InterestPoint?, String?>, InterestPointRepositoryCustom {
//    @Query("""{${'$'}and: [
//                    { location: { ${'$'}near: { ${'$'}geometry: { type: 'Point', coordinates: [ :lat, :lon ] }, ${'$'}maxDistance: :distance } }},
//                    { descriptions: { ${'$'}elemMatch: { tag: { ${'$'}in: [:tags] } } } },
//                    { approved: :approved}
//                ]}""")
//    fun findByLocation(
//        @Param("lat") lat: Double,
//        @Param("lon") lon: Double,
//        @Param("distance") distance: Double,
//        @Param("tags") tags: List<String>,
//        @Param("approved") approved: Boolean,
//        pageable: Pageable) : List<InterestPoint>
}