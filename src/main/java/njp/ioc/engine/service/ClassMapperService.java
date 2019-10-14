package njp.ioc.engine.service;

import njp.ioc.annotation.Autowire;
import njp.ioc.annotation.Bean;
import njp.ioc.annotation.Component;
import njp.ioc.annotation.Service;
import njp.ioc.engine.model.ClassProperties;
import njp.ioc.exception.ClassMappingException;
import njp.ioc.utilities.ClassPropertiesComparator;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

public class ClassMapperService {

    private static final List<Class<? extends Annotation>> CLASS_ANNOTATIONS = new ArrayList<>();

    static {
        CLASS_ANNOTATIONS.add(Bean.class);
        CLASS_ANNOTATIONS.add(Service.class);
        CLASS_ANNOTATIONS.add(Component.class);
    }

    public LinkedList<ClassProperties> mapClasses(Set<Class<?>> locatedClasses) throws ClassMappingException {
        return locatedClasses.stream()
                .filter(locatedClass -> !locatedClass.isInterface())
                .map(locatedClass -> new ClassProperties(
                        locatedClass, findAnnotation(locatedClass), findConstructor(locatedClass),
                        findDependencies(locatedClass)
                ))
                // Sortiramo klase po broju parametara u konstruktoru.
                // Zelimo prvo da instanciramo klase bez parametara kako bi postigli bolje performanse.
                .sorted(new ClassPropertiesComparator())
                .collect(Collectors.toCollection(LinkedList::new));
    }

    private Annotation findAnnotation(Class<?> locatedClass) {
        return Arrays.stream(locatedClass.getAnnotations())
                .filter(annotation -> CLASS_ANNOTATIONS.contains(annotation.annotationType()))
                .findFirst()
                .orElse(null);
//                .orElseThrow();
    }

    private Constructor<?> findConstructor(Class<?> locatedClass) {
        return Arrays.stream(locatedClass.getDeclaredConstructors())
                .filter(constructor -> constructor.getParameterCount() == 0)
                .peek(constructor -> constructor.setAccessible(true))
                .findFirst()
                .orElseThrow(() -> new ClassMappingException(
                        String.format("%s failed to provide a default constructor!", locatedClass)
                ));
    }

    private List<Class<?>> findDependencies(Class<?> locatedClass) {
        return Arrays.stream(locatedClass.getDeclaredFields())
                .filter(field -> field.isAnnotationPresent(Autowire.class))
                .peek(field -> field.setAccessible(true))
                .map(Field::getType)
                .collect(Collectors.toList());
    }

}
