package com.wangshanhai.guard.mybatis.audit.result;

import lombok.Data;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.SelectItem;

import java.util.List;

/**
 * @author Fly.Sky
 */
@Data
public class Columns2SelectItemsResult {
    private Column pk;
    /**
     * all column with additional columns: ID, etc.
     */
    private List<SelectItem> selectItems;
    /**
     * newly added column count from meta data.
     */
    private int additionalItemCount;

    public static Columns2SelectItemsResult build(List<SelectItem> selectItems, int additionalItemCount) {
        Columns2SelectItemsResult result = new Columns2SelectItemsResult();
        result.setSelectItems(selectItems);
        result.setAdditionalItemCount(additionalItemCount);
        return result;
    }
}
