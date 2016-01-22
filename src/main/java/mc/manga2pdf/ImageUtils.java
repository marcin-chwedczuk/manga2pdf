package mc.manga2pdf;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

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

    public static void convertGifToPng(String gifImagePath, String pngImagePath) throws IOException {
        ImageIO.write(ImageIO.read(new File(gifImagePath)), "png", new File(pngImagePath));
    }
}
