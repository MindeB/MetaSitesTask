package com.metasites.MetaSitesTask.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.nio.file.Files;

public class FileCreationUtils {

    private static Logger logger = LogManager.getLogger(FileCreationUtils.class);


    public static File createTempFile(String prefix, String suffix) throws Exception {
        String tempDir = System.getProperty("java.io.tmpdir");
        String fileName = (prefix != null ? prefix : "") + (suffix != null ? suffix : "");
        File tempFile = new File(tempDir, fileName);
        try {
            Files.deleteIfExists(tempFile.toPath());
        } catch (Exception e) {
            FileCreationUtils.logger.error("The file was not deleted because it was not found", e);
        }
        try {
            tempFile.createNewFile();
        } catch (Exception e) {
            FileCreationUtils.logger.error("The temp file was not created: ", e);
            throw new Exception(e);
        }
        return tempFile;
    }

}
