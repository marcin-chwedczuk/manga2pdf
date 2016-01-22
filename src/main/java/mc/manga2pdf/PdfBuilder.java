package mc.manga2pdf;

import org.apache.jempbox.xmp.XMPMetadata;
import org.apache.jempbox.xmp.XMPSchemaBasic;
import org.apache.jempbox.xmp.XMPSchemaDublinCore;
import org.apache.jempbox.xmp.XMPSchemaPDF;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDDocumentCatalog;
import org.apache.pdfbox.pdmodel.PDDocumentInformation;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDMetadata;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.edit.PDPageContentStream;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDJpeg;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDPixelMap;
import org.apache.pdfbox.pdmodel.graphics.xobject.PDXObjectImage;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.GregorianCalendar;
import java.util.List;

public class PdfBuilder {
    private Logger logger = LogManager.getLogger(PdfBuilder.class);

    public void createPdfFromImages(final List<String> images, final String title, final String pdfFilename) {
        try {
            PDDocument document = new PDDocument();
            setMetadata(document, title);

            for(String image : images) {
                logger.debug("BUILDING PDF: ADDING IMAGE {}", PathUtils.getFileName(image));
                addPageFromImage(document, image);
            }

            logger.debug("SAVING PDF FILE...");
            document.save(pdfFilename);
            document.close();
        }
        catch (Exception e) {
            throw new RuntimeException("Cannot create pdf document " + pdfFilename, e);
        }
    }

    private void setMetadata(PDDocument document, String title) throws Exception {
        PDDocumentInformation info = document.getDocumentInformation();

        info.setTitle(title);
        info.setKeywords("manga manga2pdf " + title);
        info.setAuthor("manga2pdf via PDFBox");
        info.setCreationDate(new GregorianCalendar());
        info.setModificationDate(new GregorianCalendar());
    }

    private void addPageFromImage(PDDocument document, String imagePath) {
        try {
            PDXObjectImage image = loadImage(document, imagePath);

            PDPage page = new PDPage(new PDRectangle(image.getWidth(), image.getHeight()));
            document.addPage(page);

            try(PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
                contentStream.drawImage(image, 0, 0);
            }
        }
        catch (Exception e) {
            throw new RuntimeException("Cannot add image: " + imagePath, e);
        }
    }

    private PDXObjectImage loadImage(PDDocument document, String imagePath) throws IOException {
        String extension = PathUtils.getExtension(imagePath);

        switch (extension.toLowerCase()) {
            case ".jpeg": case ".jpg":
                return new PDJpeg(document, new FileInputStream(imagePath));

            // gifs are not supported by pdfbox
            // we convert gif -> png then add that png to pdf
            case ".gif":
                imagePath = convertGifToPng(imagePath);
                // fall though

            case ".png":
                return new PDPixelMap(document, ImageIO.read(new File(imagePath)));

            default:
                throw new RuntimeException("Unsupported image format: " + imagePath);
        }
    }

    private String convertGifToPng(String gifImagePath) throws IOException {
        logger.debug("convert GIF -> PNG: {}", gifImagePath);

        String pngImagePath = PathUtils.replaceExtension(gifImagePath, ".png");
        ImageUtils.convertGifToPng(gifImagePath, pngImagePath);

        return pngImagePath;
    }
}
