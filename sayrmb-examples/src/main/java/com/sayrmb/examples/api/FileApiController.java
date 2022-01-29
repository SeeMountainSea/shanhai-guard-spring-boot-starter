package com.sayrmb.examples.api;

import com.sayrmb.guard.annotation.FileGuard;
import com.sayrmb.guard.annotation.FileType;
import com.sayrmb.guard.utils.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/api")
public class FileApiController {
    /**
     * 文件上传测试样例
     * @param request
     * @return
     */
    @RequestMapping(value = "/file/common/upload")
    @ResponseBody
    public String common(HttpServletRequest request, MultipartFile file){
        Logger.info("[file-upload-api]-name:{},contentType:{},size:{}",file.getName(),file.getContentType(),file.getSize());
        return "success";
    }
    /**
     * 文件上传测试样例
     * @param request
     * @return
     */
    @RequestMapping(value = "/file/bytes/upload")
    @FileGuard(message = "只能上传图片文件",type = FileGuard.GuardType.BYTES,supportedFileTypes = {FileType.JPEG,FileType.PNG})
    @ResponseBody
    public String bytes(HttpServletRequest request, MultipartFile file){
        Logger.info("[file-upload-api]-name:{},contentType:{},size:{}",file.getName(),file.getContentType(),file.getSize());
        return "success";
    }
    /**
     * 文件上传测试样例
     * @param request
     * @return
     */
    @RequestMapping(value = "/file/suffix/upload")
    @FileGuard(message = "只能上传图片文件",type = FileGuard.GuardType.SUFFIX,supportedSuffixes = {"png", "jpg", "jpeg"})
    @ResponseBody
    public String suffix(HttpServletRequest request, MultipartFile file){
        Logger.info("[file-upload-api]-name:{},contentType:{},size:{}",file.getName(),file.getContentType(),file.getSize());
        return "success";
    }
}
