package org.instaquarm.uploading;


import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Lob;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;

import java.sql.Types;
import java.util.Date;

@Entity
public class Picture  extends PanacheEntity {

    public String title;

    public String owner;

    @CreationTimestamp
    public Date created;

    @Lob
    @Column(columnDefinition = "BYTEA")
    @JdbcTypeCode(Types.BINARY)
    public byte[] image;

    public Picture(String title, String owner, byte[] image) {
        this.title = title;
        this.owner = owner;
        this.image = image;
    }

    public Picture() {
    }
}
