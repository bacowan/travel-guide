package travelGuide

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.geo.Distance
import org.springframework.data.geo.Metrics
import org.springframework.data.geo.Point
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import travelGuide.collections.InterestPoint
import travelGuide.repositories.InterestPointRepository
import travelGuide.repositories.UserRepository
import java.util.concurrent.atomic.AtomicLong

@RestController
class GreetingController {
    @Autowired
    private lateinit var interestPointRepository: InterestPointRepository
    @Autowired
    private lateinit var userRepository: UserRepository

    @RequestMapping("/interest_points/near")
    fun interest_points_near(
        @RequestParam(value = "lat") lat: Double,
        @RequestParam(value = "lon") lon: Double,
        @RequestParam(value = "distance", defaultValue = "5") distance: Double): List<InterestPoint> {
        return interestPointRepository.findByLocationNear(
            Point(lat, lon), Distance(distance, Metrics.KILOMETERS)
        )
    }
}