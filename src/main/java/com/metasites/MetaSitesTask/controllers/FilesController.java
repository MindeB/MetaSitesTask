package com.metasites.MetaSitesTask.controllers;

import com.metasites.MetaSitesTask.services.FilesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

@RestController
@RequestMapping("/files")
public class FilesController {

   final FilesService filesService;

    @Autowired
    public FilesController(FilesService filesService) {
        this.filesService = filesService;
    }


    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = "application/zip")
    public ResponseEntity<StreamingResponseBody> uploadFiles(@RequestParam("files") List<MultipartFile> files) throws ExecutionException, InterruptedException, IOException {
        List<File> fileList = filesService.consumingFiles(files);
        return ResponseEntity
                .ok()
                .header("Content-Disposition", "attachment; filename=\"files.zip\"")
                .body(out -> {
                    filesService.prepareFilesIntoZip(fileList, out);
                });
    }

}
