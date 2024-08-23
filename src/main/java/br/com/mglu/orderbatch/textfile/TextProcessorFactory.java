package br.com.mglu.orderbatch.textfile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class TextProcessorFactory {

    private final Map<String, ITextProcessor> textProcessors;

    public ITextProcessor getTextProcessor(String recordTypeValue) {
        TextRecordTypes recordType = TextRecordTypes.getRecordType(recordTypeValue);
        if (!textProcessors.containsKey(recordType.getService())) {
            throw new RuntimeException(String.format("Record Processor Not Found [%s]", recordType.getService()));
        }
        return textProcessors.get(recordType.getService());
    }
}
