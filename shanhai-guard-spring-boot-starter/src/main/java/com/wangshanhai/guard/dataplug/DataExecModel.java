package com.wangshanhai.guard.dataplug;

/**
 * 数据生效范围
 */
public class DataExecModel {
    /**
     * 新增
     */
    public static final Integer SAVE =1;
    /**
     * 更新
     */
    public static final Integer UPDATE =2;
    /**
     * 查询
     */
    public static final Integer QUERY =3;
    /**
     * 新增&更新
     */
    public static final Integer SAVEANDUPDATE =4;
    /**
     * 新增&查询
     */
    public static final Integer SAVEANDQUERY =5;
    /**
     * 更新&查询
     */
    public static final Integer UPDATEANDQUERY =6;
}
