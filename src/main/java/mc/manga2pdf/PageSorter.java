package mc.manga2pdf;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class PageSorter {
    private static Logger logger = LogManager.getLogger(PageSorter.class);

    private final List<String> pages;

    public PageSorter() {
        pages = new ArrayList<>();
    }

    public List<String> getPagesInMangaOrder(String directory) {
        if (StringUtils.isNullOrEmpty(directory))
            throw new IllegalArgumentException("Directory path cannot be empty");

        discoverPages(directory);
        logger.debug("summary: discovered {} pages", pages.size());

        return pages;
    }

    private void discoverPages(String directory) {
        logger.debug("discovering pages in {} directory", directory);

        // get files in directory, sort them and add to pages
        List<String> images = PathUtils.getFilesInDirectory(directory).stream()
                .filter(f -> ImageUtils.isImage(f))
                .collect(Collectors.toList());

        logger.debug(" -> discovered {} images", images.size());

        images = mangaSort(images);
        pages.addAll(images);

        // sort list of subdirectories and discoverPages recursively
        List<String> subdirectories = PathUtils.getDirectoriesInDirectory(directory);
        logger.debug(" -> discovered {} subdirectories", subdirectories.size());

        subdirectories = mangaSort(subdirectories);
        subdirectories.forEach(this::discoverPages);
    }

    private List<String> mangaSort(List<String> images) {
        // use default string sort
        Collections.sort(images);
        return images;
    }
}
