package travelGuide.repositories

import org.springframework.data.mongodb.repository.MongoRepository
import travelGuide.collections.Language

interface LanguageRepository : MongoRepository<Language, String?> {
    fun existsByName(name: String): Boolean
}