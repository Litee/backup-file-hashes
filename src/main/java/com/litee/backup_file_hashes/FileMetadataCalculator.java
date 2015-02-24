package com.litee.backup_file_hashes;

import java.io.File;

/**
 * User: Andrey
 * Date: 2015-02-20
 * Time: 01:01
 */
public interface FileMetadataCalculator {
    FileMetaData processFile(File file) throws Exception;
}
