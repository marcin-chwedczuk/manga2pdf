package mc.manga2pdf;

import com.github.junrar.*;
import com.github.junrar.extract.ExtractArchive;

import java.io.File;

public class RarArchiveExtractor implements ArchiveExtractor {
    @Override
    public void extract(String archivePath, String targetDirectory) {
        final File archive = new File(archivePath);
        final File target = new File(targetDirectory);

        ExtractArchive extractArchive = new ExtractArchive();
        extractArchive.extractArchive(archive, target);
    }
}
