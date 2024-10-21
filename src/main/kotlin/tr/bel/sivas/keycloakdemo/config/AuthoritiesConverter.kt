package tr.bel.sivas.keycloakdemo.config

import org.slf4j.LoggerFactory
import org.springframework.core.convert.converter.Converter
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component


interface AuthoritiesConverter :
    Converter<Map<String, Any>, Collection<GrantedAuthority>>

@Component
class AuthoritiesConverterImpl : AuthoritiesConverter {
    override fun convert(source: Map<String, Any>): Collection<GrantedAuthority> {
        val role = source["role"] as List<String>
        return role.map { SimpleGrantedAuthority(it) }.map { GrantedAuthority::class.java.cast(it) }
    }

    companion object {
        private val log = LoggerFactory.getLogger(AuthoritiesConverterImpl::class.java)
    }
}
