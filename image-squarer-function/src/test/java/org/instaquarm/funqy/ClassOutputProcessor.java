package org.instaquarm.funqy;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.LinkedHashSet;
import java.util.Set;

@Disabled
public class ClassOutputProcessor {

    @Test
    void process() throws IOException {
        File classes = new File("target/classes.txt");
        var lines = Files.readAllLines(classes.toPath());

        Set<String> output = new LinkedHashSet<>();
        // EVENTS	1680608393748	[0.529s][info][class,load] com.amazonaws.services.lambda.runtime.serialization.util.ReflectUtil$ReflectException source: shared objects file
        for (String line : lines) {
            var l = line.trim();
            if (l.startsWith("EVENTS")  && l.contains("[class,load]")  && ! l.contains("opened:")) {
                var c = l.substring(l.lastIndexOf("]") +1, l.indexOf("source")).trim();
                if (! c.isBlank()) {
                    output.add(c);
                }
            }
        }

        Files.write(new File("target/quarkus-preload-classes.txt").toPath(), output);
    }
}
