package cn.stars.reversal.command.impl;

import cn.stars.reversal.Reversal;
import cn.stars.reversal.command.Command;
import cn.stars.reversal.command.api.CommandInfo;
import cn.stars.reversal.ui.atmoic.msgbox.AtomicMsgBox;

@CommandInfo(name = "Test", description = "test", syntax = ".test <args...>", aliases = "test")
public class Test extends Command {
    @Override
    public void onCommand(String command, String[] args) throws Exception {
        if (Reversal.atomicMsgBox == null) {
            for (int i = 0; i < args.length; i++) {
                if (i==0) {
                    Reversal.atomicMsgBox = new AtomicMsgBox(args[i]);
                } else {
                    Reversal.atomicMsgBox.FACTORY.addLine(args[i]);
                }
            }
        }
    }
}
