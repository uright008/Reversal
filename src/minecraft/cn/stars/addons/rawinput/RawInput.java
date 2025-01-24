package cn.stars.addons.rawinput;

import net.java.games.input.Controller;
import net.java.games.input.ControllerEnvironment;
import net.java.games.input.Mouse;
import net.minecraft.client.Minecraft;

public class RawInput {
    public static Mouse mouse;
    public static Controller[] controllers;
    public static float dx = 0;
    public static float dy = 0;

    public static void startRawInputThread() {
        controllers = ControllerEnvironment.getDefaultEnvironment().getControllers();
        Thread inputThread = new Thread(() -> {
            while (true) {
                int i = 0;
                while (i < controllers.length && mouse == null) {
                    if (controllers[i].getType() == Controller.Type.MOUSE) {
                        controllers[i].poll();
                        float px = ((Mouse) controllers[i]).getX().getPollData();
                        float py = ((Mouse) controllers[i]).getY().getPollData();
                        float eps = 0.1f;
                        if (Math.abs(px) > eps || Math.abs(py) > eps) {
                            mouse = (Mouse) controllers[i];
                        }
                    }
                    i++;
                }
                if (mouse != null) {
                    mouse.poll();
                    if (Minecraft.getMinecraft().currentScreen == null) {
                        dx += mouse.getX().getPollData();
                        dy += mouse.getY().getPollData();
                    }
                }

                try {
                    Thread.sleep(1L);
                } catch (InterruptedException ignored) {
                }
            }
        });
        inputThread.setName("Input Thread");
        inputThread.start();
    }
}
