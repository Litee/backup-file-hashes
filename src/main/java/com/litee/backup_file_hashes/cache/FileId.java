package com.litee.backup_file_hashes.cache;

import java.io.Serializable;

/**
 * User: Andrey
 * Date: 2015-02-22
 * Time: 11:44
 */
public class FileId implements Serializable {
    public String path;

    public FileId(String path) {
        this.path = path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        FileId fileId = (FileId) o;

        if (!path.equals(fileId.path)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return path.hashCode();
    }
}
