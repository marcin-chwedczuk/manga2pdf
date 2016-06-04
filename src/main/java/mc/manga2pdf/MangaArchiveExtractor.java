package mc.manga2pdf;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class MangaArchiveExtractor extends ProgressReportingComponent {
    private static Logger logger = LogManager.getLogger(MangaArchiveExtractor.class);

    private final List<String> archiveFilenames;
    private final String outputDirectory;

    public MangaArchiveExtractor(List<String> archiveFilenames, String outputDirectory) {
        if (archiveFilenames == null)
            throw new NullPointerException("archiveFilenames");

        if (archiveFilenames.isEmpty())
            throw new IllegalArgumentException("archiveFileNames must contain at least one filename.");

        if (StringUtils.isNullOrEmpty(outputDirectory))
            throw new IllegalArgumentException("outputDirectory cannot be empty");

        this.archiveFilenames = new ArrayList<>(archiveFilenames);
        this.outputDirectory = outputDirectory;
    }

    public void extractRecursively() {
        // process files in manga order - so arg_x subdirectories are in sync
        // with order of chapters
        Collections.sort(archiveFilenames);

        int index = 0;
        for (String archiveFilename : archiveFilenames) {
            logger.debug("-- PROCESSING FILE: {} --", archiveFilename);

            String normalizedArchiveFilename = PathUtils.normalizePath(archiveFilename);

            Path outputPath = Paths.get(outputDirectory, "arg_"+index);
            String normalizedOutputPath = PathUtils.normalizePath(outputPath.toString());

            extractRecursively(normalizedArchiveFilename, normalizedOutputPath, false);
            index++;
        }
    }

    private void extractRecursively(
            String archiveFilename,
            String outputDirectory,
            boolean deleteArchiveAfterExtraction)
    {
        // extract archive to output directory
        logger.debug("extracting archive: {} to directory {}", archiveFilename, outputDirectory);
        reportProgress("EXTRACTING ARCHIVE: %s", PathUtils.getFileName(archiveFilename));

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
