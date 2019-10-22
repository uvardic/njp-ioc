package njp.ioc.injector;

import njp.ioc.injector.model.ClassProperties;
import njp.ioc.injector.service.ClassInitializationService;
import njp.ioc.injector.service.ClassLocatorService;
import njp.ioc.injector.service.ClassMapperService;

import java.util.LinkedList;
import java.util.Set;

public class Injector {

    private static final ClassLocatorService classLocatorService = new ClassLocatorService();

    private static final ClassMapperService classMapperService = new ClassMapperService();

    private static final ClassInitializationService classInitializationService = new ClassInitializationService();

    public static void run(Class<?> startupClass) {
        final Set<Class<?>> locatedClasses = classLocatorService.locateClasses(startupClass);

        final LinkedList<ClassProperties> mappedClasses = classMapperService.mapClasses(locatedClasses);

        classInitializationService.initializeClasses(mappedClasses);
    }

}
