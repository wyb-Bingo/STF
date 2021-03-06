package com.ubin.stf.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;

public class WeChatRequestUtils {

    public static WeChatResponse jsCode2session(String AppId, String AppSecret, String code) throws JsonProcessingException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.TEXT_PLAIN);
        HttpEntity<Object> entity = new HttpEntity<Object>(null, headers);

        StringBuffer url = new StringBuffer("https://api.weixin.qq.com/sns/jscode2session");
        url.append("?appid="); url.append(AppId);
        url.append("&secret="); url.append(AppSecret);
        url.append("&js_code="); url.append(code);
        url.append("&grant_type="); url.append("authorization_code");
        String body = RestTemplateUtils.geTemplate().exchange(url.toString(), HttpMethod.GET, entity, String.class).getBody();

        if (body != null){
            return new ObjectMapper().readValue(body,WeChatResponse.class);
        }
        return null;

    }


}
