package mc.manga2pdf;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

public class CommandLineOptions {
    private List<String> archives = new ArrayList<>();
    private String outputPdf;

    private boolean verbose;
    private boolean quiet;

    private CommandLineOptions(String[] args) {
        if (args == null)
            throw new NullPointerException();

        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (StringUtils.isNullOrEmpty(arg))
                continue;

            if (arg.startsWith("-")) {
                if ("-v".equals(arg) || "--verbose".equals(arg)) {
                    verbose = true;
                }
                else if ("-q".equals(arg) || "--quiet".equals(arg)) {
                    quiet = true;
                }
                else if ("-o".equals(arg) || "--output".equals(arg)) {
                    if (isLastArgument(args, i))
                        throw new CommandLineOptionsException("Expecting output file name after '" + arg + "' option.");

                    outputPdf = args[++i];
                }
                else {
                    throw new CommandLineOptionsException("Unrecognized option: '" + arg + "'.");
                }
            }
            else {
                archives.add(arg);
            }
        }

        if (archives.isEmpty())
            throw new CommandLineOptionsException("No input files were given on command line.");

        // if no output were provided we get output name from first filename
        if (StringUtils.isNullOrEmpty(outputPdf))
            outputPdf = PathUtils.replaceExtension(archives.get(0), ".pdf");

        if (!outputPdf.endsWith(".pdf")) {
            outputPdf = outputPdf + ".pdf";
        }
    }

    private boolean isLastArgument(String[] args, int index) {
        return index == args.length-1;
    }

    public boolean getVerbose() { return verbose; }
    public boolean getQuiet() { return quiet; }
    public List<String> getArchives() { return archives; }
    public String getOutputPdf() { return outputPdf; }

    public static CommandLineOptions fromArgs(String[] args) {
        return new CommandLineOptions(args);
    }

    public static void printUsage(PrintStream stream) {
        stream.println();
        stream.println("usage:");
        stream.printf("  manga2pdf archives [-o output[.pdf]] [-v|--verbose]%n");
        stream.println();
        stream.printf("  archives           - path to ZIP or RAR file(s) containing manga/comic book(s)%n");
        stream.printf("  -o | --output file - path to PDF file that will be generated%n");
        stream.printf("  -v | --verbose     - enable debug logging to console%n");
        stream.printf("  -q | --quiet       - don't write anything to console%n");
        stream.println();
    }
}
