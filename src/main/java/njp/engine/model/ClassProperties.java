package njp.engine.model;

import lombok.Data;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Data
public class ClassProperties {

    private Object instance;

    private final Class<?> classType;

    private final Annotation annotation;

    private final Constructor<?> constructor;

    private final List<ClassProperties> dependencies = new ArrayList<>();

    public ClassProperties(Class<?> classType, Annotation annotation, Constructor<?> constructor) {
        this.classType = classType;
        this.annotation = annotation;
        this.constructor = constructor;
    }

    public List<ClassProperties> getDependencies() {
        return Collections.unmodifiableList(dependencies);
    }

    public void addDependency(ClassProperties dependency) {
        if (dependency == null)
            throw new NullPointerException("Null can't be a dependency");

        dependencies.add(dependency);
    }

    public void removeDependency(ClassProperties dependency) {
        dependencies.remove(dependency);
    }

    public void removeAllDependencies() {
        dependencies.clear();
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
