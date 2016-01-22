package mc.manga2pdf;

public class StdoutProgressReporter implements ProgressReporter {
    @Override
    public void reportProgress(String message) {
        System.out.printf("%s\r", message);
    }
}
