package com.ubin.stf.config;


import com.alibaba.druid.util.StringUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ubin.stf.model.User;
import com.ubin.stf.utils.JwtUtils;
import com.ubin.stf.utils.ResponseBean;
import io.jsonwebtoken.Claims;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @author 70432
 */
@Component
public class JwtTokenFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader("Authorization");
        if (StringUtils.isEmpty(token)){
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write(new ObjectMapper().writeValueAsString(ResponseBean.errorAuth("未登录")));
            response.getWriter().flush();
            response.getWriter().close();
            return;
        }
        Claims claims = JwtUtils.parseJWT(token);
        if (JwtUtils.isTokenExpired(claims)) {
            response.setContentType("application/json;charset=utf-8");
            response.getWriter().write(new ObjectMapper().writeValueAsString(ResponseBean.errorAuth("登录态失效，请重新登录")));
            response.getWriter().flush();
            response.getWriter().close();
            return;
        }
        String userDetail = claims.get("userDetail", String.class);
        User user = new ObjectMapper().readValue(userDetail,User.class);
        UsernamePasswordAuthenticationToken usernamePasswordAuthenticationToken = new UsernamePasswordAuthenticationToken(user,null,user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(usernamePasswordAuthenticationToken);
        filterChain.doFilter(request,response);
    }

}
