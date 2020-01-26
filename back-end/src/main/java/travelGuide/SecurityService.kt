package travelGuide

import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import java.security.Principal

@Component("securityService")
class SecurityService {
    fun matchesUser(authentication: Authentication?, key: String?): Boolean {
        return authentication != null
                && key != null
                && authentication.principal == key
    }
}