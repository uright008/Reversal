package cn.stars.reversal.module.impl.render;

import cn.stars.reversal.event.impl.PreMotionEvent;
import cn.stars.reversal.module.Category;
import cn.stars.reversal.module.Module;
import cn.stars.reversal.module.ModuleInfo;
import cn.stars.reversal.value.impl.BoolValue;
import cn.stars.reversal.value.impl.ModeValue;
import cn.stars.reversal.value.impl.NumberValue;
import net.minecraft.client.renderer.entity.RendererLivingEntity;

@ModuleInfo(name = "Animations", localizedName = "动画", description = "Display special swing animations.",
        localizedDescription = "修改你的挥手动画", category = Category.RENDER)
public class Animations extends Module {
    public final NumberValue itemX = new NumberValue("Item X", this, 0.0, -1, 1, 0.1);
    public final NumberValue itemY = new NumberValue("Item Y", this, 0.0, -1, 1, 0.1);
    public final NumberValue itemZ = new NumberValue("Item Z", this, 0.0, -1, 1, 0.1);
    public final NumberValue blockY = new NumberValue("Block Y", this, 0.1, -1, 1, 0.1);
    public final NumberValue itemScale = new NumberValue("Item Scale", this, 1.0, 0.1, 2, 0.1);
    public final NumberValue swingSpeed = new NumberValue("Swing Speed", this, 1.0, 1, 5, 1);
    public final BoolValue swordSwing = new BoolValue("Sword Swing", this, true);
    public final ModeValue swordMode = new ModeValue("Sword Mode", this, "1.7",
            "None" ,"1.7", "Spin", "Leaked", "Old", "Exhibition", "SlideDown", "Liquid");
    public final BoolValue foodSwing = new BoolValue("Food Swing", this, true);
    public final BoolValue drinkSwing = new BoolValue("Drink Swing", this, true);
    public final BoolValue bowSwing = new BoolValue("Bow Swing", this, true);
    public final BoolValue interactWhileSwing = new BoolValue("Interact While Swing", this, false);
    public final BoolValue swingAnim = new BoolValue("Swing Animation", this, false);
    public final BoolValue anythingBlock = new BoolValue("Anything Block", this, false);
    public final BoolValue oldSneak = new BoolValue("Old Sneak", this, false);
    public final BoolValue leftHand = new BoolValue("Left Hand", this, false);
    public final BoolValue customHitColor = new BoolValue("Custom Hit Color",this , false);

    public final NumberValue chcRed = new NumberValue("Hit Color Red", this, 255, 0, 255, 1);
    public final NumberValue chcGreen = new NumberValue("Hit Color Green", this, 0, 0, 255, 1);
    public final NumberValue chcBlue = new NumberValue("Hit Color Blue", this, 0, 0, 255, 1);
    public final NumberValue chcAlpha = new NumberValue("Hit Color Alpha", this, 100, 0, 255, 1);

    @Override
    public void onUpdateAlwaysInGui() {
        swordMode.hidden = !swordSwing.isEnabled();
        chcRed.hidden = !customHitColor.isEnabled();
        chcGreen.hidden = !customHitColor.isEnabled();
        chcBlue.hidden = !customHitColor.isEnabled();
        chcAlpha.hidden = !customHitColor.isEnabled();
    }

    @Override
    public void onUpdateAlways() {
        setSuffix(swordMode.getMode());
    }

    @Override
    public void onPreMotion(final PreMotionEvent event) {
        RendererLivingEntity.dmgRed = (float) chcRed.getValue() / 255;
        RendererLivingEntity.dmgGreen = (float) chcGreen.getValue() / 255;
        RendererLivingEntity.dmgBlue = (float) chcBlue.getValue() / 255;
        RendererLivingEntity.dmgAlpha = (float) chcAlpha.getValue() / 255;
    }

}
