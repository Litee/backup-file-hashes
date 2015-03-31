package com.litee.backup_file_hashes.cache;

import com.litee.backup_file_hashes.FileMetaData;
import com.litee.backup_file_hashes.FileMetadataCalculator;
import com.litee.backup_file_hashes.FileMetadataCalculatorImpl;
import org.prevayler.Prevayler;
import org.prevayler.PrevaylerFactory;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;

/**
 * User: Andrey
 * Date: 2015-02-20
 * Time: 00:50
 */
public class FileMetadataCalculatorWithCacheImpl implements FileMetadataCalculator {
    private final Prevayler<MetadataCache> prevayler;
    private final long bytesToProcessBeforeFlush;
    private long bytesProcessedSinceLastFlushToDisk = 0;

    public FileMetadataCalculatorWithCacheImpl(String cacheDir) throws Exception {
        this(cacheDir, 10L * 1024 * 1024 * 1024);
    }

    public FileMetadataCalculatorWithCacheImpl(String cacheDir, long bytesToProcessBeforeFlush) throws Exception {
        this.bytesToProcessBeforeFlush = bytesToProcessBeforeFlush;
        File file = new File(cacheDir);
        if (!file.exists()) {
            file.mkdirs();
        }
        System.out.println("INFO: Loading cache...");
        prevayler = PrevaylerFactory.createCheckpointPrevayler(new MetadataCache(), cacheDir);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                System.out.println("INFO: Saving cache...");
                prevayler.takeSnapshot();
                prevayler.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }));
    }

    @Override
    public FileMetaData processFile(File file) throws Exception {
        final FileId fileId = new FileId(file.getAbsolutePath());
        FileMetaData cachedFileMetaData = prevayler.execute(new FindFileMetaDataQuery(fileId));
        if (cachedFileMetaData != null) {
            BasicFileAttributes fileAttributes = Files.readAttributes(file.toPath(), BasicFileAttributes.class);
            if (cachedFileMetaData.getFileLength() == file.length() && cachedFileMetaData.getLastModified() == fileAttributes.lastModifiedTime().toMillis()) {
                System.out.println("INFO: File found in cache: " + fileId.path);
                return cachedFileMetaData;
            }
        }
        FileMetaData newFileMetaData = new FileMetadataCalculatorImpl().processFile(file);
        prevayler.execute(new SaveFileMetaDataTransaction(fileId, newFileMetaData));
        bytesProcessedSinceLastFlushToDisk += file.length();
        if (bytesProcessedSinceLastFlushToDisk > bytesToProcessBeforeFlush)
        {
            System.out.println("INFO: Flushing snapshot to the disk...");
            prevayler.takeSnapshot();
            bytesProcessedSinceLastFlushToDisk = 0;
        }
        return newFileMetaData;
    }
}

