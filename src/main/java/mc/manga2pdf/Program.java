package mc.manga2pdf;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.ConsoleAppender;
import org.apache.logging.log4j.core.appender.FileAppender;
import org.apache.logging.log4j.core.config.AppenderRef;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.apache.logging.log4j.core.layout.PatternLayout;

import java.nio.charset.Charset;
import java.util.List;

public class Program {
    private static Logger logger;

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
        configureLog4j(commandLine);

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
        ProgressReporter progressReporter = commandLine.getQuiet() || commandLine.getVerbose()
            ? new QuietProgressReporter()
            : new StdoutProgressReporter();

        String currentWorkingDir = PathUtils.getCurrentWorkingDirectory();
        logger.debug("Current working directory: {}", currentWorkingDir);

        String tempDirectory = PathUtils.createTempDirectory("manga2pdf");
        logger.debug("Temp directory: {}", tempDirectory);

        // extract recursively archive zip/rar supported
        MangaArchiveExtractor extractor = new MangaArchiveExtractor(commandLine.getArchives(), tempDirectory);
        extractor.setProgressReporter(progressReporter);
        extractor.extractRecursively();

        // filter and order files
        List<String> pages = new PageSorter().getPagesInMangaOrder(tempDirectory);
        for(String page : pages) {
            logger.debug("page: {}", PathUtils.getFileName(page));
        }

        // build pdf
        PdfBuilder pdfBuilder = new PdfBuilder();
        pdfBuilder.setProgressReporter(progressReporter);
        pdfBuilder.createPdfFromImages(
                pages,
                PathUtils.replaceExtension(PathUtils.getFileName(commandLine.getOutputPdf()), ""),
                commandLine.getOutputPdf());

        // cleanup
        logger.debug("REMOVING TEMP DIRECTORY...");
        progressReporter.reportProgress("CLEANING TEMP DIRECTORY...");

        PathUtils.removeRecurively(tempDirectory);
        PathUtils.deleteFile(tempDirectory);

        progressReporter.reportProgress("SUCCESS\n");
    }

    private static void configureLog4j(CommandLineOptions commandLine) {

        final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        final Configuration config = ctx.getConfiguration();

        String logfileName = PathUtils.replaceExtension(commandLine.getOutputPdf(), ".log");

        Layout layout = PatternLayout.newBuilder()
                .withPattern("%-5level %d{HH:mm:ss}: %m%n")
                .withCharset(Charset.defaultCharset())
                .build();

        // file logger
        Appender fileAppender = FileAppender.createAppender(
                logfileName, "false", "false", "FILE", "true", "false", "false", "4000", layout, null, "false", null, config);
        fileAppender.start();
        config.addAppender(fileAppender);

        // console logger
        Appender consoleAppender = ConsoleAppender.createAppender(layout, null, null, "CONSOLE", null, null);
        consoleAppender.start();
        config.addAppender(consoleAppender);

        // appender's
        AppenderRef refFile = AppenderRef.createAppenderRef("FILE", null, null);
        AppenderRef refConsole = AppenderRef.createAppenderRef("CONSOLE",null,null);

        // root logger
        AppenderRef[] refs = commandLine.getVerbose()
                ? new AppenderRef[] { refFile, refConsole }
                : new AppenderRef[] { refFile };

        LoggerConfig loggerConfig = LoggerConfig.createLogger("false", Level.DEBUG, LogManager.ROOT_LOGGER_NAME, "true", refs, null, config, null);
        loggerConfig.addAppender(fileAppender, null, null);
        if (commandLine.getVerbose())
            loggerConfig.addAppender(consoleAppender, null, null);

        config.addLogger(LogManager.ROOT_LOGGER_NAME, loggerConfig);
        ctx.updateLoggers();

        logger = LogManager.getLogger(Program.class);
    }

    private static void checkCommandLine(CommandLineOptions commandLine) {
        // archive must exists
        for (String archive : commandLine.getArchives()) {
            if (!PathUtils.fileExists(archive)) {
                System.err.printf("File '%s' doesn't exists.%n", archive);
                System.exit(2);
            }
        }

        // output pdf must not exists
        if (PathUtils.fileExists(commandLine.getOutputPdf())) {
            System.err.printf("File '%s' already exists, remove file or use different name.%n", commandLine.getOutputPdf());
            System.exit(3);
        }
    }
}
