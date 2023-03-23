package org.instaquarm.funqy;

import java.util.List;

public class SquarerRequest {
    public String user;
    public byte[] picture;
    public List<String> tags;

    public SquarerRequest() {

    }

    public SquarerRequest(String user, byte[] picture, List<String> tags) {
        this.user = user;
        this.picture = picture;
        this.tags = tags;
    }

}
