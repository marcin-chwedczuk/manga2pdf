package mc.manga2pdf;

public final class StringUtils {
    private StringUtils() { }

    public static boolean isNullOrEmpty(String path) {
        return (path == null) || (path.isEmpty());
    }
}
