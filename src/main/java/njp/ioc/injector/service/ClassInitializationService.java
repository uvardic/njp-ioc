package njp.ioc.injector.service;

import njp.ioc.injector.model.ClassProperties;
import njp.ioc.exception.ClassInitializationException;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class ClassInitializationService {

    private static final int MAX_ITERATIONS = 10000;

    public List<ClassProperties> initializeClasses(LinkedList<ClassProperties> mappedClasses) throws ClassInitializationException {
        final List<ClassProperties> initializedClasses = new ArrayList<>();

        int iterationsCounter = 0;

        while (!mappedClasses.isEmpty()) {
            if (iterationsCounter > MAX_ITERATIONS)
                throw new ClassInitializationException("Maximum number of iterations was reached!");

            final ClassProperties enqueuedClass = mappedClasses.removeFirst();

            if (enqueuedClass.isResolved()) {
                final Object instance = initializeClass(enqueuedClass, mappedClasses);

                for (int i = 0; i < instance.getClass().getDeclaredFields().length; i++) {
                    final Field field = instance.getClass().getDeclaredFields()[i];

                    final Object dependencyInstance = enqueuedClass.getInstantiatedDependencies()[i];

                    try {
                        field.setAccessible(true);
                        field.set(instance, dependencyInstance);
                    } catch (IllegalAccessException e) {
                        throw new ClassInitializationException(e.getMessage(), e);
                    }
                }

                initializedClasses.add(enqueuedClass);
            } else {
                mappedClasses.addLast(enqueuedClass);
                iterationsCounter++;
            }
        }

        return initializedClasses;
    }

    private Object initializeClass(ClassProperties enqueuedClass, LinkedList<ClassProperties> mappedClasses) {
        updateDependantClasses(enqueuedClass, mappedClasses);

        try {
            final Object instance = enqueuedClass.getConstructor().newInstance();

            // Trazimo klase koje kao dependency imaju trenutno instanciranu klasu i dodajemo je u
            // niz instanciranih dependency-a
            mappedClasses.stream()
                    .filter(mappedClass -> mappedClass.isDependencyRequired(enqueuedClass.getClassType()))
                    .forEach(mappedClass -> mappedClass.addDependencyInstance(instance));

            return instance;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new ClassInitializationException(e.getMessage(), e);
        }
    }

    private void updateDependantClasses(ClassProperties enqueuedClass, LinkedList<ClassProperties> mappedClasses) {
        enqueuedClass.getDependencies()
                .forEach(dependency -> mappedClasses.stream()
                        .filter(mappedClass -> dependency.isAssignableFrom(mappedClass.getClassType()))
                        .forEach(mappedClass -> mappedClass.addDependantClass(enqueuedClass))
                );
    }

}
