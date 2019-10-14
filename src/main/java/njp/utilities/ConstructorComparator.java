package njp.utilities;

import java.lang.reflect.Constructor;
import java.util.Comparator;

public class ConstructorComparator implements Comparator<Constructor<?>> {

    @Override
    public int compare(Constructor<?> constructor, Constructor<?> other) {
        return Integer.compare(
                constructor.getParameterCount(),
                other.getParameterCount()
        );
    }

}