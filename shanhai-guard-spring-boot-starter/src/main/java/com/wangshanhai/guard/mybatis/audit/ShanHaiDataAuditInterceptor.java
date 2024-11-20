package com.wangshanhai.guard.mybatis.audit;

import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfoHelper;
import com.baomidou.mybatisplus.core.toolkit.PluginUtils;
import com.baomidou.mybatisplus.extension.plugins.inner.InnerInterceptor;
import com.wangshanhai.guard.config.DataAuditConfig;
import com.wangshanhai.guard.mybatis.audit.result.*;
import com.wangshanhai.guard.service.ShanHaiDataAuditService;
import com.wangshanhai.guard.utils.Logger;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.JdbcParameter;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.*;
import net.sf.jsqlparser.statement.update.Update;
import net.sf.jsqlparser.statement.update.UpdateSet;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.scripting.defaults.DefaultParameterHandler;

import java.sql.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
/**
 * 数据操作审计
 * Ref: Mybatis-Plus DataChangeRecorderInnerInterceptor
 * @author Fly.Sky
 */
public class ShanHaiDataAuditInterceptor implements InnerInterceptor {
    private ShanHaiDataAuditService shanHaiDataAuditService;
    private DataAuditConfig dataAuditConfig;
    public ShanHaiDataAuditInterceptor(ShanHaiDataAuditService shanHaiDataAuditService,DataAuditConfig dataAuditConfig) {
        this.shanHaiDataAuditService = shanHaiDataAuditService;
        this.dataAuditConfig=dataAuditConfig;
        this.setProperties(null);
    }

    @SuppressWarnings("unused")
    public static final String IGNORED_TABLE_COLUMN_PROPERTIES = "ignoredTableColumns";

    private final Map<String, Set<String>> ignoredTableColumns = new ConcurrentHashMap<>();
    private final Set<String> ignoreAllColumns = new HashSet<>();//全部表的这些字段名，INSERT/UPDATE都忽略，delete暂时保留

    @Override
    public void beforePrepare(StatementHandler sh, Connection connection, Integer transactionTimeout) {
        PluginUtils.MPStatementHandler mpSh = PluginUtils.mpStatementHandler(sh);
        MappedStatement ms = mpSh.mappedStatement();
        final BoundSql boundSql = mpSh.boundSql();
        SqlCommandType sct = ms.getSqlCommandType();
        if (sct == SqlCommandType.INSERT || sct == SqlCommandType.UPDATE || sct == SqlCommandType.DELETE) {
            PluginUtils.MPBoundSql mpBs = mpSh.mPBoundSql();
            OperationResult operationResult;
            long startTs = System.currentTimeMillis();
            try {
                Statement statement = CCJSqlParserUtil.parse(mpBs.sql());
                if (statement instanceof Insert) {
                    operationResult = processInsert((Insert) statement, mpSh.boundSql());
                } else if (statement instanceof Update) {
                    operationResult = processUpdate((Update) statement, ms, boundSql, connection);
                } else if (statement instanceof Delete) {
                    operationResult = processDelete((Delete) statement, ms, boundSql, connection);
                } else {
                   Logger.info("other operation sql={}", mpBs.sql());
                    return;
                }
            } catch (Exception e) {
                Logger.error("Unexpected error for mappedStatement={}, sql={}", ms.getId(), mpBs.sql(), e);
                return;
            }
            long costThis = System.currentTimeMillis() - startTs;
            if (operationResult != null) {
                operationResult.setCost(costThis);
                dealOperationResult(operationResult);
            }
        }
    }

    /**
     * 判断哪些SQL需要处理
     * 默认INSERT/UPDATE/DELETE语句
     *
     * @param sql
     * @return
     */
    protected boolean allowProcess(String sql) {
        String sqlTrim = sql.trim().toUpperCase();
        return sqlTrim.startsWith("INSERT") || sqlTrim.startsWith("UPDATE") || sqlTrim.startsWith("DELETE");
    }

    /**
     * 处理数据更新结果，默认打印
     *
     * @param operationResult
     */
    protected void dealOperationResult(OperationResult operationResult) {
        Logger.info("[ShanhaiDataAudit]-db-change-data:{}", operationResult);
        shanHaiDataAuditService.dealOperationResult(operationResult);
    }

    public OperationResult processInsert(Insert insertStmt, BoundSql boundSql) {
        OperationResult result = new OperationResult();
        result.setOperation("insert");
        result.setTableName(insertStmt.getTable().getName());
        result.setRecordStatus(true);
        result.buildDataStr(compareAndGetUpdatedColumnDatas(result.getTableName(), boundSql, insertStmt, null));
        return result;
    }

    public OperationResult processUpdate(Update updateStmt, MappedStatement mappedStatement, BoundSql boundSql, Connection connection) {
        Expression where = updateStmt.getWhere();
        Select selectStmt = new Select();
        PlainSelect selectBody = new PlainSelect();
        Table table = updateStmt.getTable();
        final Set<String> ignoredColumns = ignoredTableColumns.get(table.getName().toUpperCase());
        if (ignoredColumns != null) {
            if (ignoredColumns.stream().anyMatch("*"::equals)) {
                OperationResult result = new OperationResult();
                result.setOperation("update");
                result.setTableName(table.getName() + ":*");
                result.setRecordStatus(false);
                return result;
            }
        }
        selectBody.setFromItem(table);
        List<Column> updateColumns = new ArrayList<>();
        for (UpdateSet updateSet : updateStmt.getUpdateSets()) {
            updateColumns.addAll(updateSet.getColumns());
        }
        Columns2SelectItemsResult buildColumns2SelectItems = buildColumns2SelectItems(table.getName(), updateColumns);
        selectBody.setSelectItems(buildColumns2SelectItems.getSelectItems());
        selectBody.setWhere(where);
        selectStmt.setSelectBody(selectBody);

        BoundSql boundSql4Select = new BoundSql(mappedStatement.getConfiguration(), selectStmt.toString(),
                prepareParameterMapping4Select(boundSql.getParameterMappings(), updateStmt),
                boundSql.getParameterObject());
        MetaObject metaObject = SystemMetaObject.forObject(boundSql);
        Map<String, Object> additionalParameters = (Map<String, Object>) metaObject.getValue("additionalParameters");
        if (additionalParameters != null && !additionalParameters.isEmpty()) {
            for (Map.Entry<String, Object> ety : additionalParameters.entrySet()) {
                boundSql4Select.setAdditionalParameter(ety.getKey(), ety.getValue());
            }
        }
        OriginalDataObj originalData = buildOriginalObjectData(selectStmt, buildColumns2SelectItems.getPk(), mappedStatement, boundSql4Select, connection);
        OperationResult result = new OperationResult();
        result.setOperation("update");
        result.setTableName(table.getName());
        result.setRecordStatus(true);
        result.buildDataStr(compareAndGetUpdatedColumnDatas(result.getTableName(), boundSql, updateStmt, originalData));
        return result;
    }

    /**
     * 将update SET部分的jdbc参数去除
     *
     * @param originalMappingList 这里只会包含JdbcParameter参数
     * @param updateStmt
     * @return
     */
    private List<ParameterMapping> prepareParameterMapping4Select(List<ParameterMapping> originalMappingList, Update updateStmt) {
        List<Expression> updateValueExpressions = new ArrayList<>();
        for (UpdateSet updateSet : updateStmt.getUpdateSets()) {
            updateValueExpressions.addAll(updateSet.getExpressions());
        }
        int removeParamCount = 0;
        for (Expression expression : updateValueExpressions) {
            if (expression instanceof JdbcParameter) {
                ++removeParamCount;
            }
        }
        return originalMappingList.subList(removeParamCount, originalMappingList.size());
    }

    /**
     * @param updateSql
     * @param originalDataObj
     * @return
     */
    private List<DataChangedRecord> compareAndGetUpdatedColumnDatas(String tableName, BoundSql updateSql, Statement statement, OriginalDataObj originalDataObj) {
        Map<String, Object> columnNameValMap = new HashMap<>(updateSql.getParameterMappings().size());
        List<Column> selectItemsFromUpdateSql = new ArrayList<>();
        if (statement instanceof Update) {
            Update updateStmt = (Update) statement;
            for (UpdateSet updateSet : updateStmt.getUpdateSets()) {
                selectItemsFromUpdateSql.addAll(updateSet.getColumns());
                final List<Expression> updateList = updateSet.getExpressions();
                for (int i = 0; i < updateList.size(); ++i) {
                    Expression updateExps = updateList.get(i);
                    if (!(updateExps instanceof JdbcParameter)) {
                        columnNameValMap.put(updateSet.getColumns().get(i).getColumnName().toUpperCase(), updateExps.toString());
                    }
                }
            }
        } else if (statement instanceof Insert) {
            selectItemsFromUpdateSql.addAll(((Insert) statement).getColumns());
        }
        Map<String, String> relatedColumnsUpperCaseWithoutUnderline = new HashMap<>(selectItemsFromUpdateSql.size(), 1);
        for (Column item : selectItemsFromUpdateSql) {
            //FIRSTNAME: FIRST_NAME/FIRST-NAME/FIRST$NAME/FIRST.NAME
            relatedColumnsUpperCaseWithoutUnderline.put(item.toString().replaceAll("[._\\-$]", "").toUpperCase(), item.toString().toUpperCase());
        }
        MetaObject metaObject = SystemMetaObject.forObject(updateSql.getParameterObject());

        for (ParameterMapping parameterMapping : updateSql.getParameterMappings()) {
            String propertyName = parameterMapping.getProperty();
            if (propertyName.startsWith("ew.paramNameValuePairs")) {
                continue;
            }
            String[] arr = propertyName.split("\\.");
            String propertyNameTrim = arr[arr.length - 1].replace("_", "").toUpperCase();
            if (relatedColumnsUpperCaseWithoutUnderline.containsKey(propertyNameTrim)) {
                columnNameValMap.put(relatedColumnsUpperCaseWithoutUnderline.get(propertyNameTrim), metaObject.getValue(propertyName));
            }
        }

        final Set<String> ignoredColumns = ignoredTableColumns.get(tableName.toUpperCase());
        if (originalDataObj == null || originalDataObj.isEmpty()) {
            DataChangedRecord oneRecord = new DataChangedRecord();
            List<DataColumnChangeResult> updateColumns = new ArrayList<>(columnNameValMap.size());
            for (Map.Entry<String, Object> ety : columnNameValMap.entrySet()) {
                String columnName = ety.getKey();
                if ((ignoredColumns == null || !ignoredColumns.contains(columnName)) && !ignoreAllColumns.contains(columnName)) {
                    updateColumns.add(DataColumnChangeResult.constrcutByUpdateVal(columnName, ety.getValue()));
                }
            }
            oneRecord.setUpdatedColumns(updateColumns);
            return Collections.singletonList(oneRecord);
        }
        List<DataChangedRecord> originalDataList = originalDataObj.getOriginalDataObj();
        List<DataChangedRecord> updateDataList = new ArrayList<>(originalDataList.size());
        for (DataChangedRecord originalData : originalDataList) {
            if (originalData.hasUpdate(columnNameValMap, ignoredColumns, ignoreAllColumns)) {
                updateDataList.add(originalData);
            }
        }
        return updateDataList;
    }


    private Map<String, Object> buildParameterObjectMap(BoundSql boundSql) {
        MetaObject metaObject = SystemMetaObject.forObject(boundSql.getParameterObject());
        Map<String, Object> propertyValMap = new HashMap<>(boundSql.getParameterMappings().size());
        for (ParameterMapping parameterMapping : boundSql.getParameterMappings()) {
            String propertyName = parameterMapping.getProperty();
            if (propertyName.startsWith("ew.paramNameValuePairs")) {
                continue;
            }
            Object propertyValue = metaObject.getValue(propertyName);
            propertyValMap.put(propertyName, propertyValue);
        }
        return propertyValMap;

    }


    private String buildOriginalData(Select selectStmt, MappedStatement mappedStatement, BoundSql boundSql, Connection connection) {
        try (PreparedStatement statement = connection.prepareStatement(selectStmt.toString())) {
            DefaultParameterHandler parameterHandler = new DefaultParameterHandler(mappedStatement, boundSql.getParameterObject(), boundSql);
            parameterHandler.setParameters(statement);
            ResultSet resultSet = statement.executeQuery();
            final ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();
            StringBuilder sb = new StringBuilder("[");
            while (resultSet.next()) {
                sb.append("{");
                for (int i = 1; i <= columnCount; ++i) {
                    sb.append("\"").append(metaData.getColumnName(i)).append("\":\"");
                    Object res = resultSet.getObject(i);
                    if (res instanceof Clob) {
                        sb.append(DataColumnChangeResult.convertClob((Clob) res));
                    } else {
                        sb.append(res);
                    }
                    sb.append("\",");
                }
                sb.replace(sb.length() - 1, sb.length(), "}");
            }
            sb.append("]");
            resultSet.close();
            return sb.toString();
        } catch (Exception e) {
            Logger.error("[ShanhaiDataAudit]-try to get record tobe deleted for selectStmt={}", selectStmt, e);
            return "failed to get original data";
        }
    }

    private OriginalDataObj buildOriginalObjectData(Select selectStmt, Column pk, MappedStatement mappedStatement, BoundSql boundSql, Connection connection) {
        try (PreparedStatement statement = connection.prepareStatement(selectStmt.toString())) {

            DefaultParameterHandler parameterHandler = new DefaultParameterHandler(mappedStatement, boundSql.getParameterObject(), boundSql);
            parameterHandler.setParameters(statement);
            ResultSet resultSet = statement.executeQuery();
            List<DataChangedRecord> originalObjectDatas = new LinkedList<>();
            while (resultSet.next()) {
                originalObjectDatas.add(prepareOriginalDataObj(resultSet, pk));
            }
            OriginalDataObj result = new OriginalDataObj();
            result.setOriginalDataObj(originalObjectDatas);
            resultSet.close();
            return result;
        } catch (Exception e) {
            Logger.error("[ShanhaiDataAudit]-try to get record tobe updated for selectStmt={}", selectStmt, e);
            return new OriginalDataObj();
        }
    }

    /**
     * get records : include related column with original data in DB
     *
     * @param resultSet
     * @param pk
     * @return
     * @throws SQLException
     */
    private DataChangedRecord prepareOriginalDataObj(ResultSet resultSet, Column pk) throws SQLException {
        final ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();
        List<DataColumnChangeResult> originalColumnDatas = new LinkedList<>();
        DataColumnChangeResult pkval = null;
        for (int i = 1; i <= columnCount; ++i) {
            String columnName = metaData.getColumnName(i).toUpperCase();
            DataColumnChangeResult col = DataColumnChangeResult.constrcutByOriginalVal(columnName, resultSet.getObject(i));
            if (pk != null && columnName.equalsIgnoreCase(pk.getColumnName())) {
                pkval = col;
            } else {
                originalColumnDatas.add(col);
            }
        }
        DataChangedRecord changedRecord = new DataChangedRecord();
        changedRecord.setOriginalColumnDatas(originalColumnDatas);
        if (pkval != null) {
            changedRecord.setPkColumnName(pkval.getColumnName());
            changedRecord.setPkColumnVal(pkval.getOriginalValue());
        }
        return changedRecord;
    }

    private Columns2SelectItemsResult buildColumns2SelectItems(String tableName, List<Column> columns) {
        if (columns == null || columns.isEmpty()) {
            return Columns2SelectItemsResult.build(Collections.singletonList(new AllColumns()), 0);
        }
        List<SelectItem> selectItems = new ArrayList<>(columns.size());
        for (Column column : columns) {
            selectItems.add(new SelectExpressionItem(column));
        }
        for (TableInfo tableInfo : TableInfoHelper.getTableInfos()) {
            if (tableName.equalsIgnoreCase(tableInfo.getTableName())) {
                Column pk = new Column(tableInfo.getKeyColumn());
                selectItems.add(new SelectExpressionItem(pk));
                Columns2SelectItemsResult result = Columns2SelectItemsResult.build(selectItems, 1);
                result.setPk(pk);
                return result;
            }
        }
        return Columns2SelectItemsResult.build(selectItems, 0);
    }

    private String buildParameterObject(BoundSql boundSql) {
        Object paramObj = boundSql.getParameterObject();
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        if (paramObj instanceof Map) {
            Map<String, Object> paramMap = (Map<String, Object>) paramObj;
            int index = 1;
            boolean hasParamIndex = false;
            String key;
            while (paramMap.containsKey((key = "param" + index))) {
                Object paramIndex = paramMap.get(key);
                sb.append("\"").append(key).append("\"").append(":").append("\"").append(paramIndex).append("\"").append(",");
                hasParamIndex = true;
                ++index;
            }
            if (hasParamIndex) {
                sb.delete(sb.length() - 1, sb.length());
                sb.append("}");
                return sb.toString();
            }
            for (Map.Entry<String, Object> ety : paramMap.entrySet()) {
                sb.append("\"").append(ety.getKey()).append("\"").append(":").append("\"").append(ety.getValue()).append("\"").append(",");
            }
            sb.delete(sb.length() - 1, sb.length());
            sb.append("}");
            return sb.toString();
        }
        sb.append("param:").append(paramObj);
        sb.append("}");
        return sb.toString();
    }

    public OperationResult processDelete(Delete deleteStmt, MappedStatement mappedStatement, BoundSql boundSql, Connection connection) {
        Table table = deleteStmt.getTable();
        Expression where = deleteStmt.getWhere();
        Select selectStmt = new Select();
        PlainSelect selectBody = new PlainSelect();
        selectBody.setFromItem(table);
        selectBody.setSelectItems(Collections.singletonList(new AllColumns()));
        selectBody.setWhere(where);
        selectStmt.setSelectBody(selectBody);
        String originalData = buildOriginalData(selectStmt, mappedStatement, boundSql, connection);
        OperationResult result = new OperationResult();
        result.setOperation("delete");
        result.setTableName(table.getName());
        result.setRecordStatus(originalData.startsWith("["));
        result.setChangedData(originalData);
        return result;
    }

    /**
     * ignoredColumns = TABLE_NAME1.COLUMN1,COLUMN2; TABLE2.COLUMN1,COLUMN2; TABLE3.*; *.COLUMN1,COLUMN2
     * 多个表用分号分隔
     * TABLE_NAME1.COLUMN1,COLUMN2 : 表示忽略这个表的这2个字段
     * TABLE3.*: 表示忽略这张表的INSERT/UPDATE，delete暂时还保留
     * *.COLUMN1,COLUMN2:表示所有表的这个2个字段名都忽略
     *
     * @param properties
     */
    @Override
    public void setProperties(Properties properties) {

        String ignoredTableColumns = dataAuditConfig.getIgnoredTableColumns();
        if (ignoredTableColumns == null || ignoredTableColumns.trim().isEmpty()) {
            return;
        }
        String[] array = ignoredTableColumns.split(";");
        for (String table : array) {
            int index = table.indexOf(".");
            if (index == -1) {
                Logger.warn("[ShanhaiDataAudit]-invalid data={} for ignoredColumns, format should be TABLE_NAME1.COLUMN1,COLUMN2; TABLE2.COLUMN1,COLUMN2;", table);
                continue;
            }
            String tableName = table.substring(0, index).trim().toUpperCase();
            String[] columnArray = table.substring(index + 1).split(",");
            Set<String> columnSet = new HashSet<>(columnArray.length);
            for (String column : columnArray) {
                column = column.trim().toUpperCase();
                if (column.isEmpty()) {
                    continue;
                }
                columnSet.add(column);
            }
            if ("*".equals(tableName)) {
                ignoreAllColumns.addAll(columnSet);
            } else {
                this.ignoredTableColumns.put(tableName, columnSet);
            }
        }
    }

}
