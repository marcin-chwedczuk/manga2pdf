package mc.manga2pdf;

public class ImageUtils {
    public static boolean isImage(String fileName) {
        String extension = PathUtils.getExtension(fileName);

        switch (extension.toLowerCase()) {
            case ".jpg": case ".jpeg":
            case ".gif": case ".png":
                return true;

            default:
                return false;
        }
    }
}
