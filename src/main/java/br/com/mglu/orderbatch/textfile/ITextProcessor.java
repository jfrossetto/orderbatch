package br.com.mglu.orderbatch.textfile;

import com.amazonaws.services.s3.model.S3Object;

import java.util.List;

public interface ITextProcessor {

    List<String> processFile(S3Object s3Object);

}
