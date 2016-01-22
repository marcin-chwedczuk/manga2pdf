package mc.manga2pdf;

public class ProgressReportingComponent {
    private ProgressReporter progressReporter;

    public void setProgressReporter(ProgressReporter progressReporter) {
        this.progressReporter = progressReporter;
    }

    protected void reportProgress(String message, Object... args) {
        if (this.progressReporter != null)
            this.progressReporter.reportProgress(String.format(message, args));
    }
}
