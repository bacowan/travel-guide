package travelGuide

import org.springframework.data.annotation.Id;

class InterestPoint {
    @Id
    var id: String? = null
    var firstName: String? = null
    var lastName: String? = null

    constructor() {}
    constructor(firstName: String?, lastName: String?) {
        this.firstName = firstName
        this.lastName = lastName
    }

    override fun toString(): String {
        return String.format(
            "Customer[id=%s, firstName='%s', lastName='%s']",
            id, firstName, lastName
        )
    }
}