package mc.manga2pdf;

import java.util.Arrays;

public final class ArchiveExtractorFactory {
    private ArchiveExtractorFactory() { }

    public static boolean isKnownArchiveFormat(String archiveExtension) {
        String ext = archiveExtension.toLowerCase();
        return Arrays.asList(".zip", ".cbz", ".rar").contains(ext);
    }

    public static ArchiveExtractor createExtractor(String archiveExtension) {
        switch (archiveExtension.toLowerCase()) {
            case ".zip":
            case ".cbz":
                return new ZipArchiveExtractor();

            case ".rar":
                return new RarArchiveExtractor();

            default:
                throw new IllegalArgumentException("Unsupported archive format: " + archiveExtension);
        }
    }
}
