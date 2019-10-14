package njp.ioc.engine.service;

import njp.ioc.engine.model.ClassProperties;
import njp.ioc.exception.ClassInitializationException;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class ClassInitializationService {

    private static final int MAX_ITERATIONS = 10000;

    public List<ClassProperties> initializeClasses(LinkedList<ClassProperties> mappedClasses) throws ClassInitializationException {
        final List<ClassProperties> initializedClasses = new ArrayList<>();

        int counter = 0;

        while (!mappedClasses.isEmpty()) {
            if (counter > MAX_ITERATIONS)
                throw new ClassInitializationException("Maximum number of iterations was reached!");

            final ClassProperties enqueuedClass = mappedClasses.removeFirst();

            System.out.println(enqueuedClass);

            if (enqueuedClass.isResolved()) {
                initializeClass(enqueuedClass, mappedClasses);
                initializedClasses.add(enqueuedClass);
            } else {
                mappedClasses.addLast(enqueuedClass);
                counter++;
            }
        }

        return initializedClasses;
    }

    private void initializeClass(ClassProperties enqueuedClass, LinkedList<ClassProperties> mappedClasses) {
        updateDependantClasses(enqueuedClass, mappedClasses);

        try {
            final Object instance = enqueuedClass.getConstructor().newInstance();

            // Trazimo klase koje kao dependency imaju trenutno instanciranu klasu i dodajemo je u
            // niz instanciranih dependency-a
            mappedClasses.stream()
                    .filter(mappedClass -> mappedClass.isDependencyRequired(enqueuedClass.getClassType()))
                    .forEach(mappedClass -> mappedClass.addDependencyInstance(instance));
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
