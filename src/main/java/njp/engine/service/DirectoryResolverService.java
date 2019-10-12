package njp.engine.service;

import njp.engine.model.Directory;

public interface DirectoryResolverService {

    Directory resolveDirectory(Class<?> startupClass);

}
