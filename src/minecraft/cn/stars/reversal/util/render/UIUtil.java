package cn.stars.reversal.util.render;

import cn.stars.reversal.GameInstance;
import cn.stars.reversal.ui.modern.TextButton;
import cn.stars.reversal.util.shader.RiseShaders;
import cn.stars.reversal.util.shader.base.ShaderRenderType;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.UtilityClass;

import static cn.stars.reversal.GameInstance.*;
import static cn.stars.reversal.GameInstance.UI_BLOOM_RUNNABLES;

@Getter
@Setter
@UtilityClass
public class UIUtil {
    public void renderButton(TextButton[] buttons, int mouseX, int mouseY, float partialTicks) {
        // blur
        RiseShaders.GAUSSIAN_BLUR_SHADER.update();
        RiseShaders.GAUSSIAN_BLUR_SHADER.run(ShaderRenderType.OVERLAY, partialTicks, NORMAL_BLUR_RUNNABLES);
        // bloom
        RiseShaders.POST_BLOOM_SHADER.update();
        RiseShaders.POST_BLOOM_SHADER.run(ShaderRenderType.OVERLAY, partialTicks, NORMAL_POST_BLOOM_RUNNABLES);

        GameInstance.clearRunnables();

        for (TextButton button : buttons) {
            button.draw(mouseX, mouseY, partialTicks);
        }

        UI_BLOOM_RUNNABLES.forEach(Runnable::run);
        UI_BLOOM_RUNNABLES.clear();
    }

    public void onButtonClick(TextButton[] buttons, int mouseX, int mouseY, int mouseButton) {
        if (mouseButton == 0) {
            for (TextButton menuButton : buttons) {
                if (RenderUtil.isHovered(menuButton.getX(), menuButton.getY(), menuButton.getWidth(), menuButton.getHeight(), mouseX, mouseY) && menuButton.enabled) {
                    mc.getSoundHandler().playUISound("click");
                    menuButton.runAction();
                    break;
                }
            }
        }
    }
}
