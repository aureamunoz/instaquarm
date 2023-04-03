package org.instaquarm.funqy;

import java.util.List;

public class SquarerRequest {
    public String owner;
    public byte[] image;
    public String title;

    public SquarerRequest() {

    }

    public SquarerRequest(String owner, byte[] image, String title) {
        this.owner = owner;
        this.image = image;
        this.title = title;
    }

}
