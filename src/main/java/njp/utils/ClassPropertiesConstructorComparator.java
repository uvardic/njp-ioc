package njp.utils;

import njp.engine.model.ClassProperties;

import java.util.Comparator;

public class ClassPropertiesConstructorComparator implements Comparator<ClassProperties> {

    @Override
    public int compare(ClassProperties classProperties, ClassProperties other) {
        return Integer.compare(
                classProperties.getConstructor().getParameterCount(),
                other.getConstructor().getParameterCount()
        );
    }

}
