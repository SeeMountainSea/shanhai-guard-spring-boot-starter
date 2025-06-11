package com.wangshanhai.guard.dataplug;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author Shmily
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HyposensitRule implements Serializable {
    private static final long serialVersionUID = 1L;
    /**
     * 规则ID
     */
    private String ruleId;
    /**
     * 脱敏正则
     */
    private String regex;
    /**
     * 脱敏展示效果
     */
    private String replacement;
}
