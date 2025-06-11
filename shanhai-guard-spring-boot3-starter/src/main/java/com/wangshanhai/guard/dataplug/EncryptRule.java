package com.wangshanhai.guard.dataplug;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Map;

/**
 * @author Shmily
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EncryptRule implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 规则ID
     */
    private String ruleId;
    /**
     * 加解密所需参数
     */
    private Map<String,Object> ruleParams;

}
