package com.wangshanhai.examples.domain;

import com.wangshanhai.guard.annotation.RespFieldGuard;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
//@RespDataGuard
public class RespInfo {
    @RespFieldGuard(ruleId = "text")
    private String text;
    private String msg;
    private Integer code;
}

