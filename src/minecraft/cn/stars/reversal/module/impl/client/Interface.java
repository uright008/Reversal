package cn.stars.reversal.module.impl.client;

import cn.stars.reversal.module.Category;
import cn.stars.reversal.module.Module;
import cn.stars.reversal.module.ModuleInfo;
import cn.stars.reversal.value.impl.BoolValue;
import cn.stars.reversal.value.impl.NoteValue;
import cn.stars.reversal.value.impl.NumberValue;

@ModuleInfo(name = "Interface", localizedName = "module.Interface.name", description = "Edit Minecraft interfaces", localizedDescription = "module.Interface.desc", category = Category.CLIENT)
public class Interface extends Module {
    public final NoteValue note1 = new NoteValue("< Title and Subtitle >", "value.Interface.note1", this);
    public final BoolValue modernFont_T_S = new BoolValue("Modern Font (T&S)", "value.Interface.modernFont_T_S", this, false);
    public final NumberValue yOffset = new NumberValue("Y Offset", "value.Interface.yOffset",this, 0f, -300f, 300f, 1f);

    public final NoteValue note2 = new NoteValue("< Chat >", "value.Interface.note2",this);
    public final BoolValue modernFont_Chat = new BoolValue("Modern Font (Chat)", "value.Interface.modernFont_Chat",this, false);
    public final BoolValue chatBackground = new BoolValue("Background", "value.Interface.chatBackground",this, true);
    public final BoolValue combineDuplicatedMsg = new BoolValue("Combine Duplicated Messages", "value.Interface.combineDuplicatedMsg",this, false);

    public final NoteValue note3 = new NoteValue("< Inventory >", this);
    public final BoolValue moveWhenPotionActive = new BoolValue("Move when potion active", this, true);
    public final BoolValue guiBackground = new BoolValue("Background (GUI)", this, true);

    @Override
    public void onUpdateAlways() {
        checkClientModuleState();
    }
}
