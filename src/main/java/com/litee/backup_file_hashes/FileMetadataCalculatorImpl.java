package com.litee.backup_file_hashes;

import com.litee.backup_file_hashes.hashing.Hashes;
import com.litee.backup_file_hashes.hashing.StreamHashCalculator;
import com.litee.backup_file_hashes.hashing.StreamHashCalculatorImpl;
import jonelo.sugar.util.Base32;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;

public class FileMetadataCalculatorImpl implements FileMetadataCalculator {
    public static final StreamHashCalculator STREAM_HASH_CALCULATOR = new StreamHashCalculatorImpl();

    @Override
    public FileMetaData processFile(File file) throws IOException {
        System.out.println("INFO: Processing file: " + file.getAbsolutePath());
        try (FileInputStream inputStream = new FileInputStream(file)) {
            Hashes hashes = STREAM_HASH_CALCULATOR.calculateHashes(inputStream);
            BasicFileAttributes fileAttributes = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
            return new FileMetaData(file.getName(), file.length(), fileAttributes.creationTime().toMillis(), fileAttributes.lastModifiedTime().toMillis(), Base32.encode(hashes.getTTH()));
        }
    }
}
