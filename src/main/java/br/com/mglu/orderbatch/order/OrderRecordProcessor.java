package br.com.mglu.orderbatch.order;

import br.com.mglu.orderbatch.textfile.ITextProcessor;
import br.com.mglu.orderbatch.textfile.TextRecordBuilder;
import com.amazonaws.services.s3.model.S3Object;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class OrderRecordProcessor implements ITextProcessor {

    private final TextRecordBuilder textProcessor;

    @Override
    public boolean processFile(S3Object s3Object) {
        try {
            List<OrderRecord> orderRecords = buildOrderRecords(s3Object);
            log.info(" orderRecords size {}", orderRecords.size());
            return true;
        } catch (Exception e) {
            log.error(" error processing {} ", s3Object.getKey(), e);
            return false;
        }
    }

    private List<OrderRecord> buildOrderRecords(S3Object s3Object) {
        BufferedReader newBufferedReader = new BufferedReader(new InputStreamReader(s3Object.getObjectContent()));
        return newBufferedReader.lines()
                .map(record -> textProcessor.buildRecord(record, OrderRecord.class))
                .collect(Collectors.toList());
    }
}
