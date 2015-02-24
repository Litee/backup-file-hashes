package com.litee.backup_file_hashes.hashing;

/**
 * User: Andrey
 * Date: 2015-02-19
 * Time: 23:59
 */
public class HashesImpl implements Hashes {
    private byte[] tth;

    public HashesImpl(byte[] tth) {
        this.tth = tth;
    }

    @Override
    public byte[] getTTH() {
        return tth;
    }
}
