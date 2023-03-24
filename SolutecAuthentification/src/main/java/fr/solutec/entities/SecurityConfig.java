package fr.solutec.entities;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import fr.solutec.repository.UserRepository;
import fr.solutec.service.UserDetailsServiceImpl;
import lombok.RequiredArgsConstructor;


@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
	private final JwtAuthFilter jwtAuthFilter;
		@Autowired
		private UserRepository userRepo;
		@Autowired
		UserDetailsServiceImpl userDetailsService;
	
		
	
	
	
	
	@Bean
    AuthenticationProvider authenticationProvider() {
        final DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }
	@Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
	
	
	  @Bean
	  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
	        http
	        	.csrf().disable()
	        	/*.cors().disable()*/ //Pour paramétrer le crossOrigin
	        	/*.formLogin((form) -> form
		                .permitAll()
		            )*/
	        	.sessionManagement((session) -> session 
	            		.sessionCreationPolicy(SessionCreationPolicy.STATELESS) //Pour créer une session avec une authentification par Token
	            )
	            .authorizeHttpRequests((requests) -> requests
	            	.requestMatchers(AntPathRequestMatcher.antMatcher("liste")).authenticated()
	                .anyRequest().permitAll()
	            )
	            .authenticationProvider(authenticationProvider())
	            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class) //Filtre avec Token
	            .logout((logout) -> logout.permitAll()
	            );

	        return http.build();
	    }
	  
	
	  
	  
    @Bean
    PasswordEncoder passwordEncoder() //Encodage de mot de passe en Bcrypt
    {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    UserDetailsService userDetailsService() {
        return new UserDetailsService() {
            @Override
            public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
                return (UserDetails) userRepo.findByUsername(username);
            }
        };
    }

}
