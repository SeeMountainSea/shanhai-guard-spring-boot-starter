package com.wangshanhai.guard.interceptor;

import com.wangshanhai.guard.config.MysqlGuardConfig;
import com.wangshanhai.guard.utils.Logger;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.*;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.Properties;

/**
 * SQL 查询拦截器 (mysql定制)
 * @author Shmily
 */
@Intercepts({
        @Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class})
})
public class MysqlSQLScanInterceptor  implements Interceptor {
    private MysqlGuardConfig mysqlGuardConfig;

    public MysqlSQLScanInterceptor(MysqlGuardConfig mysqlGuardConfig) {
        this.mysqlGuardConfig = mysqlGuardConfig;
    }

    /**
     *    拦截器拦截后对象后，执行自己的业务逻辑
     */
    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
        BoundSql boundSql = statementHandler.getBoundSql();
        String sql = boundSql.getSql();
        sql=sql.toLowerCase();
        int whereIndex=sql.lastIndexOf("where");
        //SQL语句必须包含where 语句
        if(mysqlGuardConfig.getWhereExist()&&whereIndex==-1){
            Logger.info("[MysqlSQLScan-Guard-Alert]-sql:{}",sql);
            sql=sql+" where 1=1 ";
            whereIndex=sql.lastIndexOf("where");
        }
        int limitIndex=sql.lastIndexOf("limit");
        //SQL语句必须包含LIMIT
        if(mysqlGuardConfig.getLimitExist()&&!(limitIndex>whereIndex)){
            //通过反射修改sql语句
            String ctgDBSQL=sql+" limit "+mysqlGuardConfig.getQueryLimit();
            Field field = boundSql.getClass().getDeclaredField("sql");
            field.setAccessible(true);
            field.set(boundSql, ctgDBSQL);
        }
        return invocation.proceed(); //程序继续运行
    }
    @Override
    public Object plugin(Object target) {
       /*
            根据Intercepts注解，拦截 StatementHandler 的prepare 方法
         */
        return Plugin.wrap(target, this);
    }
    @Override
    public void setProperties(Properties properties) {
    }
}
