package work.soho.express.biz.apis.adapter.zto;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType0Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.pdmodel.graphics.state.RenderingMode;
import org.apache.pdfbox.util.Matrix;
import work.soho.express.api.dto.PrintInfoDTO;
import work.soho.express.biz.utils.PdfUtils;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.EnumMap;
import java.util.Map;

public class PdfCreater {
    // 官方尺寸：76mm x 130mm
    private static final float PAGE_W = mm2pt(76);
    private static final float PAGE_H = mm2pt(130);
    // 模版内边距 (根据图纸四周留白约为 4mm)
    private static final float M = mm2pt(4.0f);

    public static byte[] buildPdf(PrintInfoDTO billPrintDTO) throws Exception {
        try (PDDocument doc = new PDDocument();
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {

            PDPage page = new PDPage(new PDRectangle(PAGE_W, PAGE_H));
            doc.addPage(page);

            // 加载字体 (务必确保路径正确)
            PDType0Font font;
            try (InputStream is = new FileInputStream("/home/fang/Documents/宋体.ttf")) {
                font = PDType0Font.load(doc, is);
            }

            try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {
                // 顶部边框运单号打印
                float topPartWidth = 76f/3;
                for (int i = 0; i < 3; i++) {
                    float startX = topPartWidth * i;
                    drawText(cs, font, 6f, mm2pt(startX + 5f), PAGE_H - M, "* " + billPrintDTO.getPrintParam().getMailNo());
                }

                // 底部边框打印运单号
                float partWidth = 76f/3;
                for (int i = 0; i < 3; i++) {
                    float startX = partWidth * i;
                    drawText(cs, font, 6f, mm2pt(startX + 5f), mm2pt(1f), "* " + billPrintDTO.getPrintParam().getMailNo());
                }

                // 绘制右边 边缘运单号
                float partheight =  (130f - 8f)/5;
                for (int i = 0; i < 5; i++) {
                    float startY = partheight * i;
//                    drawTextRotated(cs, font, 6f, mm2pt(68 + 5), mm2pt(130 - startY - 11f), "*" +billPrintDTO.getPrintParam().getMailNo());
                    drawTextRotated270(cs, font, 6f, mm2pt(68 + 7), mm2pt(startY + 5f), "* " +billPrintDTO.getPrintParam().getMailNo());
                }

                // 绘制左边边缘运单号
                for (int i = 0; i < 5; i++) {
                    float startY = partheight * i;
//                    drawTextRotated270(cs, font, 6f, mm2pt(3), mm2pt(startY + 5f), "*" +billPrintDTO.getPrintParam().getMailNo());
                    drawTextRotated(cs, font, 6f, mm2pt(1), mm2pt(130 - startY - 11f), "* " +billPrintDTO.getPrintParam().getMailNo());
                }

                // 绘制打印时间
                drawText(cs, font, 6f,  2 * M, PAGE_H - M - mm2pt(11f), LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss")));

                // 绘制右上角
                drawRect(cs, PAGE_W - M - mm2pt(12f), PAGE_H - M - mm2pt(12f), mm2pt(10f), mm2pt(10f), 0.8f);
                drawMultiline(cs, font, 12f, PAGE_W - M - mm2pt(11.5f), PAGE_H - M - mm2pt(5.8f), "快递包裹", mm2pt(10f),  2);

                // 1. 整体框架与右侧纵栏 (W:14mm H:58mm 区域)
                float rightBarW = mm2pt(14.0f);
                float leftAreaW = PAGE_W - (2 * M) - rightBarW;
                float rightX = PAGE_W - M - rightBarW;

                // 最外围的大方框
//                drawRect(cs, M, M, PAGE_W - 2 * M, PAGE_H - 2 * M, 0.8f);

                // 2. 顶部：普快反色块 (W:16mm H:12mm 黑底白字)
//                cs.setNonStrokingColor(Color.BLACK);
//                cs.addRect(PAGE_W - mm2pt(25), PAGE_H - mm2pt(2) - mm2pt(12), mm2pt(25), mm2pt(12));
//                cs.fill();
//                cs.setNonStrokingColor(Color.WHITE);


                cs.setNonStrokingColor(Color.BLACK);

                // 整理区域划分
                float topAreaH = 24.52f;
                float rightSubAreaWidth = mm2pt(57);
                drawLine(cs, M, PAGE_H-M-mm2pt(topAreaH), PAGE_W - M, PAGE_H-M-mm2pt(topAreaH), 0.5f);

                // 打印 mark
                drawTextCenter(cs, font, 20f, M, PAGE_H - mm2pt(topAreaH), PAGE_W - 2 * M, billPrintDTO.getPrintParam().getPrintMark());

                topAreaH = 41.7f;
                drawLine(cs, M, PAGE_H-M-mm2pt(topAreaH), PAGE_W - M, PAGE_H-M-mm2pt(topAreaH), 0.5f);

                // 打印二维码
                PDImageXObject hBarX = toX(doc, code128(billPrintDTO.getPrintParam().getMailNo(), 600, 200));
                //        float barTop = posTop - mm2pt(10);
                cs.drawImage(hBarX, M, PAGE_H - mm2pt(topAreaH), mm2pt(76 - 8f), mm2pt(12f));
                drawTextCenter(cs, font, 8f, M, PAGE_H - mm2pt(topAreaH + 2.6f), PAGE_W - 2 * M , billPrintDTO.getPrintParam().getMailNo(), true);

                // 竖线
                drawLine(cs, rightSubAreaWidth, PAGE_H-M-mm2pt(topAreaH), rightSubAreaWidth, PAGE_H-M-mm2pt(100.56f), 0.5f);


                topAreaH = 52.73f;
                drawLine(cs, M, PAGE_H-M-mm2pt(topAreaH), PAGE_W -  M, PAGE_H-M-mm2pt(topAreaH), 0.5f);
                //集包地区
                drawText(cs, font, 17f, M + mm2pt(1), PAGE_H - mm2pt(topAreaH), billPrintDTO.getPrintParam().getPrintBagaddr());

                // 画竖线
                drawLine(cs, mm2pt(15f), PAGE_H-M-mm2pt(topAreaH), mm2pt(15f), PAGE_H-M-mm2pt(85.84f), 0.5f);

                topAreaH = 76.03f;
                drawLine(cs, M, PAGE_H-M-mm2pt(topAreaH), rightSubAreaWidth, PAGE_H-M-mm2pt(topAreaH), 0.5f);
                // 收圆圈
                drawCircleIcon(cs, font, M + mm2pt(3), PAGE_H -M - mm2pt(topAreaH-12f), "收");
                drawText(cs, font, 11.38f, mm2pt(16f), PAGE_H - mm2pt(topAreaH - 16f), billPrintDTO.getReceiver().getName() + " " + billPrintDTO.getReceiver().getMobile());
                drawMultiline(cs, font, 11.38f, mm2pt(16), PAGE_H - mm2pt(topAreaH - 11f),
                        billPrintDTO.getReceiver().getProv() + " "
                        + billPrintDTO.getReceiver().getCity() + " "
                        + billPrintDTO.getReceiver().getCounty() + " "
                        + billPrintDTO.getReceiver().getAddress() + " "
                        , mm2pt(42), 4);

                topAreaH = 85.84f;
                drawLine(cs, M, PAGE_H-M-mm2pt(topAreaH), rightSubAreaWidth, PAGE_H-M-mm2pt(topAreaH), 0.5f);
                // 寄
                drawTextCenter(cs, font, 16f, M, PAGE_H - mm2pt(topAreaH), mm2pt(15f) - M, "寄");

                drawText(cs, font, 7f, mm2pt(16f), PAGE_H - mm2pt(topAreaH - 3f), billPrintDTO.getReceiver().getName() + " " + billPrintDTO.getReceiver().getMobile());
                drawMultiline(cs, font, 6f, mm2pt(16), PAGE_H - mm2pt(topAreaH - 0.6f),
                        billPrintDTO.getSender().getProv() + " "
                                + billPrintDTO.getSender().getCity() + " "
                                + billPrintDTO.getSender().getCounty() + " "
                                + billPrintDTO.getSender().getAddress() + " "
                        , mm2pt(41), 4);

                topAreaH = 100.56f;
                drawLine(cs, M, PAGE_H-M-mm2pt(topAreaH), PAGE_W - M, PAGE_H-M-mm2pt(topAreaH), 0.5f);
                // 用户备注区域
                String remark = "品名内容：";
                for(PrintInfoDTO.Goods goods: billPrintDTO.getGoods()) {
                    remark += goods.getName() + " *" + goods.getQty() + " " + goods.getRemark() + "\n";
                }

                // 添加备注
                if(billPrintDTO.getPrintParam().getRemark() != null && billPrintDTO.getPrintParam().getRemark().length() > 0) {
                    remark += "\n备注：";
                    remark += billPrintDTO.getPrintParam().getRemark();
                }

                drawMultiline(cs, font, 6f, M + mm2pt(1f), PAGE_H - mm2pt(topAreaH - 8f), remark
                , mm2pt(rightSubAreaWidth) - M, 5);

                // 绘制侧边条码
                PDImageXObject vBarX = toX(doc, code128(billPrintDTO.getPrintParam().getMailNo(), 800, 150));
                drawRotatedImage90(cs, vBarX, rightSubAreaWidth + M + mm2pt(2f), PAGE_H - mm2pt(topAreaH - 21),
                        mm2pt(45), mm2pt(10));

                drawTextRotated270(cs, font, 6f, rightSubAreaWidth + M + mm2pt(9.5f),
                        PAGE_H - mm2pt(topAreaH - 15f), billPrintDTO.getPrintParam().getMailNo());



                topAreaH = 118f;
                drawLine(cs, M, PAGE_H-M-mm2pt(topAreaH), PAGE_W - M, PAGE_H-M-mm2pt(topAreaH), 0.5f);

                // 绘制底部二维码
                PDImageXObject qrcode = PdfUtils.createQRCodePDImageXObject(doc, "https://q.zto.com/c/idx?channel=ydy", (int)mm2pt(20), (int)mm2pt(20));
                cs.drawImage(qrcode, M , M + mm2pt(9f), mm2pt(12), mm2pt(12));
                String serviceText = "本次服务适用中通官网(www.zto.com)公示的快递服务协议条款，您对此次的签收代表您已收到快件且包装完好无损。";
                drawMultiline(cs, font, 6f, M + mm2pt(12), PAGE_H - mm2pt(topAreaH - 11f), serviceText
                        , mm2pt(76 - 8 - 12), 5);

                drawText(cs, font, 8f, M + mm2pt(13), PAGE_H - mm2pt(topAreaH - 3f), "签收人/时间：");

                // 已验视， 已实名
                drawText(cs, font, 8f, mm2pt(76 - 24f), PAGE_H - mm2pt(topAreaH + 7f), "已验视 已实名");
            }

            doc.save(baos);
            return baos.toByteArray();
        }
    }

    private static void drawTextRotated(PDPageContentStream cs, PDType0Font font, float size, float x, float y, String text) throws IOException {
        if (text == null || text.isEmpty()) return;

        cs.beginText();
        cs.setFont(font, size);

        // 顺时针旋转90度：-90度（弧度制：-π/2）
        // 设置文本矩阵：[cosθ, sinθ, -sinθ, cosθ, x, y]
        // 顺时针旋转90度：cos(90°)=0, sin(90°)=1，但注意是负角度
        // 顺时针旋转90度 = -90度：cos(-90°)=0, sin(-90°)=-1
        cs.setTextMatrix(new Matrix(0, -1, 1, 0, x, y));

        cs.showText(text);
        cs.endText();
    }

    private static void drawTextRotated270(PDPageContentStream cs, PDType0Font font, float size, float x, float y, String text) throws IOException {
        if (text == null || text.isEmpty()) return;

        cs.beginText();
        cs.setFont(font, size);

        // 顺时针旋转270度 = 逆时针旋转90度
        // 旋转角度：θ = 270° (顺时针) 或 -90° (逆时针)
        // cos(270°) = cos(-90°) = 0
        // sin(270°) = -1, sin(-90°) = -1
        // 文本矩阵：[cosθ, sinθ, -sinθ, cosθ, x, y]
        cs.setTextMatrix(new Matrix(0, 1, -1, 0, x, y));

        cs.showText(text);
        cs.endText();
    }

    /**
     * 绘制带圆圈的文字图标 (交/收/寄)
     */
    private static void drawCircleIcon(PDPageContentStream cs, PDType0Font font, float x, float y, String text) throws IOException {
        cs.setLineWidth(0.8f);
        // 绘制圆形边界
        drawOval(cs, x, y - 2f, 12f, 12f);
        cs.stroke();

        // 绘制居中文字
        drawText(cs, font, 8f, x + 2.0f, y + 0.8f, text);
    }

    private static void drawRectIcon(PDPageContentStream cs, PDType0Font font, float x, float y, String text, String packageWdjc) throws IOException {
        cs.setLineWidth(0.8f);
        // 绘制圆形边界
        drawRect(cs, x + mm2pt(0.5f), y + mm2pt(0.5f), mm2pt(5f), mm2pt(5f), mm2pt(1f));
        // 绘制居中文字
        drawText(cs, font, mm2pt(4f), x + mm2pt(1f), y + mm2pt(1.5f), text);

        drawText(cs, font, 12f, x + mm2pt(6f),  y + mm2pt(1.5f), packageWdjc);
    }

    /**
     * 兼容性辅助方法：在 PDFBox 中绘制椭圆/圆
     */
    private static void drawOval(PDPageContentStream cs, float x, float y, float width, float height) throws IOException {
        float magic = 0.551784f; // 贝塞尔常数
        float dx = max(width / 2, 0);
        float dy = max(height / 2, 0);
        float cx = x + dx;
        float cy = y + dy;
        float ox = dx * magic;
        float oy = dy * magic;

        cs.moveTo(cx - dx, cy);
        cs.curveTo(cx - dx, cy + oy, cx - ox, cy + dy, cx, cy + dy);
        cs.curveTo(cx + ox, cy + dy, cx + dx, cy + oy, cx + dx, cy);
        cs.curveTo(cx + dx, cy - oy, cx + ox, cy - dy, cx, cy - dy);
        cs.curveTo(cx - ox, cy - dy, cx - dx, cy - oy, cx - dx, cy);
        cs.closePath();
    }

    // 别忘了引入 Math.max
    private static float max(float a, float b) {
        return Math.max(a, b);
    }

    // --- 核心工具函数 ---

    private static float mm2pt(float mm) { return (mm / 25.4f) * 72f; }

    private static void drawText(PDPageContentStream cs, PDType0Font font, float size, float x, float y, String text) throws IOException {
        drawText(cs, font, size, x, y, text, false);
    }

    private static void drawText(PDPageContentStream cs, PDType0Font font, float size, float x, float y, String text, boolean bold) throws IOException {
        if (text == null || text.isEmpty()) return;

        if (bold) {
            // 设置文本渲染模式为填充+描边
            cs.setRenderingMode(RenderingMode.FILL_STROKE);
            cs.setLineWidth(size * 0.04f);  // 设置描边宽度
        }

        cs.beginText();
        cs.setFont(font, size);
        cs.newLineAtOffset(x, y);
        cs.showText(text);
        cs.endText();

        if (bold) {
            cs.setRenderingMode(RenderingMode.FILL);  // 恢复默认渲染模式
        }
    }

    /**
     * 居中写入文字（相对于指定宽度区域）
     * @param cs PDPageContentStream对象
     * @param font 字体
     * @param size 字号
     * @param regionX 区域起始X坐标
     * @param regionY 区域Y坐标（文字基线位置）
     * @param regionWidth 区域宽度
     * @param text 要写入的文字
     * @throws IOException
     */
    private static void drawTextCenter(PDPageContentStream cs, PDType0Font font, float size,
                                       float regionX, float regionY, float regionWidth, String text) throws IOException {
        drawTextCenter(cs, font, size, regionX, regionY, regionWidth, text, false);
    }

    /**
     * 居中写入文字（相对于指定宽度区域）
     * @param cs PDPageContentStream对象
     * @param font 字体
     * @param size 字号
     * @param regionX 区域起始X坐标
     * @param regionY 区域Y坐标（文字基线位置）
     * @param regionWidth 区域宽度
     * @param text 要写入的文字
     * @param fill 是否填充（true为填充，false为描边）
     * @throws IOException
     */
    private static void drawTextCenter(PDPageContentStream cs, PDType0Font font, float size,
                                       float regionX, float regionY, float regionWidth,
                                       String text, boolean fill) throws IOException {
        // 计算文本宽度
        float textWidth = getStringWidth(text, font, size);

        // 计算起始X坐标：区域起始X + (区域宽度 - 文本宽度) / 2
        float startX = regionX + (regionWidth - textWidth) / 2;

        // 调用原有的drawText函数
        drawText(cs, font, size, startX, regionY, text, fill);
    }

    /**
     * 计算字符串宽度
     * @param text 要计算的文本
     * @param font 字体
     * @param size 字号
     * @return 文本宽度（单位：点）
     * @throws IOException
     */
    private static float getStringWidth(String text, PDType0Font font, float size) throws IOException {
        System.out.println("getStringWidth: " + text);
        // 获取文本在1000单位下的宽度
        float stringWidth = font.getStringWidth(text);

        // 转换为实际尺寸（乘以字体大小/1000）
        return stringWidth * size / 1000.0f;
    }

    private static void drawMultiline(PDPageContentStream cs, PDType0Font font, float size, float x, float y, String text, float maxW, int maxLines) throws IOException {
        if (text == null || text.isEmpty()) return;

        float leading = size * 1.1f;
        int drawnLines = 0;

        // 首先按换行符分割文本
        String[] paragraphs = text.split("\n", -1);  // -1 表示保留空行

        for (String paragraph : paragraphs) {
            if (drawnLines >= maxLines) break;

            String remaining = paragraph;
            while (!remaining.isEmpty() && drawnLines < maxLines) {
                // 计算当前行能显示多少个字符
                int charsToDraw = 0;
                while (charsToDraw < remaining.length()) {
                    String sub = remaining.substring(0, charsToDraw + 1);
                    float width = font.getStringWidth(sub) / 1000 * size;
                    if (width > maxW) {
                        // 如果第一个字符就超过宽度，强制显示一个字符
                        if (charsToDraw == 0) {
                            charsToDraw = 1;
                        }
                        break;
                    }
                    charsToDraw++;
                }

                // 如果一行都装不下，按字符截断
                if (charsToDraw == 0 && !remaining.isEmpty()) {
                    charsToDraw = 1;
                }

                // 绘制当前行
                String line = remaining.substring(0, Math.min(charsToDraw, remaining.length()));
                drawText(cs, font, size, x, y - (drawnLines * leading), line);

                // 更新剩余文本和已绘制行数
                remaining = remaining.substring(Math.min(charsToDraw, remaining.length()));
                drawnLines++;
            }

            // 如果还有剩余文本但已达到最大行数，退出
            if (drawnLines >= maxLines) break;
        }
    }

    private static void drawLine(PDPageContentStream cs, float x1, float y1, float x2, float y2, float width) throws IOException {
        cs.setLineWidth(width);
        cs.moveTo(x1, y1);
        cs.lineTo(x2, y2);
        cs.stroke();
    }

    private static void drawRect(PDPageContentStream cs, float x, float y, float w, float h, float width) throws IOException {
        cs.setLineWidth(width);
        cs.addRect(x, y, w, h);
        cs.stroke();
    }

    private static void drawRotatedImage90(PDPageContentStream cs, PDImageXObject img, float cx, float cy, float w, float h) throws IOException {
        cs.saveGraphicsState();
        cs.transform(new Matrix(0, 1, -1, 0, cx, cy));
        cs.drawImage(img, -w / 2, -h / 2, w, h);
        cs.restoreGraphicsState();
    }

    private static String text(JsonNode n, String f) { return n.path(f).asText(""); }
    private static String firstNonEmpty(String a, String b) { return (a != null && !a.isEmpty()) ? a : b; }

    private static BufferedImage code128(String content, int w, int h) throws Exception {
        Map<EncodeHintType, Object> hnts = new EnumMap<>(EncodeHintType.class);
        hnts.put(EncodeHintType.MARGIN, 0);
        BitMatrix bm = new MultiFormatWriter().encode(content, BarcodeFormat.CODE_128, w, h, hnts);
        return MatrixToImageWriter.toBufferedImage(bm);
    }

    private static BufferedImage qrCode(String content, int w, int h) throws Exception {
        Map<EncodeHintType, Object> hnts = new EnumMap<>(EncodeHintType.class);
        hnts.put(EncodeHintType.MARGIN, 0);
        BitMatrix bm = new MultiFormatWriter().encode(content, BarcodeFormat.QR_CODE, w, h, hnts);
        return MatrixToImageWriter.toBufferedImage(bm);
    }

    private static PDImageXObject toX(PDDocument d, BufferedImage i) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        javax.imageio.ImageIO.write(i, "png", os);
        return PDImageXObject.createFromByteArray(d, os.toByteArray(), "img");
    }
}