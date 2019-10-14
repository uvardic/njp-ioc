package njp.ioc.utilities;

import njp.ioc.engine.model.ClassProperties;

import java.util.Comparator;

public class ClassPropertiesComparator implements Comparator<ClassProperties> {

    @Override
    public int compare(ClassProperties classProperties, ClassProperties other) {
        return Integer.compare(
                classProperties.getDependencies().size(),
                other.getDependencies().size()
        );
    }
}
