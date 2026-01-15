package work.soho.open.biz.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HourCountDTO {
    private String hour;
    private Long count;
}