package com.litee.backup_file_hashes.cache;

import com.litee.backup_file_hashes.FileMetaData;
import org.prevayler.Query;

import java.util.Date;

/**
 * User: Andrey
 * Date: 2015-02-22
 * Time: 11:56
 */
public class FindFileMetaDataQuery implements Query<MetadataCache, FileMetaData> {
    private final FileId fileId;

    public FindFileMetaDataQuery(FileId fileId) {
        this.fileId = fileId;
    }

    @Override
    public FileMetaData query(MetadataCache prevalentSystem, Date executionTime) throws Exception {
        return prevalentSystem.data.get(fileId);
    }
}
