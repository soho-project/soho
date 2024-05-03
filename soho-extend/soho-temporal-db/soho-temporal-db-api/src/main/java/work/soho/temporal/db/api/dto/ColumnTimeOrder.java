package work.soho.temporal.db.api.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ColumnTimeOrder {
    DESC("desc"),
    ASC("asc");
    private final String order;
}
