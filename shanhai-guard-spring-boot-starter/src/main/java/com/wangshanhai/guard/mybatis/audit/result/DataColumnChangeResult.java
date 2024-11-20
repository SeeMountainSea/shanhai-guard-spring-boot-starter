package com.wangshanhai.guard.mybatis.audit.result;

import lombok.Data;

import java.io.Reader;
import java.math.BigDecimal;
import java.sql.Clob;
import java.util.Date;
import java.util.Objects;
/**
 * @author Fly.Sky
 */
@Data
public class DataColumnChangeResult {
    private String columnName;
    private Object originalValue;
    private Object updateValue;

    @SuppressWarnings("rawtypes")
    public boolean isDataChanged(Object updateValue) {
        if (!Objects.equals(originalValue, updateValue)) {
            if (updateValue instanceof Number && originalValue instanceof Number) {
                BigDecimal update = new BigDecimal(updateValue.toString());
                BigDecimal original = new BigDecimal(originalValue.toString());
                return update.compareTo(original) != 0;
            }
            if (updateValue instanceof Date && originalValue instanceof Date) {
                Date update = (Date) updateValue;
                Date original = (Date) originalValue;
                return update.compareTo(original) != 0;
            }
            if (originalValue instanceof Clob) {
                String originalStr = convertClob((Clob) originalValue);
                setOriginalValue(originalStr);
                return !originalStr.equals(updateValue);
            }
            return true;
        }
        if (originalValue instanceof Comparable) {
            Comparable original = (Comparable) originalValue;
            Comparable update = (Comparable) updateValue;
            return original.compareTo(update) != 0;
        }
        return false;
    }

    public static String convertClob(Clob clobObj) {
        try {
            return clobObj.getSubString(0, (int) clobObj.length());
        } catch (Exception e) {
            try (Reader is = clobObj.getCharacterStream()) {
                char[] chars = new char[64];
                int readChars;
                StringBuilder sb = new StringBuilder();
                while ((readChars = is.read(chars)) != -1) {
                    sb.append(chars, 0, readChars);
                }
                return sb.toString();
            } catch (Exception e2) {
                //ignored
                return "unknown clobObj";
            }
        }
    }

    public static DataColumnChangeResult constrcutByUpdateVal(String columnName, Object updateValue) {
        DataColumnChangeResult res = new DataColumnChangeResult();
        res.setColumnName(columnName);
        res.setUpdateValue(updateValue);
        return res;
    }

    public static DataColumnChangeResult constrcutByOriginalVal(String columnName, Object originalValue) {
        DataColumnChangeResult res = new DataColumnChangeResult();
        res.setColumnName(columnName);
        res.setOriginalValue(originalValue);
        return res;
    }

    public String generateDataStr() {
        StringBuilder sb = new StringBuilder();
        sb.append("\"").append(columnName).append("\"").append(":").append("\"").append(convertDoubleQuotes(originalValue)).append("->").append(convertDoubleQuotes(updateValue)).append("\"").append(",");
        return sb.toString();
    }

    public String convertDoubleQuotes(Object obj) {
        if (obj == null) {
            return null;
        }
        return obj.toString().replace("\"", "\\\"");
    }
}
