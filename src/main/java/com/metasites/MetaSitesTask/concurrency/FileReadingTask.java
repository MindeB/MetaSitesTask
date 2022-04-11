package com.metasites.MetaSitesTask.concurrency;

import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.Callable;

public class FileReadingTask implements Callable<Boolean> {

    MultipartFile file;
    Map<String, Integer> map;

    public FileReadingTask(MultipartFile file, Map<String, Integer> map) {
        this.file = file;
        this.map = map;
    }

    @Override
    public Boolean call() throws Exception {

        InputStream inputStream = file.getInputStream();
        new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
                .lines()
                .forEach(this::handleLine);
        return true;
    }

    private void handleLine(String s) {
        s = replaceAllSpecialSymbolsAndNumbersInString(s);
        String[] wordsInLine = splitLineIntoWordsByWhiteSpace(s);
        for(String word : wordsInLine) {
            word = word.toLowerCase();
            if(word.length() > 2) {
                if(map.putIfAbsent(word, 1) != null) {
                    map.compute(
                            word,
                            (key, value) -> value == null ? 1 : value + 1
                    );
                }
            }
        }
    }

    private String[] splitLineIntoWordsByWhiteSpace(String s) {
        return s.trim().split("\\s+");
    }

    private String replaceAllSpecialSymbolsAndNumbersInString(String s) {
        return s.replaceAll("\\P{L}", " ");
    }
}
