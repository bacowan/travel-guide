package travelGuide

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource


@SpringBootApplication
open class Application {
    @Bean
    open fun bCryptPasswordEncoder(): BCryptPasswordEncoder? {
        return BCryptPasswordEncoder()
    }

    @EnableWebSecurity
    @Configuration
    internal open class WebSecurityConfig : WebSecurityConfigurerAdapter() {

        @Value("\${jwt.secret}")
        private lateinit var secret: String

        @Throws(Exception::class)
        override fun configure(http: HttpSecurity) {
            http.cors().and()
                .addFilterAfter(JWTAuthorizationFilter(secret), UsernamePasswordAuthenticationFilter::class.java)
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/authenticate").permitAll()
                .antMatchers(HttpMethod.GET, "/interest_points").permitAll()
                .antMatchers(HttpMethod.POST, "/users").permitAll()
                .antMatchers(HttpMethod.GET, "/users/{id}").access("@securityService.matchesUser(authentication, #id)")
                .anyRequest().authenticated()
        }

        @Bean
        open fun corsConfigurationSource(): CorsConfigurationSource? {
            val configuration = CorsConfiguration()
            configuration.allowedOrigins = listOf("http://localhost:3000")
            configuration.allowedMethods = listOf("GET", "POST")
            val source =
                UrlBasedCorsConfigurationSource()
            source.registerCorsConfiguration("/**", configuration)
            return source
        }

//        @Bean
//        open fun corsConfigurationSource() : CorsConfigurationSource {
//            val source = UrlBasedCorsConfigurationSource()
//            source.registerCorsConfiguration("/**", CorsConfiguration().applyPermitDefaultValues())
//            return source
//        }
    }
}

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}

