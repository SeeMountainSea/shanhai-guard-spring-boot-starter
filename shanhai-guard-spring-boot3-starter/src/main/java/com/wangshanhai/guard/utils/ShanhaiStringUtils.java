package com.wangshanhai.guard.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 字符串处理类
 *
 * @author Fly.Sky
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

    /**
     * 清理文本中的特殊符号
     * @param source 原始文本
     * @param replace 符号替换内容，可以为空字符串
     * @return
     */
    public static String cleanSymbol(String source,String replace){
        //可以在中括号内加上任何想要替换的字符
        String regEx="[\n`~!@#$%^&*()+=|{}':;',\\[\\].<>/?~！@#￥%……&*（）——+|{}【】‘；：”“’。， 、？]";
        Pattern p = Pattern.compile(regEx);
        //这里把想要替换的字符串传进来
        Matcher m = p.matcher(source);
        return  m.replaceAll(replace).trim();
    }
}
