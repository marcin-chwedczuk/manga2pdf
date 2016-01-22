package mc.manga2pdf;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

public final class PathUtils {
    private PathUtils() { }

    public static String getCurrentWorkingDirectory() {
        return System.getProperty("user.dir");
    }

    public static String normalizePath(String path) {
        File pathFile = new File(path);

        try {
            return pathFile.getCanonicalPath();
        }
        catch (Exception ex) {
            throw new RuntimeException("Cannot resolve path: " + path, ex);
        }
    }

    public static String getExtension(String path) {
        if (path == null)
            throw new NullPointerException("path cannot be null");

        int dotIndex = path.lastIndexOf('.');
        if (dotIndex == (-1))
            return "";

        return path.substring(dotIndex);
    }

    public static List<String> getNestedFiles(String directory) {
        List<String> files = new ArrayList<>();

        Path directoryPath = FileSystems.getDefault().getPath(directory);
        try {
            Files.walkFileTree(directoryPath, new FileVisitor<Path>() {
                @Override
                public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    files.add(file.toAbsolutePath().toString());
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    return FileVisitResult.CONTINUE;
                }
            });
        }
        catch (Exception ex) {
            throw new RuntimeException("Cannot get list of files contained in directory: " + directory, ex);
        }

        return files;
    }

    public static String replaceExtension(String archive, String newExtension) {
        int oldExtensionLength = getExtension(archive).length();

        return archive.substring(0, archive.length() - oldExtensionLength) + newExtension;
    }

    public static void deleteFile(String archiveFilename) {
        try {
            new File(archiveFilename).delete();
        }
        catch (Exception ex) {
            throw new RuntimeException("Cannot remove file: " + archiveFilename, ex);
        }
    }

    public static void makeDirectory(String directory) {
        try {
            new File(directory).mkdirs();
        }
        catch (Exception ex) {
            throw new RuntimeException("Cannot create directory: " + directory);
        }
    }

    public static String getFileName(String archive) {
        return Paths.get(archive).getFileName().toString();
    }

    public static String getRelativePath(String file, String directory) {
        Path pathAbsolute = Paths.get(file);
        Path pathBase = Paths.get(directory);
        Path pathRelative = pathBase.relativize(pathAbsolute);
        return pathRelative.toString();
    }
}
