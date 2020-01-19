package travelGuide.repositories

import org.springframework.data.mongodb.repository.MongoRepository
import travelGuide.collections.Language
import travelGuide.collections.Tag

interface TagRepository : MongoRepository<Tag, String?> {
    fun findByEnglish(english: String) : Tag?
    fun existsByEnglish(english: String) : Boolean
}