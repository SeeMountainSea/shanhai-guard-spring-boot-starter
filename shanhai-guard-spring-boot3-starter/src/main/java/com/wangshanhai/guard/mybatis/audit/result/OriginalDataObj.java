package com.wangshanhai.guard.mybatis.audit.result;

import lombok.Data;

import java.util.List;

/**
 * @author Fly.Sky
 */
@Data
public class OriginalDataObj {
    private List<DataChangedRecord> originalDataObj;

    public boolean isEmpty() {
        return originalDataObj == null || originalDataObj.isEmpty();
    }
}
