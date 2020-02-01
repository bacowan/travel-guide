package travelGuide.bootControllers

import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.annotation.JsonNaming
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.PageRequest
import org.springframework.data.geo.Distance
import org.springframework.data.geo.Metrics
import org.springframework.data.geo.Point
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import travelGuide.collections.InterestPointRequest
import travelGuide.collections.Tag
import travelGuide.collections.TranslationText
import travelGuide.repositories.*
import travelGuide.restResponses.InterestPoint
import travelGuide.restResponses.InterestPointDescription
import travelGuide.restResponses.ShortInterestPoint

@RestController
class InterestPointController {
    @Autowired
    private lateinit var interestPointRepository: InterestPointRepository
    @Autowired
    private lateinit var interestPointRequestsRepository: InterestPointRequestsRepository
    @Autowired
    private lateinit var tagRepository: TagRepository
    @Autowired
    private lateinit var languageRepository: LanguageRepository
    @Autowired
    private lateinit var userRepository: UserRepository

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
                        lon  = it.location.firstOrNull() ?: 0.0)
                else null }

        return ResponseEntity.status(HttpStatus.OK)
            .body(interestPoints)
    }

    @PostMapping("/interest_point_requests")
    fun addInterestPoint(
        @RequestBody parameters: NewInterestPointBody,
        authentication: Authentication?): ResponseEntity<String> {

        val tooClose = isTooClose(parameters.lat, parameters.lon)
        return if (!tooClose) {
            val user = if (authentication != null) userRepository.findByIdOrNull(authentication.name) else null
            if (user != null) {
                val interestPoint = travelGuide.collections.InterestPointRequest(
                    location = listOf(parameters.lat, parameters.lon),
                    name = listOf(TranslationText(user.defaultLanguage, parameters.name)),
                    subName = if (parameters.subName != null)
                        listOf(TranslationText(user.defaultLanguage, parameters.subName))
                    else listOf(),
                    descriptions = mutableListOf(),
                    submitter = ObjectId(user.id)
                )
                val savedInterestPoint = interestPointRequestsRepository.save(interestPoint)
                ResponseEntity.status(HttpStatus.CREATED)
                    .body(savedInterestPoint.id)
            }
            ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("Could not find the given logged in user")
        }
        else {
            ResponseEntity.status(HttpStatus.CONFLICT)
                .body("There is already an interest point within 5 meters of this one")
        }
    }

    private fun isTooClose(lat: Double, lon: Double): Boolean {
        return interestPointRepository.existsByLocationNear(
            Point(lat, lon),
            Distance(0.005, Metrics.KILOMETERS) // TODO: Don't hard code "5m"
        )
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

    @PutMapping("interest_point_requests/{id}")
    fun updateInterestPointRequest(
        @PathVariable id: String,
        @RequestBody parameters: UpdateInterestPointBody,
        authentication: Authentication?) : ResponseEntity<String> {

        val interestPoint = interestPointRequestsRepository.findByIdOrNull(id)
        return if (interestPoint != null) {
            val user = if (authentication != null) userRepository.findByIdOrNull(authentication.name) else null
            if (user != null) {
                if (interestPoint.submitter.toHexString() == user.id || user.permissions.contains("Approver")) {
                    if (!isTooClose(parameters.lat, parameters.lon)) {
                        if (!parameters.approved) {
                            updateInterestPointRequest(interestPoint, parameters)
                        }
                        else if (user.permissions.contains("Approver")) {
                            approveInterestPoint(interestPoint, parameters)
                        }
                        else {
                            ResponseEntity.status(HttpStatus.FORBIDDEN)
                                .body("Only 'Approver's can approve interest points.")
                        }
                    }
                    else {
                        // TODO: race condition: the check is done on two, then the approval is done on both
                        ResponseEntity.status(HttpStatus.CONFLICT)
                            .body("There is already an interest point within 5 meters of this one")
                    }
                }
                else {
                    ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Only 'Approver's and the creating user can update unapproved interest points.")
                }
            }
            else {
                ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Could not find the given logged in user")
            }
        }
        else {
            ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Interest point not found")
        }
    }

    private fun updateInterestPointRequest(
        interestPointRequest: InterestPointRequest,
        parameters: UpdateInterestPointBody): ResponseEntity<String> {

        interestPointRequest.location = listOf(parameters.lat, parameters.lon)
        interestPointRequest.name = parameters.name
        interestPointRequest.subName = parameters.subName
    }

    private fun approveInterestPoint(
        interestPointRequest: InterestPointRequest,
        parameters: UpdateInterestPointBody): ResponseEntity<String> {

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
                submitter = it.submitter.toString(),
                value = it.values.firstOrNull { value -> value.language == language }?.value
                    ?: it.values.firstOrNull { value -> value.language == "English" }?.value
                    ?: ""
            )
        }
    }

    @PutMapping("/interest_points/{id}/descriptions")
    fun putDescription(
        @PathVariable id: String,
        @RequestBody parameters: InterestPointDescriptionBody): ResponseEntity<String> {

        val interestPoint = interestPointRepository.findByIdOrNull(id)
        return if (interestPoint != null) {
            if (tagRepository.existsByEnglish(parameters.tag)) {
                if (languageRepository.existsByName(parameters.language)) {val description = interestPoint.descriptions.firstOrNull {
                    it.tag == parameters.tag
                }
                    if (description != null) {
                        val translation = description.values.firstOrNull {
                            it.language == parameters.language
                        }
                        if (translation == null) {
                            addNewTranslation(interestPoint, description, parameters)
                        }
                        else {
                            editTranslation(interestPoint, translation, parameters)
                        }
                    }
                    else {
                        addNewDescription(interestPoint, parameters)
                    }
                }
                else {
                    ResponseEntity.status(HttpStatus.BAD_REQUEST)
                        .body("The given language is not valid.")
                }
            }
            else {
                ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("The given tag is not a valid English tag.")
            }
        }
        else {
            ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body("Could not find interest point with the given id")
        }
    }

    private fun addNewTranslation(
        interestPoint: travelGuide.collections.InterestPoint,
        description: travelGuide.collections.InterestPointDescription,
        parameters: InterestPointDescriptionBody
    ): ResponseEntity<String> {
        description.values.add(TranslationText(
            parameters.language,
            parameters.text,
            parameters.approved
        ))
        interestPointRepository.save(interestPoint)
        return ResponseEntity.status(HttpStatus.CREATED)
            .body("Added the new translation to the interest point description")
    }

    private fun editTranslation(
        interestPoint: travelGuide.collections.InterestPoint,
        translationText: TranslationText,
        parameters: InterestPointDescriptionBody
    ): ResponseEntity<String> {
        translationText.value = parameters.text
        translationText.approved = parameters.approved
        interestPointRepository.save(interestPoint)
        return ResponseEntity.status(HttpStatus.OK)
            .body("Updated the interest point description")
    }

    private fun addNewDescription(
        interestPoint: travelGuide.collections.InterestPoint,
        parameters: InterestPointDescriptionBody
    ): ResponseEntity<String> {
        val translationText = TranslationText(
            parameters.language,
            parameters.text,
            parameters.approved
        )
        interestPoint.descriptions.add(travelGuide.collections.InterestPointDescription(
            mutableListOf(translationText),
            parameters.tag,
            0,
            0,
            null
        ))
        interestPointRepository.save(interestPoint)
        return ResponseEntity.status(HttpStatus.OK)
            .body("Added the new tagged description")
    }
}

@JsonNaming(PropertyNamingStrategy.KebabCaseStrategy::class)
data class InterestPointDescriptionBody(
    val language: String,
    val tag: String,
    val text: String,
    val approved: Boolean)

@JsonNaming(PropertyNamingStrategy.KebabCaseStrategy::class)
data class NewInterestPointBody(
    val lat: Double,
    val lon: Double,
    val name: String,
    val subName: String?)

@JsonNaming(PropertyNamingStrategy.KebabCaseStrategy::class)
data class UpdateInterestPointBody(
    val lat: Double,
    val lon: Double,
    val name: String,
    val approved: Boolean,
    val subName: String?)