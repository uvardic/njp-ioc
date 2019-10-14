package njp.engine.model;

import lombok.Data;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.*;

@Data
// TODO ENIGMA
public class ClassProperties {

    private Object instance;

    private final Class<?> classType;

    private final Annotation annotation;

    private final Constructor<?> constructor;

    private final List<Class<?>> dependencies = new ArrayList<>();

    private final Object[] dependencyInstances = new Object[dependencies.size()];

    public ClassProperties(Class<?> classType, Annotation annotation, Constructor<?> constructor) {
        this.classType = classType;
        this.annotation = annotation;
        this.constructor = constructor;
    }

    public List<Class<?>> getDependencies() {
        return Collections.unmodifiableList(dependencies);
    }

    public void addDependency(Class<?> dependency) {
        if (dependency == null)
            throw new NullPointerException("Null can't be a dependency");

        dependencies.add(dependency);
    }

    public void addAllDependencies(Collection<Class<?>> dependencies) {
        if (dependencies == null || dependencies.contains(null))
            throw new NullPointerException("Null can't be a dependency");

        this.dependencies.addAll(dependencies);
    }

    public void addDependencyInstance(Object instance) {
        dependencies.stream()
                .filter(dependency -> dependency.isAssignableFrom(instance.getClass()))
                .forEach(dependency -> dependencyInstances[dependencies.indexOf(dependency)] = instance);
    }

    public boolean isResolved() {
        return Arrays.stream(dependencyInstances).allMatch(Objects::nonNull);
    }

    public boolean isDependencyRequired(Class<?> dependency) {
        return dependencies.stream()
                .anyMatch(d -> d.isAssignableFrom(dependency));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ClassProperties) {
            final ClassProperties other = (ClassProperties) obj;

            return Objects.equals(this.classType, other.classType);
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(this.classType);
    }

}
