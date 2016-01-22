package mc.manga2pdf;

import com.github.junrar.*;
import com.github.junrar.extract.ExtractArchive;
import org.apache.commons.logging.Log;
import org.apache.logging.log4j.LogManager;

import java.io.File;

public class RarArchiveExtractor implements ArchiveExtractor {
    @Override
    public void extract(String archivePath, String targetDirectory) {
        final File archive = new File(archivePath);
        final File target = new File(targetDirectory);

        ExtractArchive extractArchive = new ExtractArchive();

        // this library doesn't trow any exceptions, instead it uses logger
        // to provide info about errors
        // we intercept logger calls and convert log messages to exceptions
        extractArchive.setLogger(new Log() {
            @Override
            public void debug(Object o) {}

            @Override
            public void debug(Object o, Throwable throwable) { }

            @Override
            public void error(Object o) {
                throw new RuntimeException("cannot extract archive: " + archivePath + ", reason: " + o);
            }

            @Override
            public void error(Object o, Throwable throwable) {
                throw new RuntimeException("cannot extract archive: " + archivePath + ", reason: " + o, throwable);
            }

            @Override
            public void fatal(Object o) {
                throw new RuntimeException("cannot extract archive: " + archivePath + ", reason: " + o);
            }

            @Override
            public void fatal(Object o, Throwable throwable) {
                throw new RuntimeException("cannot extract archive: " + archivePath + ", reason: " + o, throwable);
            }

            @Override
            public void info(Object o) {

            }

            @Override
            public void info(Object o, Throwable throwable) {

            }

            @Override
            public boolean isDebugEnabled() {
                return false;
            }

            @Override
            public boolean isErrorEnabled() {
                return true;
            }

            @Override
            public boolean isFatalEnabled() {
                return true;
            }

            @Override
            public boolean isInfoEnabled() {
                return false;
            }

            @Override
            public boolean isTraceEnabled() {
                return false;
            }

            @Override
            public boolean isWarnEnabled() {
                return false;
            }

            @Override
            public void trace(Object o) {

            }

            @Override
            public void trace(Object o, Throwable throwable) {

            }

            @Override
            public void warn(Object o) {

            }

            @Override
            public void warn(Object o, Throwable throwable) {

            }
        });

        extractArchive.extractArchive(archive, target);
    }
}
