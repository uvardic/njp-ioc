package njp.engine.service;

import njp.engine.model.Directory;
import njp.exception.ClassLocationException;

import java.util.Set;

public interface ClassLocatorService {

    Set<Class<?>> locateClasses(Directory directory) throws ClassLocationException;

}
