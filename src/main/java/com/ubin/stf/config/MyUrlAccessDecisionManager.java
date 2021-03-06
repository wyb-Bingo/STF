package com.ubin.stf.config;

import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.vote.RoleVoter;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.util.Collection;


/**
 * @author 70432
 */
@Component
public class MyUrlAccessDecisionManager implements AccessDecisionManager {
    @Override
    public void decide(Authentication authentication, Object object, Collection<ConfigAttribute> configAttributes) throws AccessDeniedException, InsufficientAuthenticationException {
//        if (configAttributes.size() == 0){
//            throw new AccessDeniedException("权限不足");
//        }
        AccessDecisionVoter decisionVoter = new RoleVoter();
        int result = decisionVoter.vote(authentication,object,configAttributes);
        if (result == AccessDecisionVoter.ACCESS_GRANTED) {
            return;
        }
        throw new AccessDeniedException("权限不足");
    }

    @Override
    public boolean supports(ConfigAttribute attribute) {
        return true;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }
}
