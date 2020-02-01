package travelGuide.collections

import org.springframework.data.annotation.Id

data class InterestPoint(
    @Id val id: String? = null,
    var location: List<Double>,
    var name: List<TranslationText>,
    var subName: List<TranslationText>,
    var descriptions: MutableList<InterestPointDescription>)