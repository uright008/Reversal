package cn.stars.reversal.util.reversal;

import cn.stars.reversal.util.math.TimeUtil;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class UserHandshakeThread extends Thread {
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    IRCInstance user;

    public UserHandshakeThread(IRCInstance user) {
        this.user = user;
    }

    @Override
    public void run() {
        scheduler.scheduleAtFixedRate(() -> {
            try {
                if (user != null && user.isConnected()) user.sendMessage("Handshake", user.id);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, 120, TimeUnit.SECONDS);
    }
}
