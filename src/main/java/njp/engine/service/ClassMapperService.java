package njp.engine.service;

import njp.engine.model.ClassProperties;

import java.util.Set;

public interface ClassMapperService {

    Set<ClassProperties> mapClasses(Set<Class<?>> locatedClasses);

}
