package com.wangshanhai.guard.license;

import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.NetworkInterface;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 硬件信息获取
 * @author license
 */
public class HardwareUtil {
    /**
     * 获取机器码
     * @return
     * @throws Exception
     */
    public static String getMachineId()  {
        try{
            // 生成稳定指纹
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            byte[] digest = md.digest(getMachineInfo(false).getBytes());
            // 32字符指纹
            return HexUtil.encodeHexStr(digest).substring(0,  32);
        }catch (Exception e){
            System.out.println("信息获取异常:"+e.getMessage());
        }
        return "-";
    }

    /**
     * 获取硬件信息
     * @param isShow 打印硬件信息
     * @return
     * @throws Exception
     */
    public static String getMachineInfo(boolean isShow) {
        JSONObject hardwareInfo=new JSONObject();
        // 系统基础信息
        hardwareInfo.put("osName",System.getProperty("os.name"));
        hardwareInfo.put("osArch",System.getProperty("os.arch"));
        try{
            // 网络接口标识（物理接口优先）
            Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
            List<String>  macList=new ArrayList<>();
            while (nets.hasMoreElements())  {
                NetworkInterface net = nets.nextElement();
                if (isPhysicalInterface(net)) {
                    byte[] mac = net.getHardwareAddress();
                    if (mac != null) {
                        if(isShow){
                            System.out.println("mac|"+net.getName()+"|"+formatMac(mac));
                        }
                        macList.add(formatMac(mac));
                    }
                }
            }
            macList.sort(Comparator.naturalOrder());
            hardwareInfo.put("mac",macList);
        }catch (Exception e){
            System.out.println("信息获取异常:"+e.getMessage());
        }
        //存储设备标识
        String diskId = getDiskSerial();
        if (diskId != null) {
            hardwareInfo.put("diskId",StrUtil.cleanBlank(diskId));
        }
        //CPU信息
        String cpuId = getCpuId();
        if (cpuId != null) {
            hardwareInfo.put("cpuId", StrUtil.cleanBlank(cpuId));
        }
        if(isShow){
            System.out.println("硬件信息:\n"+hardwareInfo.toJSONString());
        }
        return hardwareInfo.toJSONString();
    }
    private static String formatMac(byte[] mac) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mac.length; i++) {
            sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? ":" : ""));
        }
        return sb.toString();
    }
    private static boolean isPhysicalInterface(NetworkInterface net)  {
        try{
            return !net.isLoopback()  &&
                    !net.isVirtual()  &&
                    net.isUp()  &&
                    !net.getDisplayName().contains("Virtual");
        }catch (Exception e){
            System.out.println("信息获取异常:"+e.getMessage());
        }
        return false;
    }

    private static String getDiskSerial() {
        try {
            String os = System.getProperty("os.name").toLowerCase();
            String command = os.contains("win")  ?
                    "wmic diskdrive get SerialNumber" :
                    "lsblk -d -o serial";

            Process p = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            return reader.lines().collect(Collectors.joining());
        } catch (Exception e) {
            return null;
        }
    }

    private static String getCpuId() {
        try {
            String os = System.getProperty("os.name").toLowerCase();
            String command = os.contains("win")  ?
                    "wmic cpu get ProcessorId" :
                    "dmidecode -t processor | grep ID";

            Process p = Runtime.getRuntime().exec(command);
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            return reader.lines().collect(Collectors.joining());
        } catch (Exception e) {
            return null;
        }
    }
}
