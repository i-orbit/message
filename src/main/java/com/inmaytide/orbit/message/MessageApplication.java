package com.inmaytide.orbit.message;

import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.SpecVersion;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(scanBasePackages = {"com.inmaytide.orbit.commons", "com.inmaytide.orbit.message"})
public class MessageApplication {

    public static void main(String[] args) {
        SpringApplication.run(MessageApplication.class, args);
    }

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .specVersion(SpecVersion.V30)
                .info(
                        new Info().title("Orbit Message API")
                                .description("Message Service For Orbit System")
                                .version("1.0.0")
                                .license(new License().name("MIT").url("https://opensource.org/licenses/MIT"))
                )
                .externalDocs(
                        new ExternalDocumentation()
                                .description("UAA Wiki Documentation")
                                .url("https://github.com/i-orbit/message")
                );
    }

}
