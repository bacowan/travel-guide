package travelGuide.bootControllers

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity

data class Precondition<T>(val condition: () -> Boolean, val responseStatus: HttpStatus, val body: T)
fun <T> notNullPrecondition(obj: Any?, responseStatus: HttpStatus, body: T)
        = Precondition({ obj != null }, responseStatus, body)

fun <T> checkPreconditions(vararg conditions: Precondition<T>, onSuccess: () -> ResponseEntity<T>) : ResponseEntity<T> {
    for (it in conditions) {
        if (!it.condition()) {
            return ResponseEntity.status(it.responseStatus).body(it.body)
        }
    }
    return onSuccess()
}