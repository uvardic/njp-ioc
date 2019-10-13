package njp.engine.service;

import njp.annotation.Bean;
import njp.annotation.Component;
import njp.annotation.Service;
import njp.engine.model.ClassProperties;
import njp.utils.ClassPropertiesConstructorComparator;
import njp.utils.ConstructorComparator;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.*;
import java.util.stream.Collectors;

public class ClassMapperServiceImpl implements ClassMapperService {

    private static final List<Class<? extends Annotation>> CLASS_ANNOTATIONS = new ArrayList<>();

    static {
        CLASS_ANNOTATIONS.add(Bean.class);
        CLASS_ANNOTATIONS.add(Service.class);
        CLASS_ANNOTATIONS.add(Component.class);
    }

    @Override
    public Set<ClassProperties> mapClasses(Set<Class<?>> locatedClasses) {
        return locatedClasses.stream()
                .filter(locatedClass -> !locatedClass.isInterface())
                .map(
                        locatedCLass -> new ClassProperties(
                                locatedCLass, findAnnotation(locatedCLass), findConstructor(locatedCLass)
                        )
                )
                // todo Comparartor mozda nije potreban
                .sorted(new ClassPropertiesConstructorComparator())
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private Annotation findAnnotation(Class<?> locatedClass) {
        return Arrays.stream(locatedClass.getAnnotations())
                .filter(annotation -> CLASS_ANNOTATIONS.contains(annotation.annotationType()))
                .findFirst()
                // Klasa ne mora da ima anotaciju
                .orElse(null);
//                .orElseThrow();
    }

    private Constructor<?> findConstructor(Class<?> locatedClass) {
        final Constructor<?> constructor = Arrays.stream(locatedClass.getDeclaredConstructors())
                .max(new ConstructorComparator())
                .orElseThrow(() -> new RuntimeException("How is it possible not to have constructor?!"));

        constructor.setAccessible(true);

        return constructor;
    }

}
