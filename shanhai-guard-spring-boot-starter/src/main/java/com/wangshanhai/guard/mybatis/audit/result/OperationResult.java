package com.wangshanhai.guard.mybatis.audit.result;

import lombok.Data;

import java.util.List;
/**
 * @author Fly.Sky
 */
@Data
public class OperationResult {
    private String operation;
    private boolean recordStatus;
    private String tableName;
    private String changedData;
    /**
     * cost for this plugin, ms
     */
    private long cost;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public boolean isRecordStatus() {
        return recordStatus;
    }

    public void setRecordStatus(boolean recordStatus) {
        this.recordStatus = recordStatus;
    }

    public void buildDataStr(List<DataChangedRecord> records) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (DataChangedRecord r : records) {
            sb.append(r.generateUpdatedDataStr()).append(",");
        }
        if (sb.length() == 1) {
            sb.append("]");
            changedData = sb.toString();
            return;
        }
        sb.replace(sb.length() - 1, sb.length(), "]");
        changedData = sb.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"").append("tableName").append("\"").append(":").append("\"").append(tableName).append("\"").append(",");
        sb.append("\"").append("operation").append("\"").append(":").append("\"").append(operation).append("\"").append(",");
        sb.append("\"").append("recordStatus").append("\"").append(":").append("\"").append(recordStatus).append("\"").append(",");
        sb.append("\"").append("changedData").append("\"").append(":").append(changedData).append(",");
        sb.append("\"").append("cost(ms)").append("\"").append(":").append(cost);
        sb.append("}");
        return sb.toString();
    }
}
