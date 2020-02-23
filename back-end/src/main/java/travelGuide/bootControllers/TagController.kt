package travelGuide.bootControllers

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import travelGuide.repositories.LanguageRepository
import travelGuide.repositories.TagRepository
import travelGuide.restResponses.Tag
import travelGuide.restResponses.Translation

@RestController
class TagController {
    @Autowired
    private lateinit var tagRepository: TagRepository

    @GetMapping("/tags")
    fun getTags(): List<Tag> {
        return tagRepository.findAll().map { tag -> Tag(tag.english, tag.translations.map { Translation(it.language, it.name) }) }
    }
}