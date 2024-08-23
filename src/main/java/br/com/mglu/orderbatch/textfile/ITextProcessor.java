package br.com.mglu.orderbatch.textfile;

import com.amazonaws.services.s3.model.S3Object;

import java.io.InputStream;
import java.util.List;

public interface ITextProcessor {

    List<String> processFile(InputStream fileStream);

}
