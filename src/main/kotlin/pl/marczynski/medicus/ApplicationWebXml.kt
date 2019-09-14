package pl.marczynski.medicus

import pl.marczynski.medicus.config.addDefaultProfile
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer

/**
 * This is a helper Java class that provides an alternative to creating a `web.xml`.
 * This will be invoked only when the application is deployed to a Servlet container like Tomcat, JBoss etc.
 */
class ApplicationWebXml : SpringBootServletInitializer() {

    override fun configure(application: SpringApplicationBuilder): SpringApplicationBuilder {
        /**
         * set a default to use when no profile is configured.
         */
        addDefaultProfile(application.application())
        return application.sources(MedicusApp::class.java)
    }
}
