package work.soho.temporal.db.biz.dto;

import lombok.Data;
import org.apache.iotdb.tsfile.file.metadata.enums.TSDataType;

@Data
public class Record {
    private String name;
    private TSDataType type;
    private Object value;
}
