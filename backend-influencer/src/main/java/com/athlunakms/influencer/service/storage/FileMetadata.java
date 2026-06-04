package com.athlunakms.influencer.service.storage;

public class FileMetadata {
    private String fileKey;
    private String fileName;
    private Long size;
    private String contentType;
    private Long lastModified;
    private boolean exists;

    public String getFileKey() {
        return this.fileKey;
    }

    public String getFileName() {
        return this.fileName;
    }

    public Long getSize() {
        return this.size;
    }

    public String getContentType() {
        return this.contentType;
    }

    public Long getLastModified() {
        return this.lastModified;
    }

    public boolean isExists() {
        return this.exists;
    }

    public void setFileKey(String fileKey) {
        this.fileKey = fileKey;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public void setLastModified(Long lastModified) {
        this.lastModified = lastModified;
    }

    public void setExists(boolean exists) {
        this.exists = exists;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof FileMetadata)) {
            return false;
        }
        FileMetadata other = (FileMetadata)o;
        if (!other.canEqual((Object)this)) {
            return false;
        }
        if (this.isExists() != other.isExists()) {
            return false;
        }
        Long this$size = this.getSize();
        Long other$size = other.getSize();
        if (this$size == null ? other$size != null : !((Object)this$size).equals(other$size)) {
            return false;
        }
        Long this$lastModified = this.getLastModified();
        Long other$lastModified = other.getLastModified();
        if (this$lastModified == null ? other$lastModified != null : !((Object)this$lastModified).equals(other$lastModified)) {
            return false;
        }
        String this$fileKey = this.getFileKey();
        String other$fileKey = other.getFileKey();
        if (this$fileKey == null ? other$fileKey != null : !this$fileKey.equals(other$fileKey)) {
            return false;
        }
        String this$fileName = this.getFileName();
        String other$fileName = other.getFileName();
        if (this$fileName == null ? other$fileName != null : !this$fileName.equals(other$fileName)) {
            return false;
        }
        String this$contentType = this.getContentType();
        String other$contentType = other.getContentType();
        return !(this$contentType == null ? other$contentType != null : !this$contentType.equals(other$contentType));
    }

    protected boolean canEqual(Object other) {
        return other instanceof FileMetadata;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + (this.isExists() ? 79 : 97);
        Long $size = this.getSize();
        result = result * 59 + ($size == null ? 43 : ((Object)$size).hashCode());
        Long $lastModified = this.getLastModified();
        result = result * 59 + ($lastModified == null ? 43 : ((Object)$lastModified).hashCode());
        String $fileKey = this.getFileKey();
        result = result * 59 + ($fileKey == null ? 43 : $fileKey.hashCode());
        String $fileName = this.getFileName();
        result = result * 59 + ($fileName == null ? 43 : $fileName.hashCode());
        String $contentType = this.getContentType();
        result = result * 59 + ($contentType == null ? 43 : $contentType.hashCode());
        return result;
    }

    public String toString() {
        return "FileMetadata(fileKey=" + this.getFileKey() + ", fileName=" + this.getFileName() + ", size=" + this.getSize() + ", contentType=" + this.getContentType() + ", lastModified=" + this.getLastModified() + ", exists=" + this.isExists() + ")";
    }
}

