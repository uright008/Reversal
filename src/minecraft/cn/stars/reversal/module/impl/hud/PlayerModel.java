package cn.stars.reversal.module.impl.hud;

import cn.stars.reversal.event.impl.Render2DEvent;
import cn.stars.reversal.module.Category;
import cn.stars.reversal.module.Module;
import cn.stars.reversal.module.ModuleInfo;
import cn.stars.reversal.util.render.RenderUtil;
import cn.stars.reversal.value.impl.ModeValue;
import cn.stars.reversal.value.impl.NumberValue;
import net.minecraft.client.gui.inventory.GuiInventory;

@ModuleInfo(name = "PlayerModel", localizedName = "module.PlayerModel.name", description = "Render a player on screen", localizedDescription = "module.PlayerModel.desc", category = Category.HUD)
public class PlayerModel extends Module {
    public final ModeValue rotateMode = new ModeValue("Rotate Mode", this, "Player", "Player", "Animated", "Custom");
    public final NumberValue customPitch = new NumberValue("Pitch", this, 0, -90, 90, 0.1);
    public final NumberValue customYaw = new NumberValue("Yaw", this, 0, -180, 180, 0.1);
    public final NumberValue scale = new NumberValue("Scale", this, 64, 16, 128, 4);

    private float rotate;
    private boolean rotateDirection;

    public PlayerModel() {
        setX(100);
        setY(100);
        setCanBeEdited(true);
    }

    @Override
    public void onRender2D(Render2DEvent event) {
        float delta = RenderUtil.deltaFrameTime;

        if (rotateDirection) {
            if (rotate <= 70F) {
                rotate += 0.3F * delta;
            } else {
                rotateDirection = false;
                rotate = 70F;
            }
        } else {
            if (rotate >= -70F) {
                rotate -= 0.3F * delta;
            } else {
                rotateDirection = true;
                rotate = -70F;
            }
        }

        float pitch, yaw;
        if (rotateMode.getMode().equals("Player")) {
            pitch = -mc.thePlayer.rotationPitch;
            yaw = mc.thePlayer.rotationYaw;
        } else if (rotateMode.getMode().equals("Animated")) {
            pitch = 0;
            yaw = rotate;
        } else {
            pitch = customPitch.getFloat();
            yaw = customYaw.getFloat();
        }

        GuiInventory.drawEntityOnScreen(getX() + 1, getY() + 1, scale.getInt(), yaw, pitch, mc.thePlayer);

        setWidth(scale.getInt() * 1.5f);
        setHeight(scale.getInt() * 2f + 10);
        setAdditionalWidth(-scale.getInt() * 0.75f);
        setAdditionalHeight(-scale.getInt() * 2f);
    }
}
