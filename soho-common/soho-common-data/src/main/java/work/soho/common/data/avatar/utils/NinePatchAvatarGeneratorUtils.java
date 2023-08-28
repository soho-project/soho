package work.soho.common.data.avatar.utils;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageOutputStream;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;

/**
 * 头像生成工具类
 */
public class NinePatchAvatarGeneratorUtils {

    /**
     * 创建九宫格头像
     *
     * @param imageSize
     * @param gridCount
     * @param imageUrls
     * @return
     */
    public static InputStream create(int imageSize, int gridCount,String[] imageUrls,String formatName) {
        BufferedImage bufferedImage = createImage(imageSize, gridCount, imageUrls);
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        ImageOutputStream imageOutputStream = new MemoryCacheImageOutputStream(os);
        try {
            ImageIO.write(bufferedImage, formatName, imageOutputStream);
            return new ByteArrayInputStream(os.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 默认创建方法
     *
     * @param imageSize
     * @param gridCount
     * @param imageUrls
     * @return
     */
    public static InputStream create(int imageSize, int gridCount, String[] imageUrls) {
        return create(imageSize, gridCount, imageUrls, "PNG");
    }


    /**
     * 生成九宫格图片
     *
     * @param imageSize
     * @param gridCount
     * @param imageUrls
     * @return
     */
    public static BufferedImage createImage(int imageSize, int gridCount, String[] imageUrls) {
        BufferedImage resultImage = new BufferedImage(imageSize, imageSize, BufferedImage.TYPE_INT_ARGB);

        int cellSize = imageSize / gridCount;

        int index = 0;
        for (int row = 0; row < gridCount; row++) {
            for (int col = 0; col < gridCount; col++) {
                String imageUrl = imageUrls[index % imageUrls.length];
                BufferedImage cell = loadImageFromUrl(imageUrl, cellSize, cellSize);
                Graphics2D graphics = resultImage.createGraphics();
                graphics.drawImage(cell, col * cellSize, row * cellSize, null);
                graphics.dispose();

                index++;
            }
        }

        return resultImage;
    }

    /**
     * 从URL加载图片
     *
     * @param imageUrl
     * @param width
     * @param height
     * @return
     */
    public static BufferedImage loadImageFromUrl(String imageUrl, int targetWidth, int targetHeight) {
        try {
            URL url = new URL(imageUrl);
            BufferedImage loadedImage = ImageIO.read(url);

            int originalWidth = loadedImage.getWidth();
            int originalHeight = loadedImage.getHeight();

            double widthRatio = (double) targetWidth / originalWidth;
            double heightRatio = (double) targetHeight / originalHeight;
            double scaleFactor = Math.min(widthRatio, heightRatio);

            int newWidth = (int) (originalWidth * scaleFactor);
            int newHeight = (int) (originalHeight * scaleFactor);

            BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
            resizedImage.getGraphics().drawImage(loadedImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH), 0, 0, null);

            return resizedImage;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
}
