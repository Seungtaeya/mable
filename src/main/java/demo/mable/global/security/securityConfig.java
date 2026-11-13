package demo.mable.global.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.session.HttpSessionEventPublisher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class securityConfig {

    @Bean
    public PasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public HttpSessionEventPublisher httpSessionEventPublisher() {
        return new HttpSessionEventPublisher();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, CustomAuthenticationFailureHandler failureHandler) throws Exception {
        http
                .authorizeHttpRequests(strategy ->
                        strategy.requestMatchers(
                                "/adminPage",
                                "/admin/**",
                                "/api/admin/**"
                                ).hasRole("ADMIN")

                                .requestMatchers(HttpMethod.GET,"/",
                                        "/registry",
                                        "/.well-known/appspecific/com.chrome.devtools.json",
                                        "/apple-touch-icon.png",
                                        "/login",
                                        "/css/**",
                                        "favicon.ico",
                                        "/js/**",
                                        "/images/**",
                                        "/error").permitAll()
                                .requestMatchers(HttpMethod.POST,"/registry").permitAll()
                                .requestMatchers(HttpMethod.POST,"/api/**").authenticated()
                                .requestMatchers("/ws","/ws/**").authenticated()
                                .anyRequest().authenticated()
                )
                .formLogin(registry ->
                        registry
                                .loginPage("/login")
                                .loginProcessingUrl("/login")
                                .defaultSuccessUrl("/lobby")
                                .failureHandler(failureHandler).permitAll()

                )
                .sessionManagement(session ->
                        session
                                .maximumSessions(1)
                                .maxSessionsPreventsLogin(false)
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/")
                        .invalidateHttpSession(true)
                        .clearAuthentication(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                );



        return http.build();
    }
}
