package br.com.mglu.orderbatch.batch;

import br.com.mglu.orderbatch.aws.AwsS3Client;
import br.com.mglu.orderbatch.order.OrderRecordProcessor;
import br.com.mglu.orderbatch.textfile.TextProcessorFactory;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.repeat.RepeatStatus;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class S3TaskletTest {

    private static final String BUCKET_NAME = "mglu-orders";
    private static final String NAME_PREFIX = "data_";
    private static final String PROCESSING_FOLDER = "processing";
    private static final String PROCESSED_FOLDER = "processed";
    private static final String NOT_INCLUDED_FOLDER = "notinclued";

    private AwsS3Client s3Client;
    private OrderRecordProcessor orderRecordProcessor;
    private TextProcessorFactory textProcessorFactory;

    private S3Tasklet s3Tasklet;

    @BeforeEach
    void setup() {
        s3Client = mock(AwsS3Client.class);
        orderRecordProcessor = mock(OrderRecordProcessor.class);
        textProcessorFactory = new TextProcessorFactory(Map.of("orderRecordProcessor", orderRecordProcessor));
        s3Tasklet = new S3Tasklet(s3Client, textProcessorFactory);
    }

    @Test
    void givenFilesInS3_whenCallExecute_shouldProcessAndContinue() throws Exception {
        ListObjectsV2Result listObjects = new ListObjectsV2Result();
        listObjects.getObjectSummaries().add(new S3ObjectSummary());
        when(s3Client.getListObjects(BUCKET_NAME, NAME_PREFIX)).thenReturn(listObjects);
        S3Object s3Object = new S3Object();
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setUserMetadata(Map.of("record-type", "orderRecord"));
        s3Object.setObjectMetadata(objectMetadata);
        when(s3Client.getS3Object(any(S3ObjectSummary.class))).thenReturn(s3Object);
        when(orderRecordProcessor.processFile(any())).thenReturn(List.of("Line 1"));
        RepeatStatus result = s3Tasklet.execute(null, null);
        assertEquals(RepeatStatus.CONTINUABLE, result );
        verify(orderRecordProcessor).processFile(any());
        verify(s3Client).moveS3Object(any(), eq(PROCESSING_FOLDER));
        verify(s3Client).moveS3Object(any(), eq(PROCESSED_FOLDER));
        verify(s3Client).writeFileToS3(any(), eq(NOT_INCLUDED_FOLDER), any(), any());
    }

    @Test
    void givenNoFilesInS3_whenCallExecute_shouldFinish() throws Exception {
        RepeatStatus result = s3Tasklet.execute(null, null);
        assertEquals(RepeatStatus.FINISHED, result );
    }

}
