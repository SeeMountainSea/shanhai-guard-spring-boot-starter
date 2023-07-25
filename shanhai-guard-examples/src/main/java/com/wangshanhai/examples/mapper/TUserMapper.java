package com.wangshanhai.examples.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wangshanhai.examples.domain.TUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * User
 *
 * @author Fly.Sky
 * @since 2023/7/23 16:30
 */
public interface TUserMapper extends BaseMapper<TUser> {
    TUser selectOneById(@Param("userForm")TUser userForm);
    List<TUser> queryMapperList(@Param("userForm")TUser userForm);
    void saveBySql(@Param("userForm")TUser userForm);

    void updateBySql(@Param("userForm")TUser userForm);
}

