package travelGuide

import org.springframework.data.annotation.Id;

data class InterestPoint(
    @Id val id: String? = null,
    var location: Array<Double>,
    var name: String)