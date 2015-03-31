package com.litee.backup_file_hashes.cache;

import com.litee.backup_file_hashes.FileMetaData;

import java.io.Serializable;
import java.util.HashMap;

/**
 * User: Andrey
 * Date: 2015-02-20
 * Time: 00:55
 */
public class MetadataCache implements Serializable {
    public HashMap<FileId, FileMetaData> data = new HashMap<FileId, FileMetaData>();
}
