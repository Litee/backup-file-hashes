package com.litee.backup_file_hashes.hashing;

import java.io.IOException;
import java.io.InputStream;

/**
 * User: Andrey
 * Date: 2015-02-19
 * Time: 23:58
 */
public interface StreamHashCalculator {
    Hashes calculateHashes(InputStream inputStream) throws IOException;
}
