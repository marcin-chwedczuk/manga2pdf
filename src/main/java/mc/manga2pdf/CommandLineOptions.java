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
                    throw new CommandLineOptionsException("Unrecognized option: '" + arg + "'.");
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

        if (!outputPdf.endsWith(".pdf")) {
            outputPdf = outputPdf + ".pdf";
        }
    }

    public boolean getVerbose() { return verbose; }
    public String getArchive() { return archive; }
    public String getOutputPdf() { return outputPdf; }

    public static CommandLineOptions fromArgs(String[] args) {
        return new CommandLineOptions(args);
    }

    public static void printUsage(PrintStream stream) {
        stream.println();
        stream.println("usage:");
        stream.printf("  manga2pdf archive [output[.pdf]] [-v|--verbose]%n");
        stream.println();
        stream.printf("  archive        - path to ZIP or RAR file containing manga/comic book%n");
        stream.printf("  output.pdf     - path to PDF file that will be generated%n");
        stream.printf("  -v | --verbose - enable debug logging to console%n");
        stream.println();
    }
}
