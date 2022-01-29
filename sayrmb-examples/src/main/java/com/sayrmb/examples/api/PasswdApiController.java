package com.sayrmb.examples.api;

import com.sayrmb.guard.service.PasswdService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/api")
public class PasswdApiController {
    @Autowired
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
