package travelGuide

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter


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
            http.csrf().disable()
                .addFilterAfter(JWTAuthorizationFilter(secret), UsernamePasswordAuthenticationFilter::class.java)
                .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/authenticate").permitAll()
                .antMatchers(HttpMethod.POST, "/users").permitAll()
                .antMatchers(HttpMethod.GET, "/users/{id}").access("@securityService.matchesUser(authentication, #id)")
                .anyRequest().authenticated()
        }
    }
}

fun main(args: Array<String>) {
    runApplication<Application>(*args)
}

