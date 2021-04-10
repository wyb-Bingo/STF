package com.ubin.stf.config;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.ubin.stf.model.User;
import com.ubin.stf.service.UserService;
import com.ubin.stf.utils.WeChatRequestUtils;
import com.ubin.stf.utils.WeChatResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;


/**
 * @author 70432
 */
public class WeiXinAuthenticationProvider extends DaoAuthenticationProvider {

    @Autowired
    UserService userService;

    @Value("${weiXin.AppID}")
    String AppId;

    @Value("${weiXin.AppSecret}")
    String AppSecret;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Authentication authenticateResult;
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String code = request.getParameter("code");
        if (code == null || code.length() == 0){
            authenticateResult = super.authenticate(authentication);
            return authenticateResult;
        }
        //System.out.println(code);
        //从微信拿openId和session_key
        WeChatResponse weChatResponse = null;
        try{
            weChatResponse = WeChatRequestUtils.jsCode2session(AppId, AppSecret,code);

        }catch (JsonProcessingException e){
            e.printStackTrace();
        }
        //System.out.println(weChatResponse.toString());
        if (weChatResponse == null){
            throw new BadCredentialsException(this.messages
                    .getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
        }
        String openId = weChatResponse.getOpenid() ;
        if (openId == null){
            throw new BadCredentialsException(this.messages
                    .getMessage("AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
        }
        //System.out.println(openId);
        //结束
        UserDetails loadedUser = ((UserService)this.getUserDetailsService()).loadUserByOpenId(openId);
        if (loadedUser == null) {
            authenticateResult = super.authenticate(authentication);
            User principal = (User) authenticateResult.getPrincipal();
            principal.setOpenId(openId);
            userService.updateUserIsOpenId(principal);
        }else{
            UsernamePasswordAuthenticationToken authenticationTmp =  new UsernamePasswordAuthenticationToken(
                    loadedUser.getUsername(), loadedUser.getPassword());
            authenticationTmp.setDetails(authentication.getDetails());
            authenticateResult = super.createSuccessAuthentication(loadedUser, authenticationTmp, loadedUser);
        }
        return authenticateResult;
    }
}
