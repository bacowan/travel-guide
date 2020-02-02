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
import travelGuide.collections.*
import travelGuide.repositories.*
import travelGuide.restResponses.InterestPoint
import travelGuide.restResponses.InterestPointDescription
import travelGuide.restResponses.ShortInterestPoint

@RestController
class InterestPointController {
    @Autowired
    private lateinit var interestPointRepository: InterestPointRepository
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
                        name = (it.name.firstOrNull { name -> name.language == language } ?: it.name.firstOrNull()) ?.value?.value ?: "",
                        sub_name = (it.subName.firstOrNull { name -> name.language == language } ?: it.subName.firstOrNull()) ?.value?.value ?: "",
                        lat = it.location.value?.firstOrNull() ?: 0.0,
                        lon  = it.location.value?.firstOrNull() ?: 0.0,
                        approved = it.approved)
                else null }

        return ResponseEntity.status(HttpStatus.OK)
            .body(interestPoints)
    }

    @PostMapping("/interest_points")
    fun addInterestPoint(
        @RequestBody parameters: NewInterestPointBody,
        authentication: Authentication?): ResponseEntity<String> {

        val tooClose = isTooClose(parameters.lat, parameters.lon)
        return if (!tooClose) {
            val user = if (authentication != null) userRepository.findByIdOrNull(authentication.name) else null
            if (user != null) {
                val interestPoint = travelGuide.collections.InterestPoint(
                    location = Approvable(listOf(parameters.lat, parameters.lon)),
                    name = mutableListOf(TranslationText(user.defaultLanguage, Approvable(parameters.name))),
                    subName = if (parameters.subName != null)
                        mutableListOf(TranslationText(user.defaultLanguage, Approvable(parameters.subName)))
                        else mutableListOf(),
                    descriptions = mutableListOf(),
                    submitter = ObjectId(user.id),
                    approved = false
                    )
                val savedInterestPoint = interestPointRepository.save(interestPoint)
                if (savedInterestPoint.id != null) {
                    ResponseEntity.status(HttpStatus.CREATED)
                        .body(savedInterestPoint.id)
                }
                else {
                    ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Could not create the interest point")
                }
            }
            else {
                ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Could not find the given logged in user")
            }
        }
        else {
            ResponseEntity.status(HttpStatus.CONFLICT)
                .body("There is already an interest point within 5 meters of this one")
        }
    }

    private fun isTooClose(lat: Double, lon: Double): Boolean {
        return interestPointRepository.existsByLocationValueNear(
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
                name = interestPoint.name.firstOrNull { it.language == language }?.value?.value
                    ?: interestPoint.name.firstOrNull { it.language == "English" }?.value?.value
                    ?: "",
                sub_name = interestPoint.subName.firstOrNull { it.language == language }?.value?.value
                    ?: interestPoint.subName.firstOrNull { it.language == "English" }?.value?.value
                    ?: "",
                lat = interestPoint.location.value?.firstOrNull() ?: 0.0,
                lon = interestPoint.location.value?.elementAtOrNull(1) ?: 0.0,
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

    @PutMapping("/interest_points/{id}")
    fun updateInterestPoint(
        @PathVariable id: String,
        @RequestBody parameters: UpdateInterestPointBody,
        authentication: Authentication?): ResponseEntity<String> {

        return if (languageRepository.existsByName(parameters.language)) {
            val interestPoint = interestPointRepository.findByIdOrNull(id)
            return if (interestPoint?.id != null) {
                val tooClose = parameters.lat != null && parameters.lon != null && isTooClose(parameters.lat, parameters.lon)
                return if (!tooClose) {
                    val user = if (authentication != null) userRepository.findByIdOrNull(authentication.name) else null
                    if (user != null) {
                        if (!parameters.approved) {
                            updateInterestPointRequest(interestPoint, parameters, user)
                        }
                        else if (user.permissions.contains("Approver")) {
                            approveInterestPoint(interestPoint, parameters, user)
                        }
                        else {
                            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                                .body("Only 'Approver's can approve interest points.")
                        }

                        val savedInterestPoint = interestPointRepository.save(interestPoint)
                        if (savedInterestPoint.id != null) {
                            ResponseEntity.status(HttpStatus.CREATED)
                                .body(savedInterestPoint.id)
                        }
                        else {
                            ResponseEntity.status(HttpStatus.FORBIDDEN)
                                .body("Failed to create the interest point.")
                        }
                    }
                    else {
                        ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body("Could not find the given logged in user")
                    }
                }
                else {
                    ResponseEntity.status(HttpStatus.CONFLICT)
                        .body("There is already an interest point within 5 meters of this one")
                }
            }
            else {
                ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .build()
            }
        }
        else {
            ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body("The given language is not valid.")
        }
    }

    private fun updateInterestPointRequest(
        interestPoint: travelGuide.collections.InterestPoint,
        parameters: UpdateInterestPointBody,
        user: User): ResponseEntity<String> {

        if (parameters.lat != null && parameters.lon != null) {
            setRequest(
                interestPoint = interestPoint,
                approvableGetter = { it.location },
                requestGetter = { it.requests.firstOrNull { r -> r.requester.toHexString() == user.id } },
                user = user,
                value = listOf(parameters.lat, parameters.lon)
            )
        }

        if (parameters.name != null) {
            var name = interestPoint.name.firstOrNull { it.language == parameters.language }
            if (name == null) {
                name = TranslationText(parameters.language, Approvable(null, mutableListOf()))
                interestPoint.name.add(name)
            }

            setRequest(
                interestPoint = interestPoint,
                approvableGetter = { name.value },
                requestGetter = { it.requests.firstOrNull { r -> r.requester.toHexString() == user.id } },
                user = user,
                value = parameters.name)
        }

        if (parameters.subName != null) {
            var subName = interestPoint.subName.firstOrNull { it.language == parameters.language }
            if (subName == null) {
                subName = TranslationText(parameters.language, Approvable(null, mutableListOf()))
                interestPoint.subName.add(subName)
            }

            setRequest(
                interestPoint = interestPoint,
                approvableGetter = { subName.value },
                requestGetter = { it.requests.firstOrNull { r -> r.requester.toHexString() == user.id } },
                user = user,
                value = parameters.subName)
        }

        interestPointRepository.save(interestPoint)

        return ResponseEntity.status(HttpStatus.OK)
            .body("Successfully submitted the update")
    }

    private fun<T> setRequest(
        interestPoint: travelGuide.collections.InterestPoint,
        approvableGetter: (travelGuide.collections.InterestPoint) -> Approvable<T>,
        requestGetter: (Approvable<T>) -> Request<T>?,
        user: User,
        value: T) {

        val approvable = approvableGetter(interestPoint)
        val request = requestGetter(approvable)
        if (request != null) {
            request.value = value
        }
        else {
            approvable.requests.add(Request(
                ObjectId(user.id),
                value
            ))
        }
    }

    private fun<T> commitRequest(
        interestPoint: travelGuide.collections.InterestPoint,
        approvableGetter: (travelGuide.collections.InterestPoint) -> Approvable<T>,
        requestGetter: (Approvable<T>) -> Request<T>?,
        value: T) {

        val approvable = approvableGetter(interestPoint)
        val request = requestGetter(approvable)

        if (request != null) {
            approvable.value = value
            approvable.requests.remove(request)
        }
    }

    private fun approveInterestPoint(
        interestPoint: travelGuide.collections.InterestPoint,
        parameters: UpdateInterestPointBody,
        user: User): ResponseEntity<String> {

        if (parameters.lat != null && parameters.lon != null) {
            commitRequest(
                interestPoint = interestPoint,
                approvableGetter = { it.location },
                requestGetter = { it.requests.firstOrNull { r -> r.requester.toHexString() == user.id } },
                value = listOf(parameters.lat, parameters.lon)
            )
        }

        if (parameters.name != null) {
            val name = interestPoint.name.firstOrNull { it.language == parameters.language }
            if (name != null) {
                commitRequest(
                    interestPoint = interestPoint,
                    approvableGetter = { name.value },
                    requestGetter = { it.requests.firstOrNull { r -> r.requester.toHexString() == user.id } },
                    value = parameters.name)
            }
        }


        if (parameters.subName != null) {
            val subName = interestPoint.subName.firstOrNull { it.language == parameters.language }
            if (subName != null) {
                setRequest(
                    interestPoint = interestPoint,
                    approvableGetter = { subName.value },
                    requestGetter = { it.requests.firstOrNull { r -> r.requester.toHexString() == user.id } },
                    user = user,
                    value = parameters.subName)
            }
        }

        interestPoint.approved = true

        interestPointRepository.save(interestPoint)

        return ResponseEntity.status(HttpStatus.OK)
            .body("Successfully updated the interest point")
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
        allTags: List<Tag>?,
        descriptions: List<travelGuide.collections.InterestPointDescription>,
        language: String): List<InterestPointDescription> {
        return descriptions.map {
            InterestPointDescription(
                tag = if (allTags != null)
                    allTags.firstOrNull { tag -> tag.english == it.tag }?.translations?.firstOrNull { trans -> trans.language == language }?.name
                        ?: it.tag ?: ""
                    else it.tag,
                likes = it.likes,
                dislikes = it.dislikes,
                value = it.values.firstOrNull { value -> value.language == language }?.value?.value
                    ?: it.values.firstOrNull { value -> value.language == "English" }?.value?.value
                    ?: ""
            )
        }
    }

    @PutMapping("/interest_points/{id}/descriptions")
    fun putDescription(
        @PathVariable id: String,
        @RequestBody parameters: InterestPointDescriptionBody,
        authentication: Authentication?): ResponseEntity<String> {

        val interestPoint = interestPointRepository.findByIdOrNull(id)
        return if (interestPoint != null) {
            if (tagRepository.existsByEnglish(parameters.tag)) {
                if (languageRepository.existsByName(parameters.language)) {
                    val user = if (authentication != null) userRepository.findByIdOrNull(authentication.name) else null
                    if (user != null) {
                        val description = interestPoint.descriptions.firstOrNull { it.tag == parameters.tag }
                        if (!parameters.approved) {
                            if (description != null) {
                                val translation = description.values.firstOrNull { it.language == parameters.language }
                                if (translation == null) {
                                    addNewTranslation(interestPoint, description, parameters, user)
                                }
                                else {
                                    editTranslation(interestPoint, translation, parameters, user)
                                }
                            }
                            else {
                                addNewDescription(interestPoint, parameters, user)
                            }
                        }
                        else if (user.permissions.contains("Approver")) {
                            if (description != null) {
                                approveDescription(interestPoint, description, parameters)
                            }
                            else {
                                ResponseEntity.status(HttpStatus.NOT_FOUND)
                                    .body("Interest point was found, but it did not have the given tag to approve")
                            }
                        }
                        else {
                            ResponseEntity.status(HttpStatus.FORBIDDEN)
                                .body("Only 'Approver's can approve interest point descriptions.")
                        }
                    }
                    else {
                        ResponseEntity.status(HttpStatus.FORBIDDEN)
                            .body("Could not find the given logged in user")
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

    private fun approveDescription(
        interestPoint: travelGuide.collections.InterestPoint,
        description: travelGuide.collections.InterestPointDescription,
        parameters: InterestPointDescriptionBody): ResponseEntity<String> {

        val text = description.values.firstOrNull { it.language == parameters.language }?.value
        if (text != null) {
            val request = text.requests.firstOrNull { it.value == parameters.text }
            if (request != null) {
                text.value = parameters.text
                text.requests.remove(request)
                interestPointRepository.save(interestPoint)
                return ResponseEntity.status(HttpStatus.OK)
                    .body("Successfully approved the description")
            }
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body("Failed to approve the description")
    }

    private fun addNewTranslation(
        interestPoint: travelGuide.collections.InterestPoint,
        description: travelGuide.collections.InterestPointDescription,
        parameters: InterestPointDescriptionBody,
        user: User): ResponseEntity<String> {

        description.values.add(TranslationText(
            parameters.language,
            Approvable(null, mutableListOf(
                Request(
                    ObjectId(user.id),
                    parameters.text
            )))
        ))
        interestPointRepository.save(interestPoint)
        return ResponseEntity.status(HttpStatus.CREATED)
            .body("Added the new translation to the interest point description")
    }

    private fun editTranslation(
        interestPoint: travelGuide.collections.InterestPoint,
        translationText: TranslationText,
        parameters: InterestPointDescriptionBody,
        user: User): ResponseEntity<String> {

        translationText.value.requests.add(Request(
            ObjectId(user.id),
            parameters.text
        ))
        interestPointRepository.save(interestPoint)
        return ResponseEntity.status(HttpStatus.OK)
            .body("Updated the interest point description")
    }

    private fun addNewDescription(
        interestPoint: travelGuide.collections.InterestPoint,
        parameters: InterestPointDescriptionBody,
        user: User): ResponseEntity<String> {

        val translationText = TranslationText(
            parameters.language,
            Approvable(
                null,
                mutableListOf(Request(
                    ObjectId(user.id),
                    parameters.text
            )))
        )
        interestPoint.descriptions.add(travelGuide.collections.InterestPointDescription(
            mutableListOf(translationText),
            parameters.tag,
            0,
            0
        ))
        interestPointRepository.save(interestPoint)
        return ResponseEntity.status(HttpStatus.OK)
            .body("Added the new tagged description")
    }
}

@JsonNaming(PropertyNamingStrategy.KebabCaseStrategy::class)
data class NewInterestPointBody(
    val lat: Double,
    val lon: Double,
    val name: String,
    val subName: String?)

@JsonNaming(PropertyNamingStrategy.KebabCaseStrategy::class)
data class InterestPointDescriptionBody(
    val language: String,
    val tag: String,
    val text: String,
    val approved: Boolean)

@JsonNaming(PropertyNamingStrategy.KebabCaseStrategy::class)
data class UpdateInterestPointBody(
    val language: String,
    val lat: Double?,
    val lon: Double?,
    val name: String?,
    val approved: Boolean,
    val subName: String?)