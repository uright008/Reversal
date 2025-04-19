/*
 * Reversal Client - A PVP Client with hack visual.
 * Copyright 2025 Aerolite Society, Some rights reserved.
 */
package cn.stars.reversal.util.reversal.irc;

import cn.stars.reversal.Reversal;
import cn.stars.reversal.module.impl.client.IRC;
import cn.stars.reversal.util.misc.ModuleInstance;
import com.blogspot.debukkitsblog.net.Client;
import com.blogspot.debukkitsblog.net.Datapackage;
import com.blogspot.debukkitsblog.net.Executable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;

import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class IRCInstance extends Client {
    public boolean receivedMsg = false;
    public String id;
    int reconnectedTimes = 0;
    public Map<String, String> users = new HashMap<>();
    public ArrayList<String> onlinePlayers = new ArrayList<>();

    public IRCInstance(String host, int port, String username) {
        super(host, port, username);
        this.id = username;

        registerMethod("Message", new Executable() {
            @Override
            public void run(Datapackage msg, Socket socket) {
                if (ModuleInstance.getModule(IRC.class).isEnabled()) {
                    if (!receivedMsg) {
                        Minecraft.getMinecraft().addScheduledTask(() -> Reversal.showMsg(I18n.format("message.IRC.tip")));
                        receivedMsg = true;
                    }
                    Minecraft.getMinecraft().addScheduledTask(() -> Reversal.showMsg(msg.get(1)));
                }
            }
        });

        registerMethod("UserList", new Executable() {
            @Override
            public void run(Datapackage msg, Socket socket) {
                users = (Map<String, String>) msg.get(1);
            }
        });
        registerMethod("OnlineList", new Executable() {
            @Override
            public void run(Datapackage msg, Socket socket) {
                onlinePlayers = (ArrayList<String>) msg.get(1);
            }
        });

        start();
    }

    @Override
    protected void repairConnection() {
        if (reconnectedTimes < 30 && reconnectedTimes >= 0) {
        //    Minecraft.getMinecraft().addScheduledTask(() -> Reversal.showMsg("[IRC] Reconnecting! Times:" + reconnectedTimes));
            super.repairConnection();
            reconnectedTimes++;
        } else {
            Minecraft.getMinecraft().addScheduledTask(() -> Reversal.showMsg("[IRC] Failed to reconnect after 30 times of try!"));
            reconnectedTimes = -1;
            this.stop();
        }
    }

}