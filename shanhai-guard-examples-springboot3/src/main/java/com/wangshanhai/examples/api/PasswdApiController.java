package com.wangshanhai.examples.api;

import com.wangshanhai.guard.service.PasswdService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;



@Controller
@RequestMapping("/api")
public class PasswdApiController {
    @Autowired(required = false)
    private PasswdService passwdService;

    /**
     * 校验密码复杂度
     * @param request
     * @return
     */
    @RequestMapping(value = "/passwd")
    @ResponseBody
    public String checkPasswd(HttpServletRequest request){
        String passwd=request.getParameter("key");
        return String.valueOf(passwdService.checkPasswd(passwd));
    }

}
