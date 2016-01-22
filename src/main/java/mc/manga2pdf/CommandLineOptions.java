package mc.manga2pdf;

import java.io.PrintStream;

public class CommandLineOptions {
    private String archive;
    private String outputPdf;

    private boolean verbose;

    private CommandLineOptions(String[] args) {
        if (args == null)
            throw new NullPointerException();

        for(String arg : args) {
            if (StringUtils.isNullOrEmpty(arg))
                continue;

            if (arg.startsWith("-")) {
                if ("-v".equals(arg) || "--verbose".equals(arg)) {
                    verbose = true;
                }
                else {
                    throw new RuntimeException("Unrecognized option: '" + arg + "'.");
                }
            }
            else if (StringUtils.isNullOrEmpty(archive)) {
                archive = arg;
            }
            else if (StringUtils.isNullOrEmpty(outputPdf)) {
                outputPdf = arg;
            }
            else {
                throw new CommandLineOptionsException("Too many command line arguments.");
            }
        }

        if (StringUtils.isNullOrEmpty(archive))
            throw new CommandLineOptionsException("Missing archive argument.");

        if (StringUtils.isNullOrEmpty(outputPdf))
            outputPdf = PathUtils.replaceExtension(archive, ".pdf");
    }

    public boolean getVerbose() { return verbose; }
    public String getArchive() { return archive; }
    public String getOutputPdf() { return outputPdf; }

    public static CommandLineOptions fromArgs(String[] args) {
        return new CommandLineOptions(args);
    }

    public static void printUsage(PrintStream stream) {
        stream.printf("manga2pdf archive [output[.pdf]] [-v|--verbose]%n");
        stream.println();
        stream.printf("\tarchive        - path to ZIP or RAR file containing manga/comic book%n");
        stream.printf("\toutput.pdf     - path to PDF file that will be created%n");
        stream.printf("\t-v | --verbose - enable debug logging to console");
        stream.println();
    }
}
