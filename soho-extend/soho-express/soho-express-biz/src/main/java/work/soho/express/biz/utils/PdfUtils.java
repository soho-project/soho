package work.soho.express.biz.utils;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.LosslessFactory;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.io.ByteArrayInputStream;

public class PdfUtils {

    /**
     * 生成二维码的BufferedImage
     */
    public static BufferedImage generateQRCodeImage(String content, int width, int height) {
        try {
            Map<EncodeHintType, Object> hints = new HashMap<>();
            hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
            hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.M);
            hints.put(EncodeHintType.MARGIN, 1);

            BitMatrix bitMatrix = new MultiFormatWriter()
                    .encode(content, BarcodeFormat.QR_CODE, width, height, hints);

            // 转换为BufferedImage
            BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            for (int x = 0; x < width; x++) {
                for (int y = 0; y < height; y++) {
                    image.setRGB(x, y, bitMatrix.get(x, y) ? 0x000000 : 0xFFFFFF);
                }
            }

            return image;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将BufferedImage转换为PDImageXObject
     */
    public static PDImageXObject convertToPDImageXObject(PDDocument document, BufferedImage image) {
        try {
            return LosslessFactory.createFromImage(document, image);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 生成二维码并直接创建PDImageXObject
     */
    public static PDImageXObject createQRCodePDImageXObject(
            PDDocument document, String content, int width, int height) {

        BufferedImage qrImage = generateQRCodeImage(content, width, height);
        if (qrImage == null) {
            return null;
        }

        return convertToPDImageXObject(document, qrImage);
    }

    /**
     * 示例：创建包含二维码的PDF
     */
    public static void createPDFWithQRCode(String content, String outputPath) {
        try (PDDocument document = new PDDocument()) {
            // 创建页面
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            // 创建二维码PDImageXObject
            PDImageXObject qrCodeImage = createQRCodePDImageXObject(
                    document, content, 200, 200);

            if (qrCodeImage != null) {
                try (PDPageContentStream contentStream = new PDPageContentStream(
                        document, page, PDPageContentStream.AppendMode.APPEND, true)) {

                    // 在PDF上绘制二维码
                    float x = 100;  // X坐标
                    float y = 500;  // Y坐标
                    float width = qrCodeImage.getWidth();
                    float height = qrCodeImage.getHeight();

                    contentStream.drawImage(qrCodeImage, x, y, width, height);

                    // 可以添加文本描述
                    contentStream.beginText();
//                    contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                    contentStream.newLineAtOffset(x, y - 20);
                    contentStream.showText("扫描此二维码访问: " + content);
                    contentStream.endText();
                }
            }

            // 保存PDF
            document.save(outputPath);
            System.out.println("PDF创建成功: " + outputPath);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 高级示例：创建带Logo的二维码PDImageXObject
     */
    public static PDImageXObject createQRCodeWithLogo(
            PDDocument document, String content, int size, String logoPath) {

        try {
            // 生成基础二维码
            BufferedImage qrImage = generateQRCodeImage(content, size, size);
            if (qrImage == null) {
                return null;
            }

            // 如果有Logo，添加到二维码中间
            if (logoPath != null && !logoPath.isEmpty()) {
                try {
                    // 读取Logo
                    BufferedImage logo = ImageIO.read(new File(logoPath));

                    // 计算Logo大小（二维码的1/5）
                    int logoSize = size / 5;

                    // 调整Logo大小
                    Image scaledLogo = logo.getScaledInstance(logoSize, logoSize, Image.SCALE_SMOOTH);
                    BufferedImage resizedLogo = new BufferedImage(
                            logoSize, logoSize, BufferedImage.TYPE_INT_ARGB);
                    Graphics2D g2 = resizedLogo.createGraphics();
                    g2.drawImage(scaledLogo, 0, 0, null);
                    g2.dispose();

                    // 计算Logo放置位置
                    int x = (size - logoSize) / 2;
                    int y = (size - logoSize) / 2;

                    // 绘制Logo到二维码
                    Graphics2D graphics = qrImage.createGraphics();
                    graphics.drawImage(resizedLogo, x, y, null);

                    // 给Logo添加白色边框
                    int borderWidth = 2;
                    int borderRadius = 10;
                    BasicStroke stroke = new BasicStroke(
                            borderWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
                    graphics.setStroke(stroke);
                    graphics.setColor(Color.WHITE);
//                    graphics.draw(new RoundRectangle2D.Float(
//                            x - borderWidth, y - borderWidth,
//                            logoSize + borderWidth * 2, logoSize + borderWidth * 2,
//                            borderRadius, borderRadius));

                    graphics.dispose();

                } catch (IOException e) {
                    System.err.println("Logo加载失败，生成无Logo二维码: " + e.getMessage());
                }
            }

            // 转换为PDImageXObject
            return LosslessFactory.createFromImage(document, qrImage);

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 示例：在PDF中放置多个二维码
     */
    public static void createPDFWithMultipleQRCodes() {
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            try (PDPageContentStream contentStream = new PDPageContentStream(
                    document, page, PDPageContentStream.AppendMode.APPEND, true)) {

                // 第一个二维码
                String content1 = "https://www.github.com";
                PDImageXObject qrCode1 = createQRCodePDImageXObject(
                        document, content1, 150, 150);
                contentStream.drawImage(qrCode1, 50, 600, 150, 150);

                // 第二个二维码
                String content2 = "https://www.google.com";
                PDImageXObject qrCode2 = createQRCodePDImageXObject(
                        document, content2, 150, 150);
                contentStream.drawImage(qrCode2, 250, 600, 150, 150);

                // 第三个二维码
                String content3 = "https://www.baidu.com";
                PDImageXObject qrCode3 = createQRCodePDImageXObject(
                        document, content3, 150, 150);
                contentStream.drawImage(qrCode3, 450, 600, 150, 150);

                // 添加标题
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16);
                contentStream.newLineAtOffset(250, 780);
                contentStream.showText("二维码示例");
                contentStream.endText();

                // 添加说明
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA, 10);
                contentStream.newLineAtOffset(50, 550);
                contentStream.showText("GitHub");
                contentStream.newLineAtOffset(200, 0);
                contentStream.showText("Google");
                contentStream.newLineAtOffset(200, 0);
                contentStream.showText("百度");
                contentStream.endText();

            }

            document.save("multiple_qrcodes.pdf");
            System.out.println("多二维码PDF创建成功");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 将二维码保存为字节数组，用于其他用途
     */
    public static byte[] getQRCodeAsBytes(String content, int width, int height) {
        try {
            BufferedImage qrImage = generateQRCodeImage(content, width, height);
            if (qrImage == null) {
                return null;
            }

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(qrImage, "PNG", baos);
            return baos.toByteArray();

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 从字节数组创建PDImageXObject
     */
    public static PDImageXObject createPDImageFromBytes(
            PDDocument document, byte[] imageBytes) throws IOException {

        BufferedImage image = ImageIO.read(new ByteArrayInputStream(imageBytes));
        return LosslessFactory.createFromImage(document, image);
    }

    public static void main(String[] args) {
        // 示例1：创建包含单个二维码的PDF
        createPDFWithQRCode("https://www.example.com", "qrcode_document.pdf");

        // 示例2：创建包含多个二维码的PDF
        createPDFWithMultipleQRCodes();

        // 示例3：创建带Logo的二维码PDF
        try (PDDocument document = new PDDocument()) {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);

            PDImageXObject qrWithLogo = createQRCodeWithLogo(
                    document, "https://www.company.com", 250, "company_logo.png");

            if (qrWithLogo != null) {
                try (PDPageContentStream contentStream = new PDPageContentStream(
                        document, page, PDPageContentStream.AppendMode.APPEND, true)) {

                    contentStream.drawImage(qrWithLogo, 100, 500,
                            qrWithLogo.getWidth(), qrWithLogo.getHeight());
                }

                document.save("qrcode_with_logo.pdf");
                System.out.println("带Logo的二维码PDF创建成功");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}