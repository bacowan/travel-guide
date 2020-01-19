package travelGuide.restResponses

import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.annotation.JsonNaming

@JsonNaming(PropertyNamingStrategy.KebabCaseStrategy::class)
data class UserLike(
    val location_id: String,
    val tag: String,
    val liked: Boolean
)