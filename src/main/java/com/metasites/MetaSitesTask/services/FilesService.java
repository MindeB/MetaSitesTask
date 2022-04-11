package com.metasites.MetaSitesTask.services;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.concurrent.ExecutionException;

public interface FilesService {

    List<File> consumingFiles(List<MultipartFile> files) throws ExecutionException, InterruptedException, IOException;

    void prepareFilesIntoZip(List<File> fileList, OutputStream out) throws IOException;
}
