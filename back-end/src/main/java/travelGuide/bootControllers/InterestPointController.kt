package travelGuide.bootControllers

import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.annotation.JsonNaming
import org.bson.types.ObjectId
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import travelGuide.collections.Tag
import travelGuide.collections.TranslationText
import travelGuide.repositories.InterestPointRepository
import travelGuide.repositories.LanguageRepository
import travelGuide.repositories.TagRepository
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