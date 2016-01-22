package mc.manga2pdf;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

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

    public static List<String> getFilesInDirectory(String directory) {
        try {
            List<String> files = new ArrayList<String>();

            File[] entries = new File(directory).listFiles();
            for (File entry : entries) {
                if (entry.isFile()) {
                    files.add(entry.getCanonicalPath());
                }
            }

            return files;
        }
        catch (Exception e) {
            throw new RuntimeException("Cannot get list of files in directory: " + directory, e);
        }
    }

    public static List<String> getDirectoriesInDirectory(String directory) {
        try {
            List<String> directories = new ArrayList<String>();

            File[] entries = new File(directory).listFiles();
            for (File entry : entries) {
                if (entry.isDirectory()) {
                    directories.add(entry.getCanonicalPath());
                }
            }

            return directories;
        }
        catch (Exception e) {
            throw new RuntimeException("Cannot get list of subdirectories in directory: " + directory, e);
        }
    }

    public static void removeRecurively(String directory) {
        try {
            Files.walkFileTree(Paths.get(directory), new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs)
                        throws IOException
                {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException
                {
                    // try to delete the file anyway, even if its attributes
                    // could not be read, since delete-only access is
                    // theoretically possible
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException
                {
                    if (exc == null)
                    {
                        Files.delete(dir);
                        return FileVisitResult.CONTINUE;
                    }
                    else
                    {
                        // directory iteration failed; propagate exception
                        throw exc;
                    }
                }
            });
        }
        catch (Exception e) {
            throw new RuntimeException("Cannot recursively remove directory: " + directory, e);
        }
    }

    public static boolean fileExists(String path) {
        try {
            File f = new File(path);
            return f.exists() && f.isFile();
        }
        catch (Exception e) {
            throw new RuntimeException("Checking if file exists failed", e);
        }
    }

    public static String createTempDirectory(String prefix) {
        try {
            String systemTempDirectory = System.getProperty("java.io.tmpdir");
            String tempDirectoryName = prefix + UUID.randomUUID().toString();
            File tempDirectory = new File(systemTempDirectory, tempDirectoryName);

            tempDirectory.mkdirs();
            return tempDirectory.getCanonicalPath();
        }
        catch (Exception e) {
            throw new RuntimeException("Creating temp directory failed", e);
        }
    }
}
