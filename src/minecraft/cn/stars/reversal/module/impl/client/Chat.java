package cn.stars.reversal.module.impl.client;

import cn.stars.reversal.module.Category;
import cn.stars.reversal.module.Module;
import cn.stars.reversal.module.ModuleInfo;
import cn.stars.reversal.value.impl.BoolValue;

@ModuleInfo(name = "Chat", localizedName = "聊天", description = "Edit chat options", localizedDescription = "修改聊天选项", category = Category.CLIENT)
public class Chat extends Module {
    public final BoolValue chatBackground = new BoolValue("Chat Background", this, true);
    public final BoolValue combineRepeatedMsg = new BoolValue("Combine Repeated Messages", this, false);

    @Override
    public void onUpdateAlways() {
        if (this.enabled) this.enabled = false;
    }
}
