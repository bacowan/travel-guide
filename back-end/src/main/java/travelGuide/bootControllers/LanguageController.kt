package travelGuide.bootControllers

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import travelGuide.repositories.LanguageRepository

@RestController
class LanguageController {
    @Autowired
    private lateinit var languageRepository: LanguageRepository

    @GetMapping("/languages")
    fun getLanguages(): List<String> {
        return languageRepository.findAll().map { it.name }
    }
}