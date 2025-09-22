package com.wangshanhai.guard.utils;

import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.StrUtil;

/**
 * IPUtils
 *
 * @author Fly.Sky
 */
public class IpUtils {
    /**
     * 判断IP是否在指定范围内
     *
     * @param targetIp 目标IP
     * @param rule     规则 如 192.168.1.1-192.168.2.254 | 192.168.1.0/24 | 192.168.1.1
     * @return {@code true} 在指定范围内;{@code false} 不在指定范围内
     */
    public static boolean isIpInRange(String targetIp, String rule) {
        if(StrUtil.isEmpty(targetIp) || StrUtil.isEmpty(rule)){
            return false;
        }
        if (rule.contains("/")) {
            return NetUtil.isInRange(targetIp, rule);
        }
        if (rule.contains("-")) {
            String[] ips = rule.split("-");
            long ipStart = NetUtil.ipv4ToLong(ips[0].trim());
            long ipEnd = NetUtil.ipv4ToLong(ips[1].trim());
            long ipTarget = NetUtil.ipv4ToLong(targetIp);
            return ipTarget >= ipStart && ipTarget <= ipEnd;
        }
        return NetUtil.ipv4ToLong(targetIp) == NetUtil.ipv4ToLong(rule);
    }
}
