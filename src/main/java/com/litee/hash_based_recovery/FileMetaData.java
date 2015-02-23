package com.litee.hash_based_recovery;

import java.io.Serializable;

/**
 * User: Andrey
 * Date: 2015-02-20
 * Time: 00:58
 */
public class FileMetaData implements Serializable {
    static final long serialVersionUID = -8705702867846772144L;
    private String fileName;
    private long fileLength;
    private long timestamp;
    private String tthAsBase32;

    public FileMetaData(String fileName, long fileLength, long timestamp, String tthAsBase32) {
        this.fileName = fileName;
        this.fileLength = fileLength;
        this.timestamp = timestamp;
        this.tthAsBase32 = tthAsBase32;
    }

    public String getFileName() {
        return fileName;
    }

    public long getFileLength() {
        return fileLength;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getTthAsBase32() {
        return tthAsBase32;
    }

    @Override
    public String toString() {
        return "FileMetaData{" +
                "fileName='" + fileName + '\'' +
                ", fileLength=" + fileLength +
                ", timestamp=" + timestamp +
                ", tthAsBase32='" + tthAsBase32 + '\'' +
                '}';
    }
}
