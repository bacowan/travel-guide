package travelGuide.bootControllers

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.geo.Distance
import org.springframework.data.geo.Metrics
import org.springframework.data.geo.Point
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import travelGuide.repositories.InterestPointRepository
import travelGuide.repositories.UserRepository
import travelGuide.restResponses.InterestPoint
import travelGuide.restResponses.ShortInterestPoint
import java.util.concurrent.atomic.AtomicLong

@RestController
class InterestPointController {
    @Autowired
    private lateinit var interestPointRepository: InterestPointRepository

    @GetMapping("/interest_points")
    fun interest_points(
            @RequestParam(value = "lat", required = true) lat: Double,
            @RequestParam(value = "lon", required = true) lon: Double,
            @RequestParam(value = "distance", defaultValue = "5") distance: Double,
            @RequestParam(value = "tags", defaultValue = "") tags: List<String>,
            @RequestParam(value = "limit", defaultValue = "50") limit: Int,
            @RequestParam(value = "offset", defaultValue = "0") offset: Int,
            @RequestParam(value = "language", defaultValue = "English") language: String): ResponseEntity<List<ShortInterestPoint>> {
        val interestPoints = interestPointRepository.findByLocation(
            lat = lat,
            lon = lon,
            distance = distance,
            tags = tags,
            approved = true,
            pageable = PageRequest.of(offset, limit)
            ).mapNotNull {
                if (it.id != null)
                    ShortInterestPoint(
                        id = it.id,
                        name = (it.name.firstOrNull { name -> name.language == language } ?: it.name.firstOrNull()) ?.value ?: "",
                        sub_name = (it.subName.firstOrNull { name -> name.language == language } ?: it.subName.firstOrNull()) ?.value ?: "",
                        lat = it.location.firstOrNull() ?: 0.0,
                        lon  = it.location.firstOrNull() ?: 0.0,
                        approved = it.approved)
                else null }

        return ResponseEntity.status(HttpStatus.OK)
            .body(interestPoints)
    }
}