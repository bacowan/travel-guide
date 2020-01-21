package travelGuide.bootControllers

import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.authority.AuthorityUtils
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import io.jsonwebtoken.Jwts
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.password.PasswordEncoder
import travelGuide.collections.User
import travelGuide.repositories.UserRepository
import java.util.*

@RestController
class AuthenticateController {

    @Value("\${jwt.secret}")
    private lateinit var secret: String

    @Value("\${jwt.id}")
    private lateinit var jwtId: String

    @Value("\${jwt.expiration}")
    private var expiration: Int = 0

    @Autowired
    private lateinit var passwordEncoder: PasswordEncoder

    @Autowired
    private lateinit var repository: UserRepository

    @PostMapping("authenticate")
    fun authenticate(
        @RequestParam("user") username: String,
        @RequestParam("password") password: String): ResponseEntity<String> {

        val user = repository.findByEmail(username).firstOrNull()
        if (user == null || !checkPassword(user, password)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body("Incorrect credentials")
        }

        val grantedAuthorities = AuthorityUtils
            .commaSeparatedStringToAuthorityList("ROLE_USER")

        val token: String = Jwts
            .builder()
            .setId(jwtId)
            .setSubject(user.id)
            .claim(
                "authorities",
                grantedAuthorities.map { it.authority }
            )
            .setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(Date(System.currentTimeMillis() + expiration))
            .signWith(
                io.jsonwebtoken.SignatureAlgorithm.HS512,
                secret.toByteArray()
            ).compact()

        return ResponseEntity.status(HttpStatus.OK)
            .body("Bearer $token")
    }

    private fun checkPassword(user: User, password: String): Boolean {
        return passwordEncoder.matches(password, user.password)
    }
}