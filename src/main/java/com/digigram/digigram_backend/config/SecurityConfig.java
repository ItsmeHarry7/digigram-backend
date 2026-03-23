//package com.digigram.digigram_backend.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//import org.springframework.security.config.http.SessionCreationPolicy;
//
//@Configuration
//public class SecurityConfig {
//
//    private final FirebaseAuthenticationFilter firebaseFilter;
//
//    public SecurityConfig(FirebaseAuthenticationFilter firebaseFilter) {
//        this.firebaseFilter = firebaseFilter;
//    }
//
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//
//        http
//            .csrf(csrf -> csrf.disable())
//            .cors(cors -> {}) // use your GlobalCorsFilter
//            .sessionManagement(session -> 
//                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//            )
//            .authorizeHttpRequests(auth -> auth
//                // Public endpoint
//                .requestMatchers("/api/certificates/verify/**").permitAll()
//
//                // Everything else requires token
//                .anyRequest().authenticated()
//            )
//            .addFilterBefore(firebaseFilter, UsernamePasswordAuthenticationFilter.class);
//
//        return http.build();
//    }
//}