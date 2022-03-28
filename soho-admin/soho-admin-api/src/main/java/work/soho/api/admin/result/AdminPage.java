package work.soho.api.admin.result;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
public class AdminPage<E> {
    long total;
    List<E> data;
}
