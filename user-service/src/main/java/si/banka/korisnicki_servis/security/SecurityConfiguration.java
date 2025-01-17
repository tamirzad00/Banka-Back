package si.banka.korisnicki_servis.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import si.banka.korisnicki_servis.filter.CustomAuthenticationFilter;
import si.banka.korisnicki_servis.filter.CustomAuthorizationFilter;
import si.banka.korisnicki_servis.model.Permissions;
import si.banka.korisnicki_servis.security.otp.OtpAuthenticationManager;
import si.banka.korisnicki_servis.service.implementation.UserServiceImplementation;

import java.util.Arrays;

import static org.springframework.http.HttpMethod.*;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    private final UserServiceImplementation userServiceImplementation;
    private final UserDetailsService userDetailsService;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        CustomAuthenticationFilter authenticationFilter = new CustomAuthenticationFilter(authenticationManagerBean());
        authenticationFilter.setFilterProcessesUrl("/api/login");

        http.authorizeRequests()
                .antMatchers("/api/login", "/h2-console").permitAll()
                .antMatchers(GET, "/api/users").hasAuthority(String.valueOf(Permissions.LIST_USERS))
                .antMatchers(POST, "/api/user/create").hasAuthority(String.valueOf(Permissions.CREATE_USER))
                .antMatchers(POST, "/api/user/edit/**").hasAnyAuthority(String.valueOf(Permissions.EDIT_USER), String.valueOf(Permissions.MY_EDIT))
                .antMatchers(POST, "/api/otp/clear/**").hasAnyAuthority(String.valueOf(Permissions.EDIT_USER), String.valueOf(Permissions.MY_EDIT))
                .antMatchers(POST, "/api/otp/set/**").hasAnyAuthority(String.valueOf(Permissions.EDIT_USER), String.valueOf(Permissions.MY_EDIT))
                .antMatchers(DELETE, "/api/user/delete/**").hasAuthority(String.valueOf(Permissions.DELETE_USER));

        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.cors(Customizer.withDefaults());
        http.csrf().disable();
        http.headers().frameOptions().disable();
        http.addFilter(authenticationFilter);
        http.addFilterBefore(new CustomAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception{
        return new OtpAuthenticationManager(userServiceImplementation, super.authenticationManagerBean());
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET","POST"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
