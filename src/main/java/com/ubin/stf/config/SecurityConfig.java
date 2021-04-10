package com.ubin.stf.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ubin.stf.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.ObjectPostProcessor;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    UserService userService;

    @Autowired
    JwtLoginSuccessHandler jwtLoginSuccessHandler;

    @Autowired
    JwtLoginFailureHandler jwtLoginFailureHandler;

    @Autowired
    JwtTokenFilter jwtTokenFilter;


    @Autowired
    MyFilterInvocationSecurityMetadataSource myFilterInvocationSecurityMetadataSource;
    @Autowired
    MyUrlAccessDecisionManager myUrlAccessDecisionManager;

    @Bean
    PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService);
    }

    @Bean
    WeiXinAuthenticationProvider weiXinAuthenticationProvider(){
        WeiXinAuthenticationProvider weiXinAuthenticationProvider = new WeiXinAuthenticationProvider();
        weiXinAuthenticationProvider.setPasswordEncoder(passwordEncoder());
        weiXinAuthenticationProvider.setUserDetailsService(userService);
        return weiXinAuthenticationProvider;
    }


    @Bean
    @Override
    protected AuthenticationManager authenticationManager() throws Exception{
        return new ProviderManager(weiXinAuthenticationProvider());
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/adminTeam/**","/team/build/","/system/config/**","/admin/info/","/userTeam/**","/work/attendance/setting/attendance/user/**","/work/attendance/statistics/data/user/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests()
                .withObjectPostProcessor(new ObjectPostProcessor<FilterSecurityInterceptor>() {
                    @Override
                    public <O extends FilterSecurityInterceptor> O postProcess(O object) {
                        object.setAccessDecisionManager(myUrlAccessDecisionManager);
                        object.setSecurityMetadataSource(myFilterInvocationSecurityMetadataSource);
                        return object;
                    }
                })
//                .anyRequest().authenticated()
                .and()
                .formLogin()
                .successHandler(jwtLoginSuccessHandler)
                .failureHandler(jwtLoginFailureHandler)
                .permitAll()
                .and()
                .addFilterAfter(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class)
                .csrf().disable()
                .exceptionHandling().authenticationEntryPoint(new AuthenticationEntryPoint() {
            @Override
            public void commence(HttpServletRequest req, HttpServletResponse resp, AuthenticationException e) throws IOException, ServletException {
                resp.setContentType("application/json;charset=utf-8");
                PrintWriter writer = resp.getWriter();
                writer.write(new ObjectMapper().writeValueAsString("尚未登录，请登录"));
                writer.flush();
                writer.close();
            }
        });
    }
}

