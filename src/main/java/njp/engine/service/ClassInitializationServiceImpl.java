package njp.engine.service;

import njp.engine.model.ClassProperties;
import njp.exception.ClassInitializationException;

import java.lang.reflect.InvocationTargetException;

public class ClassInitializationServiceImpl implements ClassInitializationService {

    @Override
    public void initializeClass(
            ClassProperties classProperties, Object... constructorParams
    ) throws ClassInitializationException {
        if (classProperties.getConstructor().getParameterCount() != constructorParams.length)
            throw new ClassInitializationException(
                    String.format("Invalid parameter count for constructor: %s", classProperties.getConstructor())
            );

        try {
            Object instance = classProperties.getConstructor().newInstance(constructorParams);
            classProperties.setInstance(instance);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new ClassInitializationException(e.getMessage(), e);
        }
    }
}
