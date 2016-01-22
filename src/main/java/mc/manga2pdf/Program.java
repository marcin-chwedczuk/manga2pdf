package mc.manga2pdf;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Files;
import java.util.List;

public class Program {
    private static Logger logger = LogManager.getLogger(Program.class);

    public static void main(String[] args) {
        CommandLineOptions commandLine = null;

        try {
            commandLine = CommandLineOptions.fromArgs(args);
        }
        catch(CommandLineOptionsException invalidArgs) {
            System.err.println(invalidArgs.getMessage());
            CommandLineOptions.printUsage(System.err);

            System.exit(1);
        }

        checkCommandLine(commandLine);

        // configure log4j dynamically

        try {
            manga2pdf(commandLine);
            logger.debug("SUCCESS");
        }
        catch (Exception e) {
            logger.error(e);
            e.printStackTrace();
            System.exit(100);
        }
    }

    private static void manga2pdf(CommandLineOptions commandLine) {
        String currentWorkingDir = PathUtils.getCurrentWorkingDirectory();
        logger.debug("Current working directory: {}", currentWorkingDir);

        String tempDirectory = PathUtils.createTempDirectory("manga2pdf");
        logger.debug("Temp directory: {}", tempDirectory);

        // extract recursively archive zip/rar supported
        new MangaArchiveExtractor(commandLine.getArchive(), tempDirectory).extractRecursively();

        // filter and order files
        List<String> pages = new PageSorter().getPagesInMangaOrder(tempDirectory);
        for(String page : pages) {
            logger.debug("page: {}", PathUtils.getFileName(page));
        }

        // build pdf
        new PdfBuilder().createPdfFromImages(
                pages,
                PathUtils.replaceExtension(PathUtils.getFileName(commandLine.getArchive()), ""),
                commandLine.getOutputPdf());

        // cleanup
        logger.debug("REMOVING TEMP DIRECTORY...");
        PathUtils.removeRecurively(tempDirectory);
        PathUtils.deleteFile(tempDirectory);
    }

    private static void checkCommandLine(CommandLineOptions commandLine) {
        // archive must exists
        if (!PathUtils.fileExists(commandLine.getArchive())) {
            System.err.printf("File '%s' doesn't exists.%n", commandLine.getArchive());
            System.exit(2);
        }

        // output pdf must not exists
        if (PathUtils.fileExists(commandLine.getOutputPdf())) {
            System.err.printf("File '%s' already exists, remove file or use different name.%n", commandLine.getOutputPdf());
            System.exit(3);
        }
    }
}
