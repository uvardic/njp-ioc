package njp.engine.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Directory {

    private final String path;

    private final DirectoryType type;

}
