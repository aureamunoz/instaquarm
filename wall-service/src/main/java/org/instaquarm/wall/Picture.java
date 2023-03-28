package org.instaquarm.wall;

import java.util.List;

public record Picture(String user, byte[] picture, List<String> tags) {
}
