package njp.engine.service;

import njp.engine.model.ClassProperties;
import njp.exception.ClassInitializationException;

public interface ClassInitializationService {

    void initializeClass(
            ClassProperties classProperties, Object... constructorParams
    ) throws ClassInitializationException;

}
