package com.metasites.MetaSitesTask.concurrency;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileWriter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.metasites.MetaSitesTask.utils.FileCreationUtils.createTempFile;

public class FileCreatingTask implements Callable<File> {

    Map<String, Integer> frequencyOfWordsMap;
    char startCharacter;
    char endCharacter;

    public  FileCreatingTask(Map<String, Integer> frequencyOfWordsMap, char startCharacter, char endCharacter) {

        this.frequencyOfWordsMap = frequencyOfWordsMap;
        this.startCharacter = startCharacter;
        this.endCharacter = endCharacter;
    }

    @Override
    public File call() throws Exception {

        File file = createTempFile(startCharacter + "-" + endCharacter, ".txt");
        FileWriter fileWriter = new FileWriter(file);

        List<String> charList = getCharListByGivenRange(startCharacter, endCharacter);

        Map<String, Integer> sortedMap = getFilteredMapByGivenCharactersRange(charList);
        fileWriter.write(convertMapToJsonString(sortedMap));
        fileWriter.close();
        return file;
    }

    private Map<String, Integer> getFilteredMapByGivenCharactersRange(List<String> charList) {
        return frequencyOfWordsMap.entrySet().stream()
                .filter(entry -> charList.contains(String.valueOf(entry.getKey().charAt(0)).toUpperCase()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (x, y) -> y, LinkedHashMap::new));
    }

    private List<String> getCharListByGivenRange(char startCharacter, char endCharacter) {
        return IntStream.rangeClosed(startCharacter, endCharacter)
                .mapToObj(c -> "" + (char) c).collect(Collectors.toList());
    }

    private String convertMapToJsonString(Map<?, ?> map) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(map);
    }

}
