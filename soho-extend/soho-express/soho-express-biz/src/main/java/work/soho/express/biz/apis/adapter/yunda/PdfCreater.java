package work.soho.express.biz.apis.adapter.yunda;

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

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.EnumMap;
import java.util.List;
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
            try (InputStream is = new FileInputStream("/home/fang/Documents/方正黑体简体.TTF")) {
                font = PDType0Font.load(doc, is);
            }

            // 获取数据
            String mailno = billPrintDTO.getPrintParam().getMailNo();
            String position = billPrintDTO.getPrintParam().getPrintBagaddr();      // 示例: 300A
            String positionNo = billPrintDTO.getPrintParam().getPrintMark(); // 示例: G262-00 F3
            String rName = billPrintDTO.getReceiver().getName();
            String rMobile = billPrintDTO.getReceiver().getMobile();
            String rAddr = billPrintDTO.getReceiver().getProv() + " "
                    + billPrintDTO.getReceiver().getCity() + " "
                    + billPrintDTO.getReceiver().getCounty() + " "
                    + billPrintDTO.getReceiver().getAddress();
            String packageWdjc = billPrintDTO.getPrintParam().getPrintBagaddr();

            try (PDPageContentStream cs = new PDPageContentStream(doc, page)) {

                // 底部边框打印运单号
                float partWidth = 76f/3;
                for (int i = 0; i < 3; i++) {
                    float startX = partWidth * i;
                    drawText(cs, font, 6f, mm2pt(startX + 5f), mm2pt(1f), "*" +mailno);
                }

                // 绘制右边 边缘运单号
                float partheight =  (130f - 8f)/5;
                for (int i = 0; i < 5; i++) {
                    float startY = partheight * i;
                    drawTextRotated(cs, font, 6f, mm2pt(68 + 5), mm2pt(130 - startY - 11f), "*" +mailno);
                }

                // 绘制左边边缘运单号
                for (int i = 0; i < 5; i++) {
                    float startY = partheight * i;
                    drawTextRotated270(cs, font, 6f, mm2pt(3), mm2pt(startY + 5f), "*" +mailno);
                }

                // 1. 整体框架与右侧纵栏 (W:14mm H:58mm 区域)
                float rightBarW = mm2pt(14.0f);
                float leftAreaW = PAGE_W - (2 * M) - rightBarW;
                float rightX = PAGE_W - M - rightBarW;

                // 最外围的大方框
//                drawRect(cs, M, M, PAGE_W - 2 * M, PAGE_H - 2 * M, 0.8f);

                // 2. 顶部：普快反色块 (W:16mm H:12mm 黑底白字)
                cs.setNonStrokingColor(Color.BLACK);
                cs.addRect(PAGE_W - mm2pt(25), PAGE_H - mm2pt(2) - mm2pt(12), mm2pt(25), mm2pt(12));
                cs.fill();
                cs.setNonStrokingColor(Color.WHITE);
//                drawText(cs, font, 13f, PAGE_W - mm2pt(16), PAGE_H - mm2pt(2) - mm2pt(9), "    普快");
//                drawText(cs, font, 13f, PAGE_W - mm2pt(16), PAGE_H - mm2pt(2) - mm2pt(7), "    普快");
//                drawText(cs, font, 18f, PAGE_W - mm2pt(22), PAGE_H - mm2pt(2) - mm2pt(8), "    普快");
//                drawText(cs, font, 18f, PAGE_W - mm2pt(22), PAGE_H - mm2pt(2) - mm2pt(8), text(label, "innerProvinceName"));
                drawText(cs, font, 18f, PAGE_W - mm2pt(22), PAGE_H - mm2pt(2) - mm2pt(8), "  标快");
                cs.setNonStrokingColor(Color.BLACK);

                // 顶部小字单号与时间
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime now = LocalDateTime.now();
                String formattedDateTime = now.format(formatter);
                drawText(cs, font, 8f, M + mm2pt(1), PAGE_H - M - mm2pt(4 + 4), formattedDateTime + " 第1/1个");
                drawText(cs, font, 6f, M + mm2pt(1), PAGE_H - M - mm2pt(4), "*" + mailno);
                drawText(cs, font, 6f, M + mm2pt(23), PAGE_H - M - mm2pt(4), "*" + mailno);


                // position上面的横线
                drawLine(cs, M, PAGE_H - mm2pt(14), PAGE_W - M, PAGE_H - mm2pt(14), 0.5f);

                // Position 区域 (H:10mm 黑体 26pt 加粗)
                float posTop = PAGE_H - mm2pt(14);
//                drawText(cs, font, 23f, M, posTop - mm2pt(8), position);
//                drawText(cs, font, 24f, M + mm2pt(22), posTop - mm2pt(8), positionNo);
                drawText(cs, font, 24f, M + mm2pt(1f), posTop - mm2pt(8), positionNo);
                drawLine(cs, M, posTop - mm2pt(10), rightX, posTop - mm2pt(10), 0.5f);

                // 处理中框绘制
                drawMiddleBox(doc, cs, 4f, 48f, 68f, 58, 0.8f, mailno, font,
                        packageWdjc, rName, rMobile, rAddr,
                        billPrintDTO
                        );


                // TOOD 获取商品信息 获取商品信息
                List<PrintInfoDTO.Goods> goods = billPrintDTO.getGoods();
                String goodsInfo = "物品信息：";
                for (int i = 0; i < goods.size(); i++) {
                    goodsInfo += "物品" + (i + 1) + "：" + goods.get(i).getName()
                            + " 数量：" + goods.get(i).getQty()
//                            + " 重量：" + goods.get(i).getWeight()
                            +  " 备注：" + goods.get(i).getRemark()
                            + "\n";
                }
                //  下面空白部分绘制
                drawMultiline(cs, font, 8f, M, mm2pt(44 - 2),
                        ""
//                        + text(label, "cus_area1")
//                        + "\n" + text(label, "item")
                        + "\n" + goodsInfo
                        + "\n" + (billPrintDTO.getPrintParam().getRemark() == null ? "" : "备注：" + billPrintDTO.getPrintParam().getRemark())
                        ,

                        mm2pt(58), 9);
            }

            doc.save(baos);
            return baos.toByteArray();
        }
    }

    // 画中框
    private static void drawMiddleBox(PDDocument doc, PDPageContentStream cs, float x, float y, float w, float h,
                                      float bw,
                                      String mailno,
                                      PDType0Font font,
                                      String packageWdjc,
                                      String rName,
                                      String rMobile,
                                      String rAddr,
                                      PrintInfoDTO billPrintDTO
                                      ) throws Exception {
        drawRect(cs,  mm2pt(x), mm2pt(y), mm2pt(54), mm2pt(58), bw);
        drawRect(cs,  mm2pt(x), mm2pt(y), mm2pt(54 + 14), mm2pt(58), bw);

        float bh = 0f;

        // 寄件人信息
        drawCircleIcon(cs, font, mm2pt(x + 1), mm2pt(y + bh + 3f), "寄");
        drawText(cs, font, 8f, mm2pt(x + 8), mm2pt(y + bh + 7), billPrintDTO.getSender().getName() + " " + billPrintDTO.getSender().getMobile());
        drawMultiline(cs, font, 8f, mm2pt(x + 8), mm2pt(y + bh + 4), billPrintDTO.getSender().getProv()
                + " " + billPrintDTO.getSender().getCity()
                + " " + billPrintDTO.getSender().getCounty()
                + " " + billPrintDTO.getSender().getAddress()
                , mm2pt(43), 3);


        bh += 10f;
        drawLine(cs, mm2pt(x), mm2pt(y + bh), mm2pt(x + 54), mm2pt(y+bh), bw);
        // 寄件
        drawCircleIcon(cs, font, mm2pt(x+1), mm2pt(y + bh + 16) - 11f, "收");
        drawText(cs, font, 11f, mm2pt(x + 8), mm2pt(y + bh +  15), rName + " " + rMobile);
        drawMultiline(cs, font, 11f, mm2pt(x + 8), mm2pt(y + bh + 10), rAddr, mm2pt(43), 3);

        bh += 20f;
        drawLine(cs, mm2pt(x), mm2pt(y + bh), mm2pt(x + 54), mm2pt(y+bh), bw);

        // 交
        drawRectIcon(cs, font, mm2pt(x), mm2pt(y + bh), "交", packageWdjc);
//        drawText(cs, font, 12f, M + mm2pt(8), jiaoTop - mm2pt(4.5f), packageWdjc);

        bh += 6f;
        drawLine(cs, mm2pt(x), mm2pt(y + bh), mm2pt(x + 54), mm2pt(y+bh), bw);


        bh += 6f;
        drawLine(cs, mm2pt(x), mm2pt(y + bh), mm2pt(x + 54), mm2pt(y+bh), bw);
        // 条码绘制
        // 4. 横向条码区 (W:54mm H:16mm)
        PDImageXObject hBarX = toX(doc, code128(mailno, 650, 200));
//        float barTop = posTop - mm2pt(10);
        cs.drawImage(hBarX, mm2pt(x + 2), mm2pt(y + bh + 5), mm2pt(50), mm2pt(8));
        drawText(cs, font, 8f, mm2pt(x + 14 ), mm2pt(y + bh + 2), mailno, true);
//        drawLine(cs, M, barTop - mm2pt(20), rightX, barTop - mm2pt(20), 0.5f);


        // 绘制右侧 条码
        PDImageXObject vBarX = toX(doc, code128(mailno, 800, 150));
        drawRotatedImage90(cs, vBarX, mm2pt(x + 54 + 1 + (14f/2f)), mm2pt(y + 58f/2f), mm2pt(54), mm2pt(8));

        drawTextRotated(cs, font, 8f, mm2pt(x + 54 + 1), mm2pt(y + 58 - 15), mailno);
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