package work.soho.temporal.db.api.dto;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
public class Row {
    private long timestamp;
    private List<Column> columns = new ArrayList<>();

    @Data
    public static class Column {
        private DataType dataType;
        private boolean boolV;
        private int intV;
        private long longV;
        private float floatV;
        private double doubleV;
        private Binary binaryV;

        public boolean getBoolV() {
            if (dataType == null) {
                throw new RuntimeException("Bool值为空");
            }
            return boolV;
        }

        public Object getObjectValue(DataType dataType) {
            if (this.dataType == null) {
                return null;
            }
            switch (dataType) {
                case DOUBLE:
                    return getDoubleV();
                case FLOAT:
                    return getFloatV();
                case INT64:
                    return getLongV();
                case INT32:
                    return getIntV();
                case BOOLEAN:
                    return getBoolV();
                case TEXT:
                    return getBinaryV();
                default:
                    throw new RuntimeException(dataType.toString());
            }
        }

        public String getStringValue() {
            if (dataType == null) {
                return "null";
            }
            switch (dataType) {
                case BOOLEAN:
                    return String.valueOf(boolV);
                case INT32:
                    return String.valueOf(intV);
                case INT64:
                    return String.valueOf(longV);
                case FLOAT:
                    return String.valueOf(floatV);
                case DOUBLE:
                    return String.valueOf(doubleV);
                case TEXT:
                    return binaryV.toString();
                default:
                    throw new RuntimeException(dataType.toString());
            }
        }

        @Override
        public String toString() {
            return getStringValue();
        }
    }

    @Getter
    @RequiredArgsConstructor
    public enum DataType {
        BOOLEAN((byte) 0),

        /** INT32. */
        INT32((byte) 1),

        /** INT64. */
        INT64((byte) 2),

        /** FLOAT. */
        FLOAT((byte) 3),

        /** DOUBLE. */
        DOUBLE((byte) 4),

        /** TEXT. */
        TEXT((byte) 5),

        /** VECTOR. */
        VECTOR((byte) 6),

        /** UNKNOWN. */
        UNKNOWN((byte) 7);

        private final byte type;
    }
}
