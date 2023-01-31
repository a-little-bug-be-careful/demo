package com.example.demo2.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.bson.types.Binary;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.Date;

@Data
@NoArgsConstructor
@Document
public class MongoFile {
    private String id;
    private String name;
    private String contentType;
    private long size;
    private Date uploadDate;
    private String md5;
    private Binary content;
    private String path;

    public MongoFile(String name, String contentType, long size, Binary content) {
        this.name = name;
        this.contentType = contentType;
        this.size = size;
        this.uploadDate = new Date();
        this.content = content;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) {
            return true;
        }
        if(obj == null || getClass() != obj.getClass()) {
            return false;
        }
        MongoFile fileInfo = (MongoFile) obj;
        return java.util.Objects.equals(size, fileInfo.size)
                && java.util.Objects.equals(name, fileInfo.name)
                && java.util.Objects.equals(contentType, fileInfo.contentType)
                && java.util.Objects.equals(uploadDate, fileInfo.uploadDate)
                && java.util.Objects.equals(md5, fileInfo.md5)
                && java.util.Objects.equals(id, fileInfo.id);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(name, contentType, size, uploadDate, md5, id);
    }
}

