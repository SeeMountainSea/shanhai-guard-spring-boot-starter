package com.wangshanhai.examples.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.wangshanhai.guard.annotation.FieldDataGuard;
import com.wangshanhai.guard.annotation.ShanHaiDataGuard;
import com.wangshanhai.guard.dataplug.DataExecModel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * User
 *
 * @author Fly.Sky
 * @since 2023/7/23 16:23
 */
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Data
@TableName("t_user")
@ShanHaiDataGuard
public class TUser implements Serializable {

    private static final long serialVersionUID = 1L;
    /**
     * id
     */
    @TableId(type = IdType.AUTO)
    private Long id;
    /**
     * name
     */
    private String name;

    /**
     * create_time
     */
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(timezone = "GMT+8", pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;

    /**
     * desc
     */
    @FieldDataGuard(ruleId = "dictLabel",encrypt = true,
            encryptMethod = "sa",encryptExecModel = DataExecModel.INSERTANDUPDATE,
            decrypt = true,decryptMethod = "sa",hyposensit = true,hyposensitMethod = "sa",hyposensitExecModel = DataExecModel.SELECT
    )
    private String userDesc;



}