package travelGuide.repositories

import org.bson.types.ObjectId

data class Request<T>(
    var requester: ObjectId,
    var value: T
)

data class Approvable<T>(
    var value: T?,
    var requests: MutableList<Request<T>>
)