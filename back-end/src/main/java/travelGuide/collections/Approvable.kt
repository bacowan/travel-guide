package travelGuide.collections

import org.bson.types.ObjectId

data class Request<T>(
    var requester: ObjectId,
    var value: T,
    var explanation: String? = null
)

data class Approvable<T>(
    var value: T?,
    var requests: MutableList<Request<T>> = mutableListOf()
)