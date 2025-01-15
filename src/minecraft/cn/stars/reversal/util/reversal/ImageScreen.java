package cn.stars.reversal.util.reversal;

import cn.stars.reversal.util.MiscUtil;
import cn.stars.reversal.util.ReversalLogger;

import javax.swing.*;
import java.awt.*;

public class ImageScreen {
    public static void load() {
        Thread thread = new Thread(() ->{
            JWindow window = new JWindow();
            window.setSize(1023,576);

            JLabel label = new JLabel();

            // image
            try {
                ImageIcon imageIcon = new ImageIcon(MiscUtil.inputStreamToByteArray(ImageScreen.class.getResourceAsStream("/assets/minecraft/reversal/images/imagescreen.jpg")));
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

            ReversalLogger.info("[ImageScreen] Loaded.");
            // wait some sec
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            window.dispose();
            ReversalLogger.info("[ImageScreen] Unloaded.");
        });
        thread.start();

    }
}