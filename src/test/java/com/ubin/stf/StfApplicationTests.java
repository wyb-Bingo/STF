package com.ubin.stf;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ubin.stf.service.AdminService;
import com.ubin.stf.service.DepartmentService;
import com.ubin.stf.utils.RestTemplateUtils;
import com.ubin.stf.utils.WeChatRequestUtils;
import com.ubin.stf.utils.WeChatResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashMap;
import java.util.Map;

@SpringBootTest
class StfApplicationTests {

    @Value("${weiXin.AppID}")
    String AppId;

    @Value("${weiXin.AppSecret}")
    String AppSecret;

    @Autowired
    AdminService adminService;

    @Autowired
    DepartmentService departmentService;

    @Test
    void TestAddAdmin(){
        adminService.insertAdminIfNoExists(2);
    }

    @Test
    void TestGetDepartment(){
        departmentService.getAllDepartmentWithChildren();
    }

    @Test
    void TestPassword(){
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encode = passwordEncoder.encode("123456");
        System.out.println(encode);

    }

    @Test
    void contextLoads() throws JsonProcessingException {
//        Map<String,String> map =  new HashMap<>();
//        map.put("appid",AppId);
//        map.put("secret",AppSecret);
//        map.put("js_code","063miy2w3zE8nV2LGX0w3TagIS3miy2w");
//        map.put("grant_type","authorization_code");
//        ResponseEntity<Object> responseEntity = RestTemplateUtils.getForEntity("https://api.weixin.qq.com/sns/jscode2session", null
//                , map);
//        HttpHeaders headers = new HttpHeaders();
//        headers.setContentType(MediaType.TEXT_PLAIN);
//        HttpEntity<String> entity = new HttpEntity<String>(null, headers);
//
//        StringBuffer url = new StringBuffer("https://api.weixin.qq.com/sns/jscode2session");
//        url.append("?appid="); url.append(AppId);
//        url.append("&secret="); url.append(AppSecret);
//        url.append("&js_code="); url.append("063miy2w3zE8nV2LGX0w3TagIS3miy2w");
//        url.append("&grant_type="); url.append("authorization_code");

        //ResponseEntity<String> responseEntity = RestTemplateUtils.exchange(url.toString(), HttpMethod.GET, entity, null, null);
//        ResponseEntity<String> responseEntity = RestTemplateUtils.geTemplate().exchange(url.toString(), HttpMethod.GET, entity, String.class);



//        String Headers = new ObjectMapper().writeValueAsString( responseEntity.getHeaders());
//        String body = new ObjectMapper().writeValueAsString( responseEntity.getBody());
//        String code = new ObjectMapper().writeValueAsString( responseEntity.getStatusCode());

        WeChatResponse weChatResponse = WeChatRequestUtils.jsCode2session(AppId, AppSecret, "063miy2w3zE8nV2LGX0w3TagIS3miy2w");
        System.out.println(weChatResponse.toString());


    }

}
