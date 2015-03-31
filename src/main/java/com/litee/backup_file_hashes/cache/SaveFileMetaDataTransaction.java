package com.litee.backup_file_hashes.cache;

import com.litee.backup_file_hashes.FileMetaData;
import org.prevayler.Transaction;

import java.util.Date;

/**
 * User: Andrey
 * Date: 2015-02-22
 * Time: 11:58
 */
public class SaveFileMetaDataTransaction implements Transaction<MetadataCache> {
    private final FileId fileId;
    private final FileMetaData fileMetaData;

    public SaveFileMetaDataTransaction(FileId fileId, FileMetaData fileMetaData) {
        this.fileId = fileId;
        this.fileMetaData = fileMetaData;
    }

    @Override
    public void executeOn(MetadataCache prevalentSystem, Date executionTime) {
        prevalentSystem.data.put(fileId, fileMetaData);
    }
}
