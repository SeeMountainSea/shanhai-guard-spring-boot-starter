package com.wangshanhai.examples.api;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.wangshanhai.examples.domain.TUser;
import com.wangshanhai.examples.mapper.TUserMapper;
import com.wangshanhai.examples.service.TUserService;
import com.wangshanhai.guard.annotation.SensitiveTextBody;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * 数据防护
 *
 * @author Fly.Sky
 * @since 2023/7/23 16:17
 */
@Controller
@RequestMapping("/api")
public class DataGuardApiController {
    @Autowired
    private TUserService tUserService;
    @Autowired
    private TUserMapper tUserMapper;

    @RequestMapping(value = "/queryPlus/{id}")
    @ResponseBody
    public TUser queryPlus(@PathVariable("id") Long id){
        return tUserService.getOne(new QueryWrapper<TUser>().eq("id",id).eq("name","张三"));
    }

    @RequestMapping(value = "/queryMapper/{id}")
    @ResponseBody
    public TUser queryMapper(@PathVariable("id") Long id){
        return tUserMapper.selectOneById(TUser.builder().id(id).build());
    }
    @RequestMapping(value = "/queryPlusList")
    @ResponseBody
    public List<TUser> queryPlusList(){
        List<TUser> list=tUserService.list(new QueryWrapper<TUser>());
        for(TUser tUser:list){
            tUserService.getById(tUser.getId());
        }
        return tUserService.list(new QueryWrapper<TUser>().eq("name","张三"));
    }

    @RequestMapping(value = "/queryMapperList")
    @ResponseBody
    public List<TUser> queryMapperList(){
        return tUserMapper.queryMapperList(TUser.builder().name("张三").build());
    }
    @RequestMapping(value = "/addPlus")
    @ResponseBody
    public String addPlus(@RequestBody TUser tUser){
        tUserService.save(tUser);
        return "success";
    }

    @RequestMapping(value = "/addMapper")
    @ResponseBody
    public String addMapper(@RequestBody TUser tUser){
        tUserMapper.saveBySql(tUser);
        return "success";
    }

    @RequestMapping(value = "/updatePlus")
    @ResponseBody
    public String updatePlus(@RequestBody TUser tUser){
        tUserService.updateById(tUser);
        return "success";
    }

    @RequestMapping(value = "/updateMapper")
    @SensitiveTextBody
    @ResponseBody
    public String updateMapper(@RequestBody TUser tUser){
        tUserMapper.updateBySql(tUser);
        return "success";
    }

}
