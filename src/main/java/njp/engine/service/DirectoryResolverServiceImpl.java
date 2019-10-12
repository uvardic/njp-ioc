package njp.engine.service;

import njp.engine.model.Directory;
import njp.engine.model.DirectoryType;

import java.io.File;

public class DirectoryResolverServiceImpl implements DirectoryResolverService {

    @Override
    public Directory resolveDirectory(Class<?> startupClass) {
        final String directoryPath = getDirectoryPath(startupClass);

        return new Directory(directoryPath, getDirectoryType(directoryPath));
    }

    private String getDirectoryPath(Class<?> startupClass) {
        return startupClass.getProtectionDomain().getCodeSource().getLocation().getFile();
    }

    private DirectoryType getDirectoryType(String directoryPath) {
        if (!new File(directoryPath).isDirectory() && directoryPath.endsWith(".jar"))
            return DirectoryType.JAR;

        return DirectoryType.DIRECTORY;
    }

}
