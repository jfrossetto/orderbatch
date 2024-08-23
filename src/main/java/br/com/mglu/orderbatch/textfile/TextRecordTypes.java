package br.com.mglu.orderbatch.textfile;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@RequiredArgsConstructor
@Getter
public enum TextRecordTypes {

    ORDER_RECORD("orderRecord", "orderRecordProcessor");

    private final String recordType;
    private final String service;

    public static TextRecordTypes getRecordType(String value) {
        return Arrays.asList(TextRecordTypes.values()).stream()
                .filter(recordType -> recordType.getRecordType().equals(value))
                .findFirst()
                .orElseThrow(() -> new RuntimeException(String.format("Record Processor Not Found for [%s] record type", value)));
    }

}
