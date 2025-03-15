package cn.stars.reversal.module.impl.client;

import cn.stars.reversal.module.Category;
import cn.stars.reversal.module.Module;
import cn.stars.reversal.module.ModuleInfo;
import cn.stars.reversal.value.impl.BoolValue;
import cn.stars.reversal.value.impl.NoteValue;
import cn.stars.reversal.value.impl.NumberValue;

@ModuleInfo(name = "Interface", localizedName = "module.Interface.name", description = "Edit Minecraft interfaces", localizedDescription = "module.Interface.desc", category = Category.CLIENT)
public class Interface extends Module {
    public final NoteValue note1 = new NoteValue("< Title and Subtitle >", this);
    public final BoolValue modernFont_T_S = new BoolValue("Modern Font (T&S)", this, false);
    public final NumberValue yOffset = new NumberValue("Y Offset", this, 0f, -300f, 300f, 1f);

    public final NoteValue note2 = new NoteValue("< Chat >", this);
    public final BoolValue modernFont_Chat = new BoolValue("Modern Font (Chat)", this, false);
    public final BoolValue chatBackground = new BoolValue("Background", this, true);
    public final BoolValue combineDuplicatedMsg = new BoolValue("Combine Duplicated Messages", this, false);
}
