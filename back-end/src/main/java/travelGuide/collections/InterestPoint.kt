package travelGuide.collections

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id

data class TranslationText(
    var language: String,
    var value: Approvable<String>
)

// if values is empty, this will just act as another tag for the interest point
data class InterestPointDescription(
    var values: MutableList<TranslationText>,
    var tag: String, // this only includes the English tag; to get other tags, you have to join with the tags collection
    var likes: Int,
    var dislikes: Int
)

data class InterestPoint(
    @Id val id: String? = null,
    var location: Approvable<List<Double>>,
    var name: MutableList<TranslationText>,
    var subName: MutableList<TranslationText>,
    var descriptions: MutableList<InterestPointDescription>,
    var submitter: ObjectId,
    var approved: Boolean)