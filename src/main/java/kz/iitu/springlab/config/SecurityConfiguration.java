
package kz.iitu.springlab.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfiguration {

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http
    ) throws Exception {

        http
                .authorizeHttpRequests(auth -> auth

                        .requestMatchers(
                                "/",
                                "/login",
                                "/css/**",
                                "/js/**",
                                "/images/**",
                                "/default-ui.css",
                                "/error"
                        ).permitAll()

                        .requestMatchers(
                                "/admin",
                                "/admin/**"
                        ).hasRole("ADMIN")

                        .requestMatchers("/h2-console/**")
                        .hasRole("ADMIN")

                        // LAB 12: Book REST API
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/books",
                                "/api/books/**"
                        ).hasAnyRole("USER", "ADMIN")

                        .requestMatchers(
                                "/api/books",
                                "/api/books/**"
                        ).hasRole("ADMIN")

                        // Book HTML pages
                        .requestMatchers(
                                HttpMethod.GET,
                                "/books",
                                "/books/**"
                        ).hasAnyRole("USER", "ADMIN")

                        .requestMatchers(
                                "/books",
                                "/books/**"
                        ).hasRole("ADMIN")

                        .anyRequest().denyAll()
                )

                .formLogin(form -> form
                        .defaultSuccessUrl("/books", true)
                        .permitAll()
                )

                .logout(logout -> logout
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                )

                .csrf(csrf -> csrf
                        .ignoringRequestMatchers("/h2-console/**")
                )

                .headers(headers -> headers
                        .frameOptions(frame -> frame.sameOrigin())
                );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(10);
    }
}
