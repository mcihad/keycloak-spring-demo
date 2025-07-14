package tr.bel.sivas.keycloakdemo.config


import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.convert.converter.Converter
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component


interface AuthoritiesConverter :
    Converter<Map<String, Any>, Collection<GrantedAuthority>>


@Component
class AuthoritiesConverterImpl(
    @Value("\${keycloak.appid}") private val appId: String
) : AuthoritiesConverter {
    override fun convert(source: Map<String, Any>): Collection<GrantedAuthority> {
        //source is expected to be a map containing the claims from the OIDC token
        val resourceAccess = source["resource_access"] as? Map<*, *>
        val app = resourceAccess?.get(appId) as? Map<*, *>
        val roles = app?.get("roles") as? List<*>


        val authorities = roles?.mapNotNull { role ->
            (role as? String)?.let { roleName ->
                SimpleGrantedAuthority(roleName)
            }
        } ?: emptyList()

        return authorities
    }

    companion object {
        private val log = LoggerFactory.getLogger(AuthoritiesConverterImpl::class.java)
    }
}