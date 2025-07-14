package tr.bel.sivas.keycloakdemo.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.Customizer
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper
import org.springframework.security.oauth2.client.oidc.web.logout.OidcClientInitiatedLogoutSuccessHandler
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
class SecurityConfig(private val authoritiesConverter: AuthoritiesConverter) {
    @Bean
    fun authenticationConverter(): GrantedAuthoritiesMapper {
        return GrantedAuthoritiesMapper { authorities ->
            authorities.filterIsInstance<OidcUserAuthority>()
                .flatMap { authority ->
                    val claims = authority.userInfo?.claims ?: authority.idToken.claims
                    authoritiesConverter.convert(claims) as Iterable<GrantedAuthority?>
                }
        }
    }

    @Bean
    fun securityFilterChain(
        http: HttpSecurity,
        clientRegistrationRepository: ClientRegistrationRepository
    ): SecurityFilterChain {

        http.invoke {
            authorizeHttpRequests {
                authorize("/", permitAll)
                authorize("/user", hasAuthority("user"))
                authorize("/admin", hasAuthority("admin"))
                authorize(anyRequest, authenticated)
            }
        }

        http.oauth2Login(Customizer.withDefaults())
        http.logout { logout ->
            val handler = OidcClientInitiatedLogoutSuccessHandler(clientRegistrationRepository)
            handler.setPostLogoutRedirectUri("{baseUrl}")
            logout.logoutSuccessHandler(handler)
        }
        return http.build()
    }
}

