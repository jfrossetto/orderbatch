package br.com.mglu.orderbatch.aws;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class AwsS3Client {

    private static final String PATH_SEPARATOR = "/";
    private final AmazonS3 amazonS3Client;

    public S3Object getS3Object(String bucketName, String fileName) throws IOException {
        return amazonS3Client.getObject(bucketName, fileName);
    }

    public S3Object getS3Object(S3ObjectSummary summary) throws IOException {
        return getS3Object(summary.getBucketName(), summary.getKey());
    }

    public void writeFileToS3(String bucketName, String fileName, String fileContent) throws IOException {
        amazonS3Client.putObject(bucketName, fileName, fileContent);
    }

    public ListObjectsV2Result getListObjects(String bucketName, String keyPrefix) {
        final ListObjectsV2Request request = new ListObjectsV2Request()
                .withBucketName(bucketName)
                .withPrefix(keyPrefix);
        return amazonS3Client.listObjectsV2(request);
    }

    public S3Object moveS3Object(S3Object sourceS3, String destinationFolder) throws IOException {
        String destinationKey = destinationFolder
                .concat(PATH_SEPARATOR)
                .concat(getKeyWithoutFolder(sourceS3.getKey()));
        CopyObjectRequest copyRequest = new CopyObjectRequest()
                .withSourceBucketName(sourceS3.getBucketName())
                .withSourceKey(sourceS3.getKey())
                .withDestinationBucketName(sourceS3.getBucketName())
                .withDestinationKey(destinationKey);
        CopyObjectResult result = amazonS3Client.copyObject(copyRequest);
        DeleteObjectRequest deleteRequest = new DeleteObjectRequest(sourceS3.getBucketName(),
                                                                    sourceS3.getKey());
        amazonS3Client.deleteObject(deleteRequest);
        return getS3Object(sourceS3.getBucketName(), destinationKey);
    }

    private String getKeyWithoutFolder(String key) {
        String[] keyParts = key.split(PATH_SEPARATOR);
        return keyParts[keyParts.length-1];
    }

}
