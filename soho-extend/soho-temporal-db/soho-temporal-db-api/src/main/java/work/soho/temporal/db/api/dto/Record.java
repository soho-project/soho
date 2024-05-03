package work.soho.temporal.db.api.dto;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Data
public class Record {
    private String name;
    private DataType type;
    private Object value;


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
