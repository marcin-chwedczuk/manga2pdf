package mc.manga2pdf;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Program {
    private static Logger logger = LogManager.getLogger(Program.class);

    public static void main(String[] args) {
        String currentWorkingDir = PathUtils.getCurrentWorkingDirectory();
        logger.debug("Current working directory: {}", currentWorkingDir);

        String archiveFilename = "/home/mc/Torrents/Trap Days/[Hachimitsu Scans] Trap Days ch. 1-6 (end).rar";
        String pdfFilename = "/home/mc/m2pdf/"; // default .zip -> .pdf

        // extract recursively archive zip/rar supported
        new MangaArchiveExtractor(archiveFilename, "/home/mc/m2pdf/").extractRecursively();

        // filter and order files
        // build pdf
        // cleanup

        // handle arguments from command line

        logger.debug("END");
    }
}
