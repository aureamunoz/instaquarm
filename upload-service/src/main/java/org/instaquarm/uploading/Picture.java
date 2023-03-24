package org.instaquarm.uploading;


import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.Type;
import org.hibernate.type.descriptor.jdbc.BinaryJdbcType;

import java.time.LocalDateTime;
import java.util.Date;

@Entity
public class Picture  {

    @Id
    @GeneratedValue
    private Long id;

    private String title;

    @CreationTimestamp
    private Date created;

//    @Lob
////    @Type(String.class)
//    @Column(columnDefinition = "BYTEA")
//    private byte[] image;


    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Date getCreated() {
        return created;
    }
}
