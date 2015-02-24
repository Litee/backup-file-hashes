package com.litee.backup_file_hashes;

import java.io.Serializable;

/**
 * User: Andrey
 * Date: 2015-02-20
 * Time: 00:58
 */
public class FileMetaData implements Serializable {
    static final long serialVersionUID = -8705702867846772144L;
    private final String fileName;
    private final long fileLength;
    private final long created;
    private final long lastModified;
    private final String tthAsBase32;

    public FileMetaData(String fileName, long fileLength, long created, long lastModified, String tthAsBase32) {
        this.fileName = fileName;
        this.fileLength = fileLength;
        this.created = created;
        this.lastModified = lastModified;
        this.tthAsBase32 = tthAsBase32;
    }

    public String getFileName() {
        return fileName;
    }

    public long getFileLength() {
        return fileLength;
    }

    public long getCreated() {
        return created;
    }

    public long getLastModified() {
        return lastModified;
    }

    public String getTthAsBase32() {
        return tthAsBase32;
    }

    @Override
    public String toString() {
        return "FileMetaData{" +
                "fileName='" + fileName + '\'' +
                ", fileLength=" + fileLength +
                ", created=" + created +
                ", lastModified=" + lastModified +
                ", tthAsBase32='" + tthAsBase32 + '\'' +
                '}';
    }
}
