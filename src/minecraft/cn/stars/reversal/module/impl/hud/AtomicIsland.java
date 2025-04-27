package cn.stars.reversal.module.impl.hud;

import cn.stars.reversal.event.impl.*;
import cn.stars.reversal.module.Category;
import cn.stars.reversal.module.Module;
import cn.stars.reversal.module.ModuleInfo;
import cn.stars.reversal.ui.atmoic.island.Atomic;
import cn.stars.reversal.util.math.MathUtil;
import cn.stars.reversal.value.impl.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

import java.awt.*;

@ModuleInfo(name = "AtomicIsland", localizedName = "module.AtomicIsland.name", description = "Display an island-like bar", localizedDescription = "module.AtomicIsland.desc", category = Category.HUD)
public class AtomicIsland extends Module {
    public final NoteValue note1 = new NoteValue("< Settings >", this);
    public final ColorValue barColor = new ColorValue("Bar Color", this, new Color(255,255,255,255)).defaultThemeColorEnabled(false);
    public final ColorValue shadowColor = new ColorValue("Shadow Color", this, Color.BLACK).defaultThemeColorEnabled(false);
    public final NumberValue yOffset = new NumberValue("Y Offset", this, 0.0, -50.0, 50.0, 0.1);
    public final BoolValue percentBar = new BoolValue("Percent Bar", this, false);
    public final BoolValue allowRepeat = new BoolValue("Allow Repeat", this, false);
    public final ModeValue musicLyricsMode = new ModeValue("Music Lyrics Mode", this, "Origin", "Origin", "Translated", "Both");
    public final BoolValue runningLight = new BoolValue("Running Light", this, false);
    public final NoteValue note2 = new NoteValue("< Features >", this);
    public final BoolValue enemyInfo = new BoolValue("Enemy Info", this, true);
    public final BoolValue blockInfo = new BoolValue("Block Info", this, true);

    public AtomicIsland() {
        setCanBeEdited(false);
    }

    @Override
    public void onRender2D(Render2DEvent event) {
        Atomic.INSTANCE.render(event.getScaledResolution());
        // Prevent hiding other hud to move
        setWidth(0);
        setHeight(0);
    }

    @Override
    protected void onDisable() {
        Atomic.width = 0;
        Atomic.height = 0;
    }

    @Override
    public void onAttack(AttackEvent event) {
        Entity entity = event.getTarget();
        if (entity instanceof EntityPlayer && enemyInfo.enabled) {
            String hp;
            try {
                hp = "" + MathUtil.round(((EntityPlayer) entity).getHealth(), 1);
            } catch (NumberFormatException e) {
                hp = "NaN";
            }
            Atomic.registerAtomic("HP: " + hp + " | Food: " + ((EntityPlayer) entity).getFoodStats().getFoodLevel() + " | Distance: " + MathUtil.round(mc.thePlayer.getDistanceToEntity(entity), 1) + "m", entity.getName(), 5000, "a", false, -1);
        }
    }

    @Override
    public void onValueChanged(ValueChangedEvent event) {
        // 重置动画
        if (event.setting == yOffset) {
            Atomic.INSTANCE.y.setValue(Atomic.getRenderY(Atomic.y));
        }
    }

    @Override
    public void onClick(ClickEvent event) {
        if (event.getType() == ClickEvent.ClickType.RIGHT && blockInfo.enabled) {
            ItemStack item = mc.thePlayer.getHeldItem();
            if (item != null && item.getItem() instanceof ItemBlock) {
                int size = mc.thePlayer.getHeldItem().stackSize;
                Atomic.registerAtomic("Count: " + size + "  " + (size > 32 ? "" : size > 16 ? "(!)" : size > 8 ? "(!!)" : "(!!!)"), item.getDisplayName(), 3000, "c", false, -1);
            }
        }
    }
}
