package org.instaquarm.funqy;

import java.util.List;

public class SquarerResponse {

    public String owner;
    public byte[] image;
    public String title;

    public SquarerResponse() {

    }

    public SquarerResponse(String owner, byte[] image, String title) {
        this.owner = owner;
        this.image = image;
        this.title = title;
    }

}
