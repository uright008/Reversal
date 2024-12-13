package cn.stars.reversal.util.reversal;

import cn.stars.reversal.util.MiscUtil;

import javax.swing.*;
import java.awt.*;

public class ImageWindow {
    public static void load() {
        Thread thread = new Thread(() ->{
            JWindow window = new JWindow();
            window.setSize(1023,576);

            JLabel label = new JLabel();

            // image
            try {
                ImageIcon imageIcon = new ImageIcon(MiscUtil.inputStreamToByteArray(ImageWindow.class.getResourceAsStream("/assets/minecraft/reversal/images/imagescreen.jpg"))); // 这里替换为你图片的路径
                label.setIcon(imageIcon);
            } catch (Exception e) {
                e.printStackTrace();
            }

            window.getContentPane().add(label);

            // middle
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            int x = (screenSize.width - window.getSize().width) / 2;
            int y = (screenSize.height - window.getSize().height) / 2;
            window.setLocation(x, y);
            window.setVisible(true);

            // wait some sec
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            window.dispose();
        });
        thread.start();

    }
}