package org.rococo.gateway.config;

import lombok.RequiredArgsConstructor;
import org.rococo.gateway.config.cors.CorsCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final CorsCustomizer corsCustomizer;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        corsCustomizer.corsCustomizer(http);

        http.authorizeHttpRequests(customizer ->
                customizer.requestMatchers(
                                antMatcher(HttpMethod.GET, "/api/session"),
                                antMatcher(HttpMethod.GET, "/api/artist/**"),
                                antMatcher(HttpMethod.GET, "/api/country/**"),
                                antMatcher(HttpMethod.GET, "/api/museum/**"),
                                antMatcher(HttpMethod.GET, "/api/painting/**"),
                                antMatcher(HttpMethod.GET, "/api/user/all"),
                                antMatcher("/actuator/health"))
                        .permitAll()
                        .anyRequest()
                        .authenticated()
        ).oauth2ResourceServer((oauth2) -> oauth2.jwt(Customizer.withDefaults()));
        return http.build();
    }
}
