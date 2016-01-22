package mc.manga2pdf;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.*;
import java.util.List;
import java.util.stream.Collectors;

public class MangaArchiveExtractor {
    private static Logger logger = LogManager.getLogger(MangaArchiveExtractor.class);

    private final String archiveFilename;
    private final String outputDirectory;

    public MangaArchiveExtractor(String archiveFilename, String outputDirectory) {
        if (StringUtils.isNullOrEmpty(archiveFilename))
            throw new IllegalArgumentException("archiveFilename cannot be empty.");

        if (StringUtils.isNullOrEmpty(outputDirectory))
            throw new IllegalArgumentException("outputDirectory cannot be empty");

        this.archiveFilename = archiveFilename;
        this.outputDirectory = outputDirectory;
    }

    public void extractRecursively() {
        String normalizedArchiveFilename = PathUtils.normalizePath(archiveFilename);
        String normalizedOutputDirectory = PathUtils.normalizePath(outputDirectory);

        extractRecursively(normalizedArchiveFilename, normalizedOutputDirectory, false);
    }

    private void extractRecursively(
            String archiveFilename,
            String outputDirectory,
            boolean deleteArchiveAfterExtraction)
    {
        // extract archive to output directory
        logger.debug("extracting archive: {} to directory {}", archiveFilename, outputDirectory);
        ArchiveExtractor extractor =
                ArchiveExtractorFactory.createExtractor(PathUtils.getExtension(archiveFilename));
        extractor.extract(archiveFilename, outputDirectory);

        // scan output directory and find nested archives
        List<String> nestedArchives = findNestedArchives(outputDirectory);

        // unpack nested archives
        for(String archive : nestedArchives) {
            String outputSubdirectory = PathUtils.replaceExtension(
                    PathUtils.getRelativePath(archive, outputDirectory), "");

            String archiveOutputDirectory =
                    Paths.get(outputDirectory, outputSubdirectory).toString();

            PathUtils.makeDirectory(archiveOutputDirectory);
            extractRecursively(archive, archiveOutputDirectory, true);
        }

        // remove original archive if needed
        if (deleteArchiveAfterExtraction) {
            logger.debug("removing archive file {}", archiveFilename);
            PathUtils.deleteFile(archiveFilename);
        }
    }

    private List<String> findNestedArchives(String outputDirectory) {
        List<String> files = PathUtils.getNestedFiles(outputDirectory);

        List<String> archives = files.stream()
                .filter(file -> ArchiveExtractorFactory.isKnownArchiveFormat(PathUtils.getExtension(file)))
                .collect(Collectors.toList());

        for(String archive : archives) {
            logger.debug("found nested archive {}", archive);
        }

        return archives;
    }
}
