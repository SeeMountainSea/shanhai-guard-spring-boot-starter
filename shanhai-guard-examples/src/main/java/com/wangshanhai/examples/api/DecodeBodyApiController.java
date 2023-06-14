package com.wangshanhai.examples.api;

import cn.hutool.json.JSONObject;
import com.wangshanhai.examples.domain.RespInfo;
import com.wangshanhai.guard.annotation.DecodeBody;
import com.wangshanhai.guard.annotation.EncodeBody;
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
    @RequestMapping(value = "/decodeAll")
    @ResponseBody
    public String decodeAll(@RequestBody String body){
        return body;
    }

    @RequestMapping(value = "/decodeOne")
    @DecodeBody(ruleId = "demio")
    @ResponseBody
    public String decodeOne(@RequestBody String body){
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

    @RequestMapping(value = "/encodeAll")
    @EncodeBody(ruleId = "demo")
    public JSONObject encode(){
        JSONObject resp=new JSONObject();
        resp.set("name","张三");
        return resp;
    }
    @RequestMapping(value = "/encodeOne")
    @ResponseBody
    @EncodeBody(ruleId = "demo")
    public JSONObject encodeOne(){
        JSONObject resp=new JSONObject();
        resp.set("name","张三");
        return resp;
    }
}
