package travelGuide.collections

import org.springframework.data.annotation.Id

data class TranslationText(
    var language: String,
    var value: String,
    var approved: Boolean
)

// if there is no description, this will just act as another tag for the interest point
data class InterestPointDescription(
    var values: List<TranslationText>,
    var tag: Tag,
    var likes: Int,
    var dislikes: Int,
    var submitter: String
)

data class InterestPoint(
    @Id val id: String? = null,
    var location: Array<Double>,
    var name: TranslationText,
    var subName: TranslationText,
    var descriptions: Array<InterestPointDescription>)