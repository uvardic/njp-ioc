package njp.ioc.injector.service;

import njp.ioc.annotation.Autowire;
import njp.ioc.exception.ClassInitializationException;
import njp.ioc.injector.model.ClassProperties;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Date;
import java.util.LinkedList;

public class ClassInitializationService {

    private static final int MAX_ITERATIONS = 10000;

    public void initializeClasses(LinkedList<ClassProperties> mappedClasses) throws ClassInitializationException {
        int iterationsCounter = 0;

        while (!mappedClasses.isEmpty()) {
            if (iterationsCounter > MAX_ITERATIONS)
                throw new ClassInitializationException("Maximum number of iterations was reached!");

            final ClassProperties enqueuedClass = mappedClasses.removeFirst();

            if (enqueuedClass.isResolved())
                initializeClass(enqueuedClass, mappedClasses);
            else {
                mappedClasses.addLast(enqueuedClass);
                iterationsCounter++;
            }
        }
    }

    private void initializeClass(ClassProperties enqueuedClass, LinkedList<ClassProperties> mappedClasses) {
        try {
            final Object classInstance = enqueuedClass.getConstructor().newInstance();

            mappedClasses.stream()
                    .filter(mappedClass -> mappedClass.isDependencyRequired(enqueuedClass.getClassType()))
                    .forEach(mappedClass -> mappedClass.addDependencyInstance(classInstance));

            for (int i = 0; i < classInstance.getClass().getDeclaredFields().length; i++) {
                final Field field = classInstance.getClass().getDeclaredFields()[i];

                if (!field.isAnnotationPresent(Autowire.class))
                    continue;

                final Object dependencyInstance = enqueuedClass.getInstantiatedDependencies()[i];

                field.setAccessible(true);
                field.set(classInstance, dependencyInstance);

                if (field.getAnnotation(Autowire.class).verbose())
                    printFieldInfo(classInstance, field);
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new ClassInitializationException(e.getMessage(), e);
        }
    }

    private void printFieldInfo(Object classInstance, Field field) {
        try {
            System.out.println(String.format(
                    "Initialized %s %s in %s on %s with %s",
                    field.getType(),
                    field.getName(),
                    field.getDeclaringClass().getName(),
                    new Date(),
                    field.get(classInstance).hashCode()
            ));
        } catch (IllegalAccessException e) {
            throw new ClassInitializationException(e.getMessage(), e);
        }
    }

}
