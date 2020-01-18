package travelGuide.collections

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.DBRef

data class TranslationText(
    var language: String,
    var value: String,
    var approved: Boolean
)

// if there is no description, this will just act as another tag for the interest point
data class InterestPointDescription(
    var values: MutableList<TranslationText>,
    var tag: String, // this only includes the English tag; to get other tags, you have to join with the tags collection
    var likes: Int,
    var dislikes: Int,
    var submitter: ObjectId? //TODO: this is just nullable for dev purposes
)

data class InterestPoint(
    @Id val id: String? = null,
    var location: List<Double>,
    var name: List<TranslationText>,
    var subName: List<TranslationText>,
    var descriptions: MutableList<InterestPointDescription>,
    var approved: Boolean)