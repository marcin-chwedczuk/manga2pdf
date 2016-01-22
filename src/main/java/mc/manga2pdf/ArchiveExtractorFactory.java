package mc.manga2pdf;

public final class ArchiveExtractorFactory {
    private ArchiveExtractorFactory() { }

    public static boolean isKnownArchiveFormat(String archiveExtension) {
        String ext = archiveExtension.toLowerCase();
        return ".zip".equals(ext) || ".rar".equals(ext);
    }

    public static ArchiveExtractor createExtractor(String archiveExtension) {
        switch (archiveExtension.toLowerCase()) {
            case ".zip":
                return new ZipArchiveExtractor();

            case ".rar":
                return new RarArchiveExtractor();

            default:
                throw new IllegalArgumentException("Unsupported archive format: " + archiveExtension);
        }
    }
}
