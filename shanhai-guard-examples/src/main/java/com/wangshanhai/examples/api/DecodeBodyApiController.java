package com.wangshanhai.examples.api;

import com.wangshanhai.examples.domain.RespInfo;
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
    /**
     * 测试响应报文数据处理
     * @return
     */
    @RequestMapping(value = "/resp")
    @ResponseBody
    public RespInfo resp(){
        RespInfo respInfo=RespInfo.builder().code(200).msg("success").text("大王来巡山").build();
        return respInfo;
    }
}
