package work.soho.common.data.avatar.utils;

import lombok.extern.log4j.Log4j2;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * 头像生成工具类
 */
@Log4j2
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
            log.error(e.getStackTrace());
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
        int lostCount = gridCount * gridCount - imageUrls.length;

        int index = 0;
        for (int row = 0; row < gridCount; row++) {
            for (int col = 0; col < gridCount; col++) {
                if(index<imageUrls.length) {
                    String imageUrl = imageUrls[index];
                    boolean merge = (imageUrls.length - lostCount)<=index;
                    BufferedImage cell = loadImageFromUrl(imageUrl, cellSize, merge ? 2*cellSize : cellSize);
                    Graphics2D graphics = resultImage.createGraphics();
                    graphics.drawImage(cell, col * cellSize, row * cellSize, null);
                    graphics.dispose();
                    index++;
                } else {
                    break;
                }
            }
        }

        return resultImage;
    }

    /**
     * 从URL加载图片
     *
     * @param imageUrl
     * @param targetWidth
     * @param targetHeight
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
            double scaleFactor = Math.max(widthRatio, heightRatio);

            int newWidth = (int) (originalWidth * scaleFactor);
            int newHeight = (int) (originalHeight * scaleFactor);

            //确定x,y坐标
            int x = (newWidth-targetWidth)/2;
            int y = (newHeight-targetHeight)/2;

            //缩放图片
            Image image = loadedImage.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
            //裁剪成需要的图片大小
            BufferedImage resizedImage = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
            resizedImage.getGraphics().drawImage(image, 0, 0, null);
            return resizedImage.getSubimage(x,y, targetWidth, targetHeight);
        } catch (IOException e) {
            log.error(e.getStackTrace());
        }
        return null;
    }

}
