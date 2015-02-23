package com.litee.hash_based_recovery;

import com.litee.hash_based_recovery.hashing.Hashes;
import com.litee.hash_based_recovery.hashing.StreamHashCalculator;
import com.litee.hash_based_recovery.hashing.StreamHashCalculatorImpl;
import jonelo.sugar.util.Base32;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class FileMetadataCalculatorImpl implements FileMetadataCalculator {
    public static final StreamHashCalculator STREAM_HASH_CALCULATOR = new StreamHashCalculatorImpl();

    @Override
    public FileMetaData processFile(File file) throws IOException {
        System.out.println("INFO: Processing file: " + file.getAbsolutePath());
        Hashes hashes = STREAM_HASH_CALCULATOR.calculateHashes(new FileInputStream(file));
        return new FileMetaData(file.getName(), file.length(), file.lastModified(), Base32.encode(hashes.getTTH()));
    }
}
