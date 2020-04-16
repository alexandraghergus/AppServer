package database;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Base64;

public class Pictures {
    public static String picturePath = "\\pictures\\";

    // Base64 blob as png
    public static String UploadPicture(String blob, String pictureID){
        byte[] bytes = Base64.getDecoder().decode(blob);
        ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
        try {
            BufferedImage image = ImageIO.read(bis);
            File output = new File(Pictures.picturePath + pictureID+ ".png");
            ImageIO.write(image, "png", output);
        } catch (IOException e) {
            return Utils.createResult("error", "Could not upload file.");
        }
        return Utils.createResult("successful", "File upladed.");
    }

    // Base64 blob to be turned into png
    public static String GetPicture(String pictureid){
        String path = Pictures.picturePath + pictureid + ".png";
        String imageString = "";
        try {
            BufferedImage img = ImageIO.read(new File(path));
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ImageIO.write(img, "png", bos);
            imageString = Base64.getEncoder().encodeToString(bos.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return imageString;

    }

}
