package com.finance.demo.configuration;

import com.finance.demo.repository.UserRepository;
import com.finance.demo.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;



@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserRepository userRepository;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints
                        .requestMatchers("/api/v1/auth/**").permitAll()

                        // Admin only
                        .requestMatchers("/api/v1/users/**").hasRole("ADMIN")

                        // Dashboard
                        .requestMatchers("/api/v1/dashboard/**").hasAnyRole("ANALYST", "ADMIN")

                        // Records
                        .requestMatchers(HttpMethod.GET, "/api/v1/records/**").authenticated()
                        .requestMatchers(HttpMethod.POST, "/api/v1/records/**").hasAnyRole("ANALYST", "ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/v1/records/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/records/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/records/**").hasRole("ADMIN")

                        // Everything else
                        .anyRequest().authenticated()
                )
                .authenticationProvider(authenticationProvider())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return email -> userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService());
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // default strength = 10
    }
}