package cn.stars.addons.dglab.qr;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import cn.stars.addons.dglab.DglabClient;
import cn.stars.addons.dglab.config.MainConfig;
import cn.stars.reversal.Reversal;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import net.minecraft.client.Minecraft;

import javax.imageio.ImageIO;


public class ToolQR {
    private ToolQR() {
    }

    public static void createQR() {
        MainConfig modConfig = DglabClient.getMainConfig();
        String ipAddress = modConfig.getAddress();
        if(ipAddress.equals("error")) {
            Reversal.showMsg("没有指定的ip地址");
        }
        else {
            int port = modConfig.getPort();
            StringBuilder url = new StringBuilder("https://www.dungeon-lab.com/app-download.php#DGLAB-SOCKET#ws://").append(ipAddress).append(':').append(port).append("/1234-123456789-12345-12345-01");
            try {
                Map<EncodeHintType, Object> hints = new HashMap<>();
                hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.H);
                hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");

                BitMatrix bitMatrix = new MultiFormatWriter().encode(url.toString(), BarcodeFormat.QR_CODE, 300, 300, hints);

                BufferedImage image = new BufferedImage(300, 300, BufferedImage.TYPE_INT_RGB);
                for (int x = 0; x < 300; x++) {
                    for (int y = 0; y < 300; y++) {
                        image.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
                    }
                }

                File qrCodeFile = new File(Minecraft.getMinecraft().mcDataDir, "Reversal/Misc/Dglab/QR.png");
                ImageIO.write(image, "png", qrCodeFile);


                ProcessBuilder pb = new ProcessBuilder("cmd", "/c", "start", "", "\"" + qrCodeFile.getAbsolutePath() + "\"");
                pb.start();


            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }
}
