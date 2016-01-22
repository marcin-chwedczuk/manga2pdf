package mc.manga2pdf;

import java.util.Arrays;

public class StdoutProgressReporter implements ProgressReporter {
    private int _longestLine = 0;
    private String _emptyLine = "";

    private String _spinner = "|/-\\";
    private int _spinnerCounter = 0;

    @Override
    public void reportProgress(String message) {
        if (message.length() > _longestLine) {
            _longestLine = message.length();

            char[] emptyLineChars = new char[_longestLine];
            Arrays.fill(emptyLineChars, ' ');

            _emptyLine = new String(emptyLineChars);
        }

        // Clear line before printing
        System.out.printf("  %s\r", _emptyLine);
        System.out.printf("%c %s\r", _spinner.charAt(_spinnerCounter), message);

        _spinnerCounter++;
        if (_spinnerCounter >= _spinner.length())
            _spinnerCounter = 0;
    }
}
