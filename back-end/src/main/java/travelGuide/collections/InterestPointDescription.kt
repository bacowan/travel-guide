package travelGuide.collections

import org.bson.types.ObjectId

// if "values" is empty, this will just act as another tag for the interest point
data class InterestPointDescription(
    var values: MutableList<TranslationText>,
    var tag: String, // this only includes the English tag; to get other tags, you have to join with the tags collection
    var likes: Int,
    var dislikes: Int
)