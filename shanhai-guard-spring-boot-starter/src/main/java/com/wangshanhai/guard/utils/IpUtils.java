package com.wangshanhai.guard.utils;

import cn.hutool.core.net.NetUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.regex.Pattern;

/**
 * IPUtils
 *
 * @author Fly.Sky
 */
public class IpUtils {
    private static final Pattern IP_PATTERN = Pattern.compile(
            "^((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)$"
    );
    /**
     * 获取客户端真实IP地址
     * @param request HTTP请求
     * @param headers 消息头数组（按优先级顺序）
     * @return IP地址
     */
    public static String getClientIp(HttpServletRequest request, String[] headers) {
        if (headers != null) {
            for (String header : headers) {
                String ip = request.getHeader(header);
                if (isValidIp(ip)) {
                    // 处理多个IP的情况，取第一个非内网IP
                    ip = getFirstValidIp(ip);
                    if (isValidIp(ip)) {
                        return ip;
                    }
                }
            }
        }
        // 如果header中没有获取到，使用默认方式
        String ip = request.getRemoteAddr();
        if ("0:0:0:0:0:0:0:1".equals(ip) || "127.0.0.1".equals(ip)) {
            ip = "127.0.0.1";
        }
        return ip;
    }

    /**
     * 获取第一个有效的IP
     */
    private static String getFirstValidIp(String ips) {
        if (!StringUtils.hasText(ips)) {
            return null;
        }
        String[] ipArray = ips.split(",");
        for (String ip : ipArray) {
            ip = ip.trim();
            if (isValidIp(ip)) {
                return ip;
            }
        }
        return null;
    }

    /**
     * 验证IP格式是否正确
     */
    public static boolean isValidIp(String ip) {
        if (!StringUtils.hasText(ip)) {
            return false;
        }
        // 处理IPv6格式的localhost
        if ("0:0:0:0:0:0:0:1".equals(ip)) {
            return true;
        }
        return IP_PATTERN.matcher(ip).matches();
    }
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
