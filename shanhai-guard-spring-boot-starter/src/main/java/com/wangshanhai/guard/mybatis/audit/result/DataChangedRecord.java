package com.wangshanhai.guard.mybatis.audit.result;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
/**
 * @author Fly.Sky
 */
@Data
public class DataChangedRecord {
    private String pkColumnName;
    private Object pkColumnVal;
    private List<DataColumnChangeResult> originalColumnDatas;
    private List<DataColumnChangeResult> updatedColumns;

    public boolean hasUpdate(Map<String, Object> columnNameValMap, Set<String> ignoredColumns, Set<String> ignoreAllColumns) {
        if (originalColumnDatas == null) {
            return true;
        }
        boolean hasUpdate = false;
        updatedColumns = new ArrayList<>(originalColumnDatas.size());
        for (DataColumnChangeResult originalColumn : originalColumnDatas) {
            final String columnName = originalColumn.getColumnName().toUpperCase();
            if (ignoredColumns != null && ignoredColumns.contains(columnName) || ignoreAllColumns.contains(columnName)) {
                continue;
            }
            Object updatedValue = columnNameValMap.get(columnName);
            if (originalColumn.isDataChanged(updatedValue)) {
                hasUpdate = true;
                originalColumn.setUpdateValue(updatedValue);
                updatedColumns.add(originalColumn);
            }
        }
        return hasUpdate;
    }

    public String generateUpdatedDataStr() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        if (pkColumnName != null) {
            sb.append("\"").append(pkColumnName).append("\"").append(":").append("\"").append(convertDoubleQuotes(pkColumnVal)).append("\"").append(",");
        }
        for (DataColumnChangeResult update : updatedColumns) {
            sb.append(update.generateDataStr());
        }
        sb.replace(sb.length() - 1, sb.length(), "}");
        return sb.toString();
    }

    public String convertDoubleQuotes(Object obj) {
        if (obj == null) {
            return null;
        }
        return obj.toString().replace("\"", "\\\"");
    }
}
