package travelGuide.collections

import org.springframework.data.annotation.Id

data class TranslationText(
    var language: String,
    var value: String
)

data class InterestPointDescription(
    // if there is no description, this will just act as another tag for the interest point
    var descriptionText: TranslationText?,
    var tag: TranslationText
)

data class InterestPoint(
    @Id val id: String? = null,
    var location: Array<Double>,
    var name: TranslationText,
    var subName: TranslationText,
    var descriptions: Array<InterestPointDescription>)