package travelGuide.bootControllers

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.geo.Distance
import org.springframework.data.geo.Metrics
import org.springframework.data.geo.Point
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import travelGuide.collections.Tag
import travelGuide.repositories.InterestPointRepository
import travelGuide.repositories.TagRepository
import travelGuide.repositories.UserRepository
import travelGuide.restResponses.InterestPoint
import travelGuide.restResponses.InterestPointDescription
import travelGuide.restResponses.ShortInterestPoint
import travelGuide.restResponses.TranslationText
import java.util.concurrent.atomic.AtomicLong

@RestController
class InterestPointController {
    @Autowired
    private lateinit var interestPointRepository: InterestPointRepository
    @Autowired
    private lateinit var tagRepository: TagRepository

    @GetMapping("/interest_points")
    fun getInterestPoints(
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

    @GetMapping("/interest_points/{id}")
    fun getInterestPointById(
        @PathVariable id: String,
        @RequestParam(value = "language", defaultValue = "English") language: String): ResponseEntity<InterestPoint> {
        val interestPoint = interestPointRepository.findByIdOrNull(id)
        return if (interestPoint?.id != null) {
            val tags = if (language != "English") {
                getAllTags()
            }
            else {
                null
            }

            val response = InterestPoint(
                id = interestPoint.id,
                name = interestPoint.name.firstOrNull { it.language == language }?.value
                    ?: interestPoint.name.firstOrNull { it.language == "English" }?.value
                    ?: "",
                sub_name = interestPoint.subName.firstOrNull { it.language == language }?.value
                    ?: interestPoint.subName.firstOrNull { it.language == "English" }?.value
                    ?: "",
                lat = interestPoint.location.firstOrNull() ?: 0.0,
                lon = interestPoint.location.elementAtOrNull(1) ?: 0.0,
                approved = interestPoint.approved,
                descriptions = toInterestPointDescriptionResponse(
                    tags,
                    interestPoint.descriptions,
                    language)
                )

            return ResponseEntity.status(HttpStatus.OK)
                .body(response)
        }
        else {
            ResponseEntity.status(HttpStatus.NOT_FOUND)
                .build()
        }
    }

    @Cacheable
    private fun getAllTags(): List<Tag> {
        return tagRepository.findAll()
    }

    @GetMapping("/interest_points/{id}/descriptions")
    fun getInterestPointDescriptions(
        @PathVariable id: String,
        @RequestParam(value = "language", defaultValue = "English") language: String): ResponseEntity<List<InterestPointDescription>> {

        val tags = if (language != "English") {
            getAllTags()
        }
        else {
            null
        }

        val interestPoint = interestPointRepository.findByIdOrNull(id)
        return if (interestPoint != null) {
            val response = toInterestPointDescriptionResponse(
                tags,
                interestPoint.descriptions,
                language)
            ResponseEntity.status(HttpStatus.OK)
                .body(response)
        }
        else {
            ResponseEntity.status(HttpStatus.NOT_FOUND)
                .build()
        }
    }

    private fun toInterestPointDescriptionResponse(
        tags: List<Tag>?,
        descriptions: List<travelGuide.collections.InterestPointDescription>,
        language: String): List<InterestPointDescription> {
        return descriptions.map {
            InterestPointDescription(
                tag = if (tags != null)
                    tags.firstOrNull { tag -> tag.english == it.tag }?.translations?.firstOrNull { trans -> trans.language == language }?.name
                        ?: it.tag
                else it.tag,
                likes = it.likes,
                dislikes = it.dislikes,
                submitter = it.submitter,
                value = it.values.firstOrNull { value -> value.language == language }?.value
                    ?: it.values.firstOrNull { value -> value.language == "English" }?.value
                    ?: ""
            )
        }
    }

}