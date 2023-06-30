package org.example;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;

public class FindDuplicateImages {

    public static void main(String[] args) throws Exception {
        if (args.length < 1) {
            System.err.println("Usage: java -jar copies.jar <directory>");
            System.exit(1);
        }

        int movedFiles = 0;

        String imagesDirPath = args[0];
        File dir = new File(imagesDirPath);

        String duplicatesDirPath = imagesDirPath + File.separator + "Kopier";
        File duplicatesDir = new File(duplicatesDirPath);
        if (!duplicatesDir.exists()) {
            duplicatesDir.mkdirs();
        }

        // Create a custom FileFilter that excludes the Luminar Neo Catalog directory
        IOFileFilter luminarFilter = FileFilterUtils.notFileFilter(new NameFileFilter("Luminar Neo Catalog"));
        IOFileFilter duplicatesFilter = FileFilterUtils.notFileFilter(new NameFileFilter("Kopier"));
        IOFileFilter customDirectoryFilter = FileFilterUtils.and(DirectoryFileFilter.DIRECTORY, luminarFilter, duplicatesFilter);

        Collection<File> files = FileUtils.listFiles(dir, TrueFileFilter.INSTANCE, customDirectoryFilter);

        // Count the total number of files
        int totalFiles = files.size();

        List<File> sortedFiles = new ArrayList<>(files);
        sortedFiles.sort(Comparator.comparingInt(f -> f.getName().length()));

        Map<String, File> imagesMap = new HashMap<>();

        // Counter for the number of processed files
        int processedFiles = 0;

        for (File file : sortedFiles) {
            String fileDigest = DigestUtils.sha256Hex(FileUtils.readFileToByteArray(file));

            String fileName = file.getName().toLowerCase();
            if ((fileName.endsWith(".jpg") || fileName.endsWith(".png") || fileName.endsWith(".jpeg") || fileName.endsWith(".cr2") || fileName.endsWith(".cr3"))
                    && (fileName.matches(".*\\(\\d+\\).*") || fileName.matches(".* - Copy( \\(\\d+\\))?"))) {
                if (imagesMap.containsKey(fileDigest)) {
                    System.out.println("Moving file: " + file.getName());
                    Files.move(file.toPath(), new File(duplicatesDir, file.getName()).toPath(), StandardCopyOption.REPLACE_EXISTING);
                    movedFiles++;
                } else {
                    imagesMap.put(fileDigest, file);
                }
            }

            // Increment the counter and display a message
            processedFiles++;
            System.out.printf("Scanning: %d / %d\n", processedFiles, totalFiles);
        }
        System.out.println("Moved duplicate files: " + movedFiles + " to directory: " + duplicatesDirPath);
    }



}
