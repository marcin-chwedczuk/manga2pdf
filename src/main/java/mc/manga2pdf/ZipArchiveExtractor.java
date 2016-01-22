package mc.manga2pdf;

import net.lingala.zip4j.core.ZipFile;
import net.lingala.zip4j.exception.ZipException;

public class ZipArchiveExtractor implements ArchiveExtractor {
    @Override
    public void extract(String archivePath, String targetDirectory) {
        try {
            ZipFile zipFile = new ZipFile(archivePath);
            if (zipFile.isEncrypted()) {
                throw new RuntimeException("Archive is encrypted - provide password");
            }
            zipFile.extractAll(targetDirectory);
        }
        catch (ZipException e) {
            throw new RuntimeException("Cannot extract zip archive: " + archivePath, e);
        }
    }
}
