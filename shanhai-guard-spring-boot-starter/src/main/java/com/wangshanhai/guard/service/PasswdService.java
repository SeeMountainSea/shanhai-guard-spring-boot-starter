package com.wangshanhai.guard.service;

import com.wangshanhai.guard.config.PasswdGuardConfig;
import com.wangshanhai.guard.utils.Logger;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 密码复杂度检测服务
 */
public class PasswdService {
    /**
     * 数字
     */
    private String REG_NUMBER = ".*\\d+.*";
    /**
     * 大写字母
     */
    private String REG_UPPERCASE = ".*[A-Z]+.*";
    /**
     * 小写字母
     */
    private String REG_LOWERCASE = ".*[a-z]+.*";
    /**
     * 特殊符号(~!@#$%^&*()_+|<>,.?/:;'[]{}\)
     */
    private  String REG_SYMBOL = ".*[~!@#$%^&*()_+|<>,.?/:;'\\[\\]{}\"]+.*";
    /**
     * 键盘字符表(小写)
     * 非shift键盘字符表
     */
    private  char[][] CHAR_TABLE1 = new char[][]{
            {'1', '2', '3', '4', '5', '6', '7', '8', '9', '0', '-', '=', '\0'},
            {'q', 'w', 'e', 'r', 't', 'y', 'u', 'i', 'o', 'p', '[', ']', '\\'},
            {'a', 's', 'd', 'f', 'g', 'h', 'j', 'k', 'l', ';', '\'', '\0', '\0'},
            {'z', 'x', 'c', 'v', 'b', 'n', 'm', ',', '.', '/', '\0', '\0', '\0'}};
    /**
     * shift键盘的字符表
     */
    private  char[][] CHAR_TABLE2 = new char[][]{
            {'!', '@', '#', '$', '%', '^', '&', '*', '(', ')', '_', '+', '\0'},
            {'q', 'w', 'e', 'r', 't', 'y', 'u', 'i', 'o', 'p', '{', '}', '|'},
            {'a', 's', 'd', 'f', 'g', 'h', 'j', 'k', 'l', ':', '"', '\0', '\0'},
            {'z', 'x', 'c', 'v', 'b', 'n', 'm', '<', '>', '?', '\0', '\0', '\0'}};

    private PasswdGuardConfig passwdConfig;

    public PasswdService(PasswdGuardConfig passwdConfig) {
        this.passwdConfig = passwdConfig;
    }

    /**
     * 检测密码复杂度是否符合要求
     * @param sourcePasswd  原始密码
     * @return
     */
    public boolean checkPasswd(String sourcePasswd){
         boolean checkResult=true;
         if(sourcePasswd.length()<passwdConfig.getMinLength()){
             Logger.info("[Passwd-check-alert]-最小长度应为:{}",passwdConfig.getMinLength());
             checkResult=false;
         }
         if(sourcePasswd.length()>passwdConfig.getMaxLength()){
            Logger.info("[Passwd-check-alert]-最大长度应为:{}",passwdConfig.getMaxLength());
            checkResult=false;
         }
         if(passwdConfig.getCharacterExist()&&!(sourcePasswd.matches(REG_LOWERCASE)&&sourcePasswd.matches(REG_UPPERCASE))){
             Logger.info("[Passwd-check-alert]-必须包含大小写字母");
             checkResult=false;
         }
         if(passwdConfig.getNumberExist()&&!(sourcePasswd.matches(REG_NUMBER))){
            Logger.info("[Passwd-check-alert]-必须包含数字");
            checkResult=false;
         }
        if(passwdConfig.getSymbolExist()&&!(sourcePasswd.matches(REG_SYMBOL))){
            Logger.info("[Passwd-check-alert]-必须包含特殊字符");
            checkResult=false;
        }
        if(passwdConfig.getKeyboardNotExist()&&isKeyBoardContinuousChar(sourcePasswd)){
            Logger.info("[Passwd-check-alert]-不能包含键盘连续字符");
            checkResult=false;
        }
        if(passwdConfig.getAllSameNotExist()&&isContinuousChar(sourcePasswd,passwdConfig.getAllSameNum(),1)){
            Logger.info("[Passwd-check-alert]-不能包含{}个相同字符",passwdConfig.getAllSameNum());
            checkResult=false;
        }
        if(passwdConfig.getSeqSameNotExist()&&isContinuousChar(sourcePasswd,passwdConfig.getSeqSameNum(),2)){
            Logger.info("[Passwd-check-alert]-不能包含{}个连续字符",passwdConfig.getSeqSameNum());
            checkResult=false;
        }
        return checkResult;
    }
    /**
     * 是否包含3个及以上键盘连续字符
     * @param sourcePasswd 待匹配的字符串
     */
    private  boolean isKeyBoardContinuousChar(String sourcePasswd) {
        if (StringUtils.isEmpty(sourcePasswd)) {
            return false;
        }
        //考虑大小写，都转换成小写字母
        char[] lpStrChars = sourcePasswd.toLowerCase().toCharArray();

        // 获取字符串长度
        int nStrLen = lpStrChars.length;
        // 定义位置数组：row - 行，col - column 列
        int[] pRowCharPos = new int[nStrLen];
        int[] pColCharPos = new int[nStrLen];
        for (int i = 0; i < nStrLen; i++) {
            char chLower = lpStrChars[i];
            pColCharPos[i] = -1;
            // 检索在表1中的位置，构建位置数组
            for (int nRowTable1Idx = 0; nRowTable1Idx < 4; nRowTable1Idx++) {
                for (int nColTable1Idx = 0; nColTable1Idx < 13; nColTable1Idx++) {
                    if (chLower == CHAR_TABLE1[nRowTable1Idx][nColTable1Idx]) {
                        pRowCharPos[i] = nRowTable1Idx;
                        pColCharPos[i] = nColTable1Idx;
                    }
                }
            }
            // 在表1中没找到，到表二中去找，找到则continue
            if (pColCharPos[i] >= 0) {
                continue;
            }
            // 检索在表2中的位置，构建位置数组
            for (int nRowTable2Idx = 0; nRowTable2Idx < 4; nRowTable2Idx++) {
                for (int nColTable2Idx = 0; nColTable2Idx < 13; nColTable2Idx++) {
                    if (chLower == CHAR_TABLE2[nRowTable2Idx][nColTable2Idx]) {
                        pRowCharPos[i] = nRowTable2Idx;
                        pColCharPos[i] = nColTable2Idx;
                    }
                }
            }
        }

        // 匹配坐标连线
        for (int j = 1; j <= nStrLen - 2; j++) {
            //同一行
            if (pRowCharPos[j - 1] == pRowCharPos[j] && pRowCharPos[j] == pRowCharPos[j + 1]) {
                // 键盘行正向连续（asd）或者键盘行反向连续（dsa）
                if ((pColCharPos[j - 1] + 1 == pColCharPos[j] && pColCharPos[j] + 1 == pColCharPos[j + 1]) ||
                        (pColCharPos[j + 1] + 1 == pColCharPos[j] && pColCharPos[j] + 1 == pColCharPos[j - 1])) {
                    return true;
                }
            }
            //同一列
            if (pColCharPos[j - 1] == pColCharPos[j] && pColCharPos[j] == pColCharPos[j + 1]) {
                //键盘列连续（qaz）或者键盘列反向连续（zaq）
                if ((pRowCharPos[j - 1] + 1 == pRowCharPos[j] && pRowCharPos[j] + 1 == pRowCharPos[j + 1]) ||
                        (pRowCharPos[j - 1] - 1 == pRowCharPos[j] && pRowCharPos[j] - 1 == pRowCharPos[j + 1])) {
                    return true;
                }
            }
        }
        return false;
    }
    /**
     * 包含相同字符或连续字符
     * @param str 原始字符串
     * @param num 连续出现个数
     * @param type 判断类型 1：完全相同 2：连续数据
     * @return
     */
    private static boolean isContinuousChar(String str,int num,int type) {
        char[] chars = str.toCharArray();
        Map<Integer, List<Integer>> fz=new HashMap<>();
        for (int i = 0; i < chars.length+1 - num; i++) {
            List<Integer> charsTmpList=new ArrayList<>();
            for(int j=i;j<num+i;j++){
                charsTmpList.add((int) chars[j]);
            }
            fz.put(i,charsTmpList);
        }
        return  (type==1&& allSame(fz)) ||  (type==2&&  seqSame(fz));
    }
    private static boolean allSame(Map<Integer,List<Integer>> fz){
        int sameNum=fz.keySet().size();
        for(Integer k:fz.keySet()){
            List<Integer> charsTmpList=fz.get(k);
            if(charsTmpList==null){
                return false;
            }
            Integer c=null;
            for(Integer i:charsTmpList){
                if(c==null){
                    c=i;
                }else{
                    if(!i.equals(c)){
                        sameNum--;
                        break;
                    }
                }
            }
        }
        if(sameNum>0){
            return true;
        }
        return false;
    }

    private static boolean seqSame(Map<Integer,List<Integer>> fz){
        int sameNum=fz.keySet().size();
        for(Integer k:fz.keySet()){
            List<Integer> charsTmpList=fz.get(k);
            Integer bl=null;
            for(int i=0;i<charsTmpList.size()-1;i++){
                Integer blc= charsTmpList.get(i + 1) - charsTmpList.get(i);
                if(bl==null){
                    bl =blc;
                }else{
                    if(!blc.equals(bl)){
                        sameNum--;
                        break;
                    }
                }
            }
        }
        if(sameNum>0){
            return true;
        }
        return false;
    }
}
