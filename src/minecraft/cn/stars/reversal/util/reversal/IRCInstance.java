/*
 * Reversal Client - A PVP Client with hack visual.
 * Copyright 2025Aerolite Society, All rights reserved.
 */
package cn.stars.reversal.util.reversal;

import cn.stars.reversal.Reversal;
import cn.stars.reversal.module.impl.client.IRC;
import cn.stars.reversal.util.misc.ModuleInstance;
import com.blogspot.debukkitsblog.net.Client;
import com.blogspot.debukkitsblog.net.Datapackage;
import com.blogspot.debukkitsblog.net.Executable;

import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class IRCInstance extends Client {
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
                    Reversal.showMsg(msg.get(1));
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
        if (reconnectedTimes < 5) {
            Reversal.showMsg("[IRC] Reconnecting! Times:" + reconnectedTimes);
            super.repairConnection();
            reconnectedTimes++;
        } else {
            Reversal.showMsg("[IRC] Failed to reconnect after 5 times of try!");
            reconnectedTimes = 0;
            this.stop();
        }
    }

}