package njp.ioc.engine.model;

import lombok.Data;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.*;

@Data
public class ClassProperties {

    private final Class<?> classType;

    private final Annotation annotation;

    private final Constructor<?> constructor;

    private final List<Class<?>> dependencies;

    private final Object[] instantiatedDependencies;

    private final List<ClassProperties> dependantClasses = new ArrayList<>();

    public ClassProperties(
            Class<?> classType, Annotation annotation, Constructor<?> constructor, List<Class<?>> dependencies
    ) {
        this.classType = classType;
        this.annotation = annotation;
        this.constructor = constructor;
        this.dependencies = dependencies;
        this.instantiatedDependencies = new Object[dependencies.size()];
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
                .forEach(dependency -> instantiatedDependencies[dependencies.indexOf(dependency)] = instance);
    }

    public boolean isResolved() {
        return Arrays.stream(instantiatedDependencies).allMatch(Objects::nonNull);
    }

    public boolean isDependencyRequired(Class<?> dependency) {
        return dependencies.stream()
                .anyMatch(d -> d.isAssignableFrom(dependency));
    }

    public List<ClassProperties> getDependantClasses() {
        return Collections.unmodifiableList(dependantClasses);
    }

    public void addDependantClass(ClassProperties dependantClass) {
        if (dependantClass == null)
            throw new NullPointerException("Null can't be a dependency");

        dependantClasses.add(dependantClass);
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
