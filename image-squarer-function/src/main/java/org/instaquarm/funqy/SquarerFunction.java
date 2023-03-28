package org.instaquarm.funqy;

import io.quarkus.funqy.Funq;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class SquarerFunction {

    @Funq
    public SquarerResponse squareFunction(SquarerRequest request) throws IOException {
        Image image = makeItSmall(makeItSquare(request.picture));
        return new SquarerResponse(request.user, getBytes(image), request.tags);
    }


    private BufferedImage makeItSquare(byte[] bytes) throws IOException {
        var image = ImageIO.read(new ByteArrayInputStream(bytes));

        var squareSize = Math.min(image.getWidth(), image.getHeight());
        if (image.getWidth() > image.getHeight()) {
            // Landscape
            var cut = (int) Math.round((image.getWidth() - image.getHeight()) / 2.0);
            return image.getSubimage(cut, 0, squareSize, squareSize);
        } else if (image.getWidth() < image.getHeight()) {
            // Portrait
            var cut = (int) Math.round((image.getHeight() - image.getWidth()) / 2.0);
            return image.getSubimage(0, cut, squareSize, squareSize);
        } else {
            // Square already
            return image;
        }
    }

    private Image makeItSmall(BufferedImage image) {
        if (image.getWidth() > 1024) {
            return image.getScaledInstance(1024, 1024, Image.SCALE_SMOOTH);
        } else {
            return image;
        }
    }

    public static byte[] getBytes(Image image) throws IOException {
        BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = bufferedImage.createGraphics();
        g2d.drawImage(image, 0, 0, null);
        g2d.dispose();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, "jpg", baos);
        return baos.toByteArray();
    }

}
