package com.wangshanhai.examples.api;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * 自定义Body
 */
@Controller
@RequestMapping("/api")
public class DecodeBodyApiController {

    /**
     * 自定义Body解密规则
     * @param body
     * @return
     */
    @RequestMapping(value = "/decode")
    @ResponseBody
    public String decode(@RequestBody String body){
        return body;
    }
}
