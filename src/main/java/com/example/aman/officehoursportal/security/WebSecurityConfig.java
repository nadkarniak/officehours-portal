package com.example.aman.officehoursportal.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;

@EnableWebSecurity
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)

public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final CustomUserDetailsService customUserDetailsService;
    private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

    private final PasswordEncoder passwordEncoder;


    public WebSecurityConfig(CustomUserDetailsService customUserDetailsService, CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler, PasswordEncoder passwordEncoder) {
        this.customUserDetailsService = customUserDetailsService;
        this.customAuthenticationSuccessHandler = customAuthenticationSuccessHandler;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) {
        auth.authenticationProvider(authenticationProvider());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable()
                .authorizeRequests()
                .antMatchers("/").hasAnyRole("STUDENT", "INSTRUCTOR", "ADMIN")
                .antMatchers("/api/**").hasAnyRole("STUDENT", "INSTRUCTOR", "ADMIN")
                .antMatchers("/students/all").hasRole("ADMIN")
                .antMatchers("/instructors/new").hasRole("ADMIN")
                .antMatchers("/reports/all").hasRole("ADMIN")
                .antMatchers("/instructors/all").hasRole("ADMIN")
                .antMatchers("/students/new/**").permitAll()
                .antMatchers("/students/**").hasAnyRole("STUDENT", "ADMIN")
                .antMatchers("/instructors/availability/**").hasRole("INSTRUCTOR")
                .antMatchers("/instructors/**").hasAnyRole("INSTRUCTOR", "ADMIN")
                .antMatchers("/courses/**").hasRole("ADMIN")
                .antMatchers("/exchange/**").hasRole("STUDENT")
                .antMatchers("/meetings/new/**").hasRole("STUDENT")
                .antMatchers("/meetings/**").hasAnyRole("STUDENT", "INSTRUCTOR", "ADMIN")
                .antMatchers("/reports/**").hasAnyRole("STUDENT", "INSTRUCTOR", "ADMIN")
                .and()
                .formLogin()
                    .loginPage("/login")
                    .loginProcessingUrl("/perform_login")
                    .successHandler(customAuthenticationSuccessHandler)
                    .permitAll()
                .and()
                .logout().logoutUrl("/perform_logout")
                .and()
                .exceptionHandling().accessDeniedPage("/access-denied");
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/customers/new/**");
    }


    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
        auth.setUserDetailsService(customUserDetailsService);
        auth.setPasswordEncoder(passwordEncoder);
        return auth;
    }
}