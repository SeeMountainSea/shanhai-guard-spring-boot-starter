package com.wangshanhai.guard.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * 字符串处理类
 *
 * @author Fly.Sky
 * @since 2023/6/4 17:01
 */
public class ShanhaiStringUtils {

    /**
     * 字符串按序分组（指定长度）
     * @param source 源字符串
     * @param groupSize 分组长度
     * @return
     */
    public static List<String> sequentialGrouping (String source, int groupSize){
        List<String> resp=new ArrayList<>();
        int start=0;
        int end=source.length()-groupSize+1;
        while (start!=end){
            resp.add( source.substring(start,groupSize+start));
            start++;
        }
        return resp;
    }
}
