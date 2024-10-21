package tr.bel.sivas.keycloakdemo.controllers

import org.slf4j.LoggerFactory
import org.springframework.security.core.Authentication
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken
import org.springframework.security.oauth2.core.oidc.user.OidcUser
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.ResponseBody

@Controller
class HomeController {
    @RequestMapping("/", method = [RequestMethod.GET])
    @ResponseBody
    fun home(auth: Authentication?): String {
        if (auth == null) {
            return "Home Page"
        }
        if (auth is OAuth2AuthenticationToken) {
            val principal = auth.principal
            if (principal is OidcUser) {
                if (auth.isAuthenticated) {
                    log.info("User authenticated : ${principal.preferredUsername}")
                    val roles = principal.claims["role"] as List<String>
                    roles.forEach {
                        log.info("--Role: $it")
                    }
                }
            }
        }
        return "Home Page"
    }

    @RequestMapping("/user", method = [RequestMethod.GET])
    @ResponseBody
    fun user(): String {
        return "User Page"
    }

    @RequestMapping("/admin", method = [RequestMethod.GET])
    @ResponseBody
    fun admin(): String {
        return "Admin Page"
    }

    companion object {
        private val log = LoggerFactory.getLogger(HomeController::class.java)
    }
}