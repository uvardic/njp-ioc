package njp.ioc.injector.service;

import njp.ioc.exception.ClassLocationException;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.jar.JarFile;
import java.util.stream.Collectors;

public class ClassLocatorService {

    public Set<Class<?>> locateClasses(Class<?> rootClass) throws ClassLocationException {
        final String directoryPath = rootClass.getProtectionDomain().getCodeSource().getLocation().getFile();

        return isJar(directoryPath) ? locateJarFileClasses(directoryPath) : locateDirectoryClasses(directoryPath);
    }

    private boolean isJar(String directoryPath) {
        return !new File(directoryPath).isDirectory() && directoryPath.endsWith(".jar");
    }

    private Set<Class<?>> locateJarFileClasses(String jarFilePath) {
        try {
            return new JarFile(new File(jarFilePath))
                    .stream()
                    .filter(jarEntry -> jarEntry.getName().endsWith(".class"))
                    .map(jarEntry -> getClass(jarEntry.getName()))
                    .collect(Collectors.toSet());
        } catch (IOException e) {
            throw new ClassLocationException(e.getMessage(), e);
        }
    }

    private final Set<Class<?>> locatedDirectoryClasses = new HashSet<>();

    private Set<Class<?>> locateDirectoryClasses(String directoryPath) {
        locatedDirectoryClasses.clear();

        final File rootDirectory = new File(directoryPath);

        if (!rootDirectory.isDirectory())
            throw new ClassLocationException(String.format("Invalid directory: \'%s\'", directoryPath));

        // Krecemo od prvog unutrasnjeg fajla kako be se otarasili 'classes' folder
        Arrays.stream(Objects.requireNonNull(rootDirectory.listFiles()))
                .forEach(innerDirectory -> scanDir(innerDirectory, ""));

        return locatedDirectoryClasses;
    }

    private void scanDir(File file, String packageName) {
        if (file.isDirectory()) {
            packageName += file.getName() + ".";

            for (File innerFile : Objects.requireNonNull(file.listFiles()))
                scanDir(innerFile, packageName);
        } else {
            if (!file.getName().endsWith(".class"))
                return;

            locatedDirectoryClasses.add(getClass(packageName + file.getName()));
        }
    }

    private Class<?> getClass(String className) {
        try {
            final String formattedClassName = className
                    .replace(".class", "")
                    .replaceAll("\\\\", ".")
                    .replaceAll("/", ".");

            return Class.forName(formattedClassName);
        } catch (ClassNotFoundException e) {
            throw new ClassLocationException(e.getMessage(), e);
        }
    }

}
