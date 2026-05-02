package org.elcata98.translator;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.elcata98.translator.beans.ItemBean;
import org.elcata98.translator.beans.StatsBean;
import org.elcata98.translator.beans.StatsBeanWrapper;


import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class TranslateProcessor {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public static void main(String[] args) throws IOException {
        new TranslateProcessor().processTranslation();
//        new TranslateProcessor().prepareForTranslation();
    }

    private void processTranslation() throws IOException {

        File inputFile = new File("renderer/public/data/en/items.ndjson");
        File translationFile = new File("renderer/public/data/items_translated.txt");
        File outputFile = new File("renderer/public/data/jp/items.ndjson");
        outputFile.delete();

        try (InputStream inputStream = new FileInputStream(inputFile); OutputStream outputStream = new FileOutputStream(outputFile); InputStream inputStreamTranslation = new FileInputStream(translationFile)) {
            BufferedReader translationsBufferedReader = new BufferedReader(new InputStreamReader(inputStreamTranslation));
            String[] translations = translationsBufferedReader.readLine().split("、");

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
            int lineNumber = 0;
            int startIndex;
            int endIndex;
            String line = bufferedReader.readLine();
            while (line != null) {
                startIndex = line.indexOf("\"name\":\"") + 8;
                endIndex = line.indexOf("\",\"refName");
                line = line.substring(0, startIndex) + translations[lineNumber] + line.substring(endIndex) + "\n";
                bufferedWriter.write(line);
                ++lineNumber;
                line = bufferedReader.readLine();
            }

            bufferedReader.close();
            bufferedWriter.flush();
            bufferedWriter.close();
        }

        inputFile = new File("renderer/public/data/en/stats.ndjson");
        translationFile = new File("renderer/public/data/stats_translated.txt");
        outputFile = new File("renderer/public/data/jp/stats.ndjson");
        outputFile.delete();

        try (InputStream inputStream = new FileInputStream(inputFile); OutputStream outputStream = new FileOutputStream(outputFile); InputStream inputStreamTranslation = new FileInputStream(translationFile)) {
            BufferedReader translationsBufferedReader = new BufferedReader(new InputStreamReader(inputStreamTranslation));
            String[] translations = translationsBufferedReader.readLine().split("、");

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream));
            int itemNumber = 0;
            int startIndex;
            int endIndex;
            String line = bufferedReader.readLine();
            while (line != null) {
                startIndex = line.indexOf("[{\"string\":\"") + 13;
                endIndex = line.indexOf("}],\"trade");
                String toUpdate = line.substring(startIndex, endIndex);
                if (toUpdate.contains("},{\"string")) {
                    // we have multiple matchers
                    // let's assume only 2
                    String updated;
                    if (toUpdate.split("string").length > 3) {
                        System.out.println(" more than 2 matchers! " + toUpdate.split("string").length);
                    } else {
//                        need to check if value is present or only the string
                        int tempIndex;
                        if (toUpdate.contains("\"value")) {
                            tempIndex = toUpdate.indexOf(",\"value\":");
                            int tempIndex2 = toUpdate.indexOf("},{\"string\":\"");
                            if (tempIndex > tempIndex2) {
//                                flip
                                updated = translations[itemNumber++] + toUpdate.substring(tempIndex2, tempIndex2 + 13) + translations[itemNumber++] + toUpdate.substring(tempIndex);
                            } else {
                                updated = translations[itemNumber++] + toUpdate.substring(tempIndex, tempIndex2) + translations[itemNumber++] + toUpdate.substring(tempIndex2);
                            }
                        } else {
                            tempIndex = toUpdate.indexOf("},{\"string\":\"");
                            int tempIndex2;
                            if (toUpdate.contains("\",\"")) {
                                tempIndex2 = toUpdate.indexOf("\",\"");
                            } else {
                                tempIndex2 = toUpdate.indexOf("},{\"string\":\"");
                            }
                            updated = translations[itemNumber++] + toUpdate.substring(tempIndex, tempIndex + 13) + translations[itemNumber++] + toUpdate.substring(tempIndex2);
                        }
                        line = line.substring(0, startIndex) + updated + line.substring(endIndex) + "\n";
                    }
                } else {
                    //we have only 1 matcher
                    line = line.substring(0, startIndex) + translations[itemNumber] + line.substring(endIndex) + "\n";
                    ++itemNumber;
                }

                bufferedWriter.write(line);
                line = bufferedReader.readLine();
            }

            bufferedReader.close();
            bufferedWriter.flush();
            bufferedWriter.close();
        }
    }

    private void prepareForTranslation() throws IOException {
        /*
            - read file
            - process line by line
            - save as csv (txt)
         */

        File inputFile = new File("renderer/public/data/en/items.ndjson");
        File outputFile = new File("renderer/public/data/items_for_translate.txt");
        outputFile.delete();

        try (InputStream inputStream = new FileInputStream(inputFile); OutputStream outputStream = new FileOutputStream(outputFile)) {
            List<ItemBean> items = new ArrayList<>();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = bufferedReader.readLine();
            while (line != null) {
                items.add(OBJECT_MAPPER.readValue(line, ItemBean.class));
                line = bufferedReader.readLine();
            }
            String itemsText = items.stream().map(ItemBean::getName).collect(Collectors.joining(","));
            outputStream.write(itemsText.getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
            bufferedReader.close();
        }

        inputFile = new File("renderer/public/data/en/stats.ndjson");
        outputFile = new File("renderer/public/data/stats_for_translate.txt");
        outputFile.delete();

        try (InputStream inputStream = new FileInputStream(inputFile); OutputStream outputStream = new FileOutputStream(outputFile)) {
            List<StatsBean> items = new ArrayList<>();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = bufferedReader.readLine();
            while (line != null) {
                StatsBean statsBean = OBJECT_MAPPER.readValue(line, StatsBean.class);
                if (statsBean.getRef() != null) {
                    items.add(statsBean);
                } else {
                    StatsBeanWrapper statsBeanWrapper = OBJECT_MAPPER.readValue(line, StatsBeanWrapper.class);
                    items.addAll(Arrays.stream(statsBeanWrapper.getStats()).toList());
                }
                line = bufferedReader.readLine();
            }
            String itemsText = items.stream().flatMap(StatsBean::getMatchersStream).map(StatsBean.Matcher::getString).map(m -> m.replaceAll("\n", ".")).collect(Collectors.joining(","));
            outputStream.write(itemsText.getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
            bufferedReader.close();
        }
    }
}
