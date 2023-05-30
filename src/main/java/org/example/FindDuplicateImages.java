package org.example;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.TrueFileFilter;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.*;

public class FindDuplicateImages {

    public static void main(String[] args) throws Exception {
        // Directory containing the images
        String imagesDirPath = "C:\\Users\\steff\\OneDrive\\Billeder";
        File dir = new File(imagesDirPath);

        // Directory where duplicates will be moved
        String duplicatesDirPath = "C:\\Users\\steff\\OneDrive\\Skrivebord\\Kopier";
        File duplicatesDir = new File(duplicatesDirPath);
        if (!duplicatesDir.exists()) {
            duplicatesDir.mkdirs();
        }

        // List all files in the directory (this does not include subdirectories)
        Collection<File> files = FileUtils.listFiles(dir, TrueFileFilter.INSTANCE, TrueFileFilter.INSTANCE);

        // Sort files by name length (shortest first)
        List<File> sortedFiles = new ArrayList<>(files);
        sortedFiles.sort(Comparator.comparingInt(f -> f.getName().length()));

        Map<String, File> imagesMap = new HashMap<>();

        // Loop through all the files
        for (File file : sortedFiles) {
            // Calculate the SHA-256 hash for each file
            String fileDigest = DigestUtils.sha256Hex(FileUtils.readFileToByteArray(file));

            String fileName = file.getName().toLowerCase();
            System.out.println(fileDigest);
            if ((fileName.endsWith(".jpg") || fileName.endsWith(".png") || fileName.endsWith(".jpeg") || fileName.endsWith(".cr2"))) {
                if (imagesMap.containsKey(fileDigest)) {
                    // It's a duplicate
                    System.out.println("Moving file: " + file.getName());
                    Files.move(file.toPath(), new File(duplicatesDir, file.getName()).toPath(), StandardCopyOption.REPLACE_EXISTING);
                } else {
                    // Not a duplicate, so we add it to the map
                    imagesMap.put(fileDigest, file);
                }
            }
        }
    }

}
