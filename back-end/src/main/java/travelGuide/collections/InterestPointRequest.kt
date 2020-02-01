package travelGuide.collections

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id

data class InterestPointRequest(
    @Id val id: String? = null,
    var location: List<Double>,
    var name: List<TranslationText>,
    var subName: List<TranslationText>,
    var descriptions: MutableList<InterestPointDescription>,
    var submitter: ObjectId
)