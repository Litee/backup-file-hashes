package com.litee.hash_based_recovery.hashing;

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
