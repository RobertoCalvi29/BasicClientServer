package common;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.ByteBuffer;

public class ImagePacket implements Serializable
{
    public String name;
    public transient BufferedImage image;
    private String imageFormat;
    public static final String defaultFormat = "jpeg";

    public ImagePacket(String name, BufferedImage image, String imageFormat)
    {
        this.name = name;
        this.image = image;
        this.imageFormat = imageFormat;
    }

    private void writeObject(ObjectOutputStream oos) throws IOException
    {
        // Implement ObjectOutputStream.writeObject for this class
        oos.defaultWriteObject();

        // Convert to bytestream
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ImageIO.write(image, imageFormat, byteArrayOutputStream);

        // Get size to inform other side how much to read
        byte[] size = ByteBuffer.allocate(4).putInt(byteArrayOutputStream.size()).array();

        // Send it!
        oos.write(size);
        oos.write(byteArrayOutputStream.toByteArray());
    }

    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException
    {
        // Implement ObjectOutputStream.readObject for this class
        ois.defaultReadObject();

        // Convert to DataInputStream to use readFully
        DataInputStream dis = new DataInputStream(ois);
        int size = dis.readInt();
        byte[] imageAr = new byte[size];
        dis.readFully(imageAr);

        // Convert to BufferedImage
        image = ImageIO.read(new ByteArrayInputStream(imageAr));
    }
}
