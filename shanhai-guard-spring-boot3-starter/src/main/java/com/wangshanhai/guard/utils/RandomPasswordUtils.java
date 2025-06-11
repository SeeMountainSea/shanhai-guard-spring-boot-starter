package com.wangshanhai.guard.utils;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class RandomPasswordUtils {
    /**
     * 定义二维数组
     */
    private static final char[][] PWD_DIC_ARR = new char[][]{{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'},
            {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'},
            {'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'},
            {'!','@','#','$','_','^'}};

    private static final Random vRandom = new Random();

    /**
     * 生成密码
     * @param len 生产密码长度
     * @return
     */
    public static String generate(int len) {
        StringBuilder stringBuilder = new StringBuilder();
        // 二维数组中的一维元素下标，记录其是否已使用过
        Set<Integer> tSet = new HashSet<>(4);
        int oidx,tidx;
        boolean flag = true;
        while(flag){
            // 检查在生成最后两位密码字符时，是否已使用过所有字符类型
            if((tSet.size() != 4)&&(stringBuilder.length()==(len - 2) || stringBuilder.length()==(len - 1))){
                // 强制使用未使用的字符类型，获取其一维下标
                oidx = getElseTypeIdx(tSet,PWD_DIC_ARR.length);
                tSet.add(oidx);
                tidx = vRandom.nextInt(PWD_DIC_ARR[oidx].length);
                stringBuilder.append(PWD_DIC_ARR[oidx][tidx]);
            }else if(stringBuilder.length() == len){
                flag = false;
            }else {
                oidx = vRandom.nextInt(PWD_DIC_ARR.length);
                tSet.add(oidx);
                tidx = vRandom.nextInt(PWD_DIC_ARR[oidx].length);
                stringBuilder.append(PWD_DIC_ARR[oidx][tidx]);
            }
        }
        return stringBuilder.toString();
    }

    private static int getElseTypeIdx(Set<Integer> tSet, int size) {
        for (int i = 0; i < size; i++) {
            if(!tSet.contains(i)){
                return i;
            }
        }
        return 0;
    }


}
