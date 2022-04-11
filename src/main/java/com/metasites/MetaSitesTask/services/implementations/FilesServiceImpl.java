package com.metasites.MetaSitesTask.services.implementations;

import com.metasites.MetaSitesTask.concurrency.FileCreatingTask;
import com.metasites.MetaSitesTask.concurrency.FileReadingTask;
import com.metasites.MetaSitesTask.services.FilesService;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.*;
import java.util.concurrent.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class FilesServiceImpl implements FilesService {

    public static final int ThreadPoolSize = 10;
    final List<String> charactersRangeList = List.of("A-G", "H-N", "O-U", "V-Z");


    @Override
    public List<File> consumingFiles(MultipartFile[] files) throws ExecutionException, InterruptedException, IOException {

        Map<String, Integer> frequencyOfWordsMap = new ConcurrentSkipListMap<>();

        ExecutorService executorService = Executors.newFixedThreadPool(ThreadPoolSize);

        executeFileReadingTasks(files, frequencyOfWordsMap, executorService);

        Set<Future<File>> fileCreatingTaskFutures = executeFileCreatingTasks(frequencyOfWordsMap, executorService);
        executorService.shutdown();

        List<File> preparedFiles = new ArrayList<>();
        for(Future<File> future: fileCreatingTaskFutures) {
            preparedFiles.add(future.get());
        }

        return preparedFiles;
    }

    @Override
    public void prepareFilesIntoZip(List<File> fileList, OutputStream out) throws IOException {
        var zipOutputStream = new ZipOutputStream(out);
        for (File file : fileList) {
            zipOutputStream.putNextEntry(new ZipEntry(file.getName()));
            FileInputStream fileInputStream = new FileInputStream(file);

            IOUtils.copy(fileInputStream, zipOutputStream);

            fileInputStream.close();
            zipOutputStream.closeEntry();
        }

        zipOutputStream.close();
    }

    private Set<Future<File>> executeFileCreatingTasks(Map<String, Integer> frequencyOfWordsMap, ExecutorService executorService) {
        Set<Future<File>> fileCreatingTaskFutures = new HashSet<>();
        for(String charactersRange : charactersRangeList) {
            FileCreatingTask fileCreatingTask = new FileCreatingTask(frequencyOfWordsMap, charactersRange.charAt(0), charactersRange.charAt(2));
            fileCreatingTaskFutures.add(executorService.submit(fileCreatingTask));
        }
        return fileCreatingTaskFutures;
    }

    private void executeFileReadingTasks(MultipartFile[] files, Map<String, Integer> frequencyOfWordsMap, ExecutorService executorService) throws InterruptedException, ExecutionException {
        Set<Future<Boolean>> fileReadingTaskFutures = new HashSet<>();
        for (MultipartFile file : files) {
            FileReadingTask fileReadingTask = new FileReadingTask(file, frequencyOfWordsMap);
            fileReadingTaskFutures.add(executorService.submit(fileReadingTask));
        }

        for(Future<Boolean> future: fileReadingTaskFutures) {
            future.get();
        }
    }

}
