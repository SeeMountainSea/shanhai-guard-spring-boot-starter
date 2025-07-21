package com.wangshanhai.examples.api;

import com.wangshanhai.examples.domain.RespInfo;
import com.wangshanhai.examples.domain.TUser;
import com.wangshanhai.guard.annotation.MethodDecryptResult;
import com.wangshanhai.guard.annotation.MethodEncryptParam;
import com.wangshanhai.guard.annotation.MethodEncryptRule;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 方法级参数加解密
 * @author Fly.Sky
 */
@RestController
@RequestMapping("/api/methodsguard")
public class MethodGuardController {

    @MethodEncryptParam(
            rules = {@MethodEncryptRule(targetIndex = 0)}
    )
    @MethodDecryptResult
    @RequestMapping("/queryMethodGuardField")
    public TUser queryMethodGuardField(@RequestBody RespInfo respInfo){
        return TUser.builder().id(1L).name("张三").build();
    }

    @MethodEncryptParam(
            rules = {@MethodEncryptRule(targetIndex = 0,targetType = 2,ruleId = "queryObj")}
    )
    @MethodDecryptResult
    @RequestMapping("/queryObj")
    public TUser queryObj(@RequestBody String respInfo){
        return TUser.builder().id(1L).name("张三").build();
    }
}
