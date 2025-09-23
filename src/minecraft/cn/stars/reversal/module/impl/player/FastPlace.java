package cn.stars.reversal.module.impl.player;

import cn.stars.reversal.module.Category;
import cn.stars.reversal.module.Module;
import cn.stars.reversal.module.ModuleInfo;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

/**
 * FastPlace 模块
 * 
 * 该模块允许玩家在持有方块时快速放置，忽略默认的放置冷却时间。
 * 这在需要快速建造或放置多个方块时非常有用。
 */
@ModuleInfo(name = "FastPlace", localizedName = "module.FastPlace.name", description = "Ignore block placement cooldown", localizedDescription = "module.FastPlace.desc", category = Category.PLAYER)
public class FastPlace extends Module { }