package com.litee.hash_based_recovery.hashing;

import jonelo.jacksum.JacksumAPI;
import jonelo.jacksum.algorithm.AbstractChecksum;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;

/**
 * User: Andrey
 * Date: 2015-02-19
 * Time: 23:39
 */
public class StreamHashCalculatorImpl implements StreamHashCalculator {
    public final static int BUFFER_SIZE = 1024 * 1024;

    @Override
    public Hashes calculateHashes(InputStream inputStream) throws IOException {
        try {
            AbstractChecksum tthCalculator = JacksumAPI.getChecksumInstance("tree:tiger");
            BufferedInputStream bufferedInputStream = null;
            try {
              bufferedInputStream = new BufferedInputStream(inputStream);
              int len;
              byte[] buffer = new byte[BUFFER_SIZE];
              while ((len = bufferedInputStream.read(buffer)) > -1) {
                  tthCalculator.update(buffer, 0, len);
              }
            } finally {
              if (bufferedInputStream != null) bufferedInputStream.close();
            }
            return new HashesImpl(tthCalculator.getByteArray());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return new HashesImpl(new byte[0]);
    }
}
