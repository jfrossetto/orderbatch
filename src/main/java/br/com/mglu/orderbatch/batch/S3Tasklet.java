package br.com.mglu.orderbatch.batch;

import br.com.mglu.orderbatch.aws.AwsS3Client;
import br.com.mglu.orderbatch.textfile.ITextProcessor;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.CollectionUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Component
@RequiredArgsConstructor
public class S3Tasklet implements Tasklet {

    private static final int DELAY = 5000;
    private static final String BUCKET_NAME = "mglu-orders";
    private static final String SERVICE_SUFFIX = "Processor";
    private static final String ERROR_FOLDER = "error";
    private static final String PROCESSING_FOLDER = "processing";
    private static final String PROCESSED_FOLDER = "processed";

    private final AwsS3Client s3Client;
    private final Map<String, ITextProcessor> textProcessors;

    @Override
    public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
        log.info(">>> orderTasklet");
        ListObjectsV2Result s3Objects = s3Client.getListObjects(BUCKET_NAME, "data");
        if (Objects.isNull(s3Objects) || CollectionUtils.isNullOrEmpty(s3Objects.getObjectSummaries())) {
            log.warn("without files to process");
            return RepeatStatus.FINISHED;
        }
        S3Object s3Object = s3Client.getS3Object(s3Objects.getObjectSummaries().getFirst());
        processFile(s3Object);
        return RepeatStatus.CONTINUABLE;
    }

    private void processFile(S3Object s3Object) throws IOException {
        String recordType = s3Object.getObjectMetadata().getUserMetaDataOf("record-type");
        if (ObjectUtils.isEmpty(recordType)) {
            log.warn("{} file without metadata record-type - moved to error", s3Object.getKey());
            s3Client.moveS3Object(s3Object, ERROR_FOLDER);
            return;
        }
        ITextProcessor textProcessor = getTextProcessor(recordType);
        if (Objects.isNull(textProcessor)) {
            log.warn(" Processor for {} not implemented! - moved to error", recordType);
            s3Client.moveS3Object(s3Object, ERROR_FOLDER);
            return;
        }
        S3Object inProcess = s3Client.moveS3Object(s3Object, PROCESSING_FOLDER);
        boolean processed = textProcessor.processFile(s3Object);
        if (!processed) {
            log.warn("{} file not processed - moved to error", inProcess.getKey());
            s3Client.moveS3Object(inProcess, ERROR_FOLDER);
            return;
        }
        s3Client.moveS3Object(inProcess, PROCESSED_FOLDER);
    }

    private ITextProcessor getTextProcessor(String recordType) {
        String serviceName = recordType.concat(SERVICE_SUFFIX);
        return textProcessors.get(serviceName);
    }

}
