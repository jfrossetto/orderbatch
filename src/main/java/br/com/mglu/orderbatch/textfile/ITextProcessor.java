package br.com.mglu.orderbatch.textfile;

import com.amazonaws.services.s3.model.S3Object;

public interface ITextProcessor {

    boolean processFile(S3Object s3Object);

}
