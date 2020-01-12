package travelGuide.restResponses

import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategy.KebabCaseStrategy::class)
data class User(
    val id: String,
    val email: String,
    val defaultLanguage: String,
    val permissions: List<String>,
    val defaultTags: List<String>
)