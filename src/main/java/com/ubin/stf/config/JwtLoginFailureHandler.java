package com.ubin.stf.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ubin.stf.utils.ResponseBean;
import org.springframework.security.authentication.*;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class JwtLoginFailureHandler extends SimpleUrlAuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        String msg = "登录失败";
        if (exception instanceof BadCredentialsException){
            msg = "用户名或密码或code错误，请重新登录";
        }else if(exception instanceof LockedException){
            msg = "账户被锁定，请联系管理员";
        }else if (exception instanceof CredentialsExpiredException){
            msg = "密码过期，请联系管理员";
        }else if (exception instanceof AccountExpiredException){
            msg = "账户过期，请联系管理员";
        }else if (exception instanceof DisabledException){
            msg = "账户被禁用，请联系管理员";
        }

        ResponseBean error = ResponseBean.errorAuth(msg);
        response.setContentType("application/json;charset=utf-8");
        response.getWriter().write(new ObjectMapper().writeValueAsString(error));
        response.getWriter().flush();
        response.getWriter().close();
    }
}
