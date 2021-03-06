package com.ubin.stf.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ubin.stf.model.User;
import com.ubin.stf.utils.ResponseBean;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.security.core.Authentication;

import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author 70432
 */
@Component
public class JwtLoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        User principal = (User) authentication.getPrincipal();
        principal.setOpenId("");
        principal.setPassword("");
        String userDetail = new ObjectMapper().writeValueAsString(principal);
        String jwt = Jwts.builder()
                .claim("userDetail", userDetail)
                .setSubject(authentication.getName())
                .setExpiration(new Date(System.currentTimeMillis() + 60 * 60 * 24 * 1000))
                .signWith(SignatureAlgorithm.HS512, "ubin@13530834836")
                .compact();

        Map<String,String> map = new HashMap<>();
        map.put("token",jwt);
        ResponseBean responseBean = ResponseBean.ok("登录成功", map);
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(new ObjectMapper().writeValueAsString(responseBean));
        response.getWriter().flush();
        response.getWriter().close();
    }
}
