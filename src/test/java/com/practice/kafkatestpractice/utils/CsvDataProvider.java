package com.practice.kafkatestpractice.utils;


import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class CsvDataProvider {

    public static Iterator<Object[]> getOrderData() throws IOException {
        return readCsv("src/test/resources/OrderData.csv");
    }


    private static Iterator<Object[]> readCsv(String path) throws IOException {
        List<Object[]> data = new ArrayList<>();

        try (Reader in = new FileReader(path)) {
            CSVFormat format = CSVFormat.DEFAULT.builder()
                    .setHeader()
                    .setSkipHeaderRecord(true)
                    .setIgnoreHeaderCase(true)
                    .setTrim(true).get();

            Iterable<CSVRecord> records = format.parse(in);
            for (CSVRecord record : records) {
                data.add(record.toList().toArray());
            }
        }
        return data.iterator();
    }
}
