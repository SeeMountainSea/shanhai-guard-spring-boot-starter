package com.wangshanhai.examples.api;

import com.wangshanhai.examples.domain.TUser;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;



/**
 *
 * 敏感词检测
 * @author Shmily
 */
@Controller
@RequestMapping("/api/snsitive")
public class SensitivewordsController {

    @RequestMapping(value = "/query")
    @ResponseBody
    public String query(HttpServletRequest request){
        return request.getParameter("name");
    }

    @RequestMapping(value = "/queryParam")
    @ResponseBody
    public TUser queryParam(@RequestParam(name = "file",required = false) MultipartFile file, TUser form){
//        System.out.println(file.getOriginalFilename());
        form.setUserDesc(file==null?"文件为空":file.getOriginalFilename());
        return form;
    }

    @RequestMapping(value = "/queryBody")
    @ResponseBody
    public TUser queryBody(@RequestBody TUser tUser){
        return tUser;
    }
}
