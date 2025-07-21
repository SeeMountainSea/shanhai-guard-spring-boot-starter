package com.wangshanhai.examples.api;

import com.wangshanhai.examples.domain.RespInfo;
import com.wangshanhai.examples.domain.TUser;
import com.wangshanhai.guard.annotation.MethodGuardResult;
import com.wangshanhai.guard.annotation.MethodGuardParam;
import com.wangshanhai.guard.annotation.MethodGuardParamRule;
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

    @MethodGuardParam(
            rules = {@MethodGuardParamRule(targetIndex = 0)}
    )
    @MethodGuardResult
    @RequestMapping("/queryMethodGuardField")
    public TUser queryMethodGuardField(@RequestBody RespInfo respInfo){
        return TUser.builder().id(1L).name("张三").build();
    }

    @MethodGuardParam(
            rules = {@MethodGuardParamRule(targetIndex = 0,targetType = 2,ruleId = "queryObj")}
    )
    @MethodGuardResult
    @RequestMapping("/queryObj")
    public TUser queryObj(@RequestBody String respInfo){
        return TUser.builder().id(1L).name("张三").build();
    }
}
