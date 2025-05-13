package cn.stars.reversal.music.ui.component.impl;

import cn.stars.reversal.music.ui.MusicCategory;
import cn.stars.reversal.music.ui.ThemeColor;
import cn.stars.reversal.music.ui.component.Button;
import cn.stars.reversal.music.ui.gui.MusicPlayerGUI;
import cn.stars.reversal.util.render.RenderUtil;
import cn.stars.reversal.util.render.RoundedUtil;
import lombok.Getter;
import lombok.Setter;

import java.awt.*;

/**
 * @author ChengFeng
 * @since 2024/8/13
 **/
@Getter
@Setter
public class CategoryButton extends Button {
    private final String text;
    private final MusicCategory category;
    private final MusicPlayerGUI gui;
    private boolean selected = false;

    public CategoryButton(String text, MusicCategory category, MusicPlayerGUI gui) {
        this.text = text;
        this.category = category;
        this.gui = gui;
    }

    @Override
    public void draw() {
        width = 80;
        height = 16;
        float textY = posY + height / 2f - 2f;
        RoundedUtil.drawRound(posX, posY, width, height, 3f, true, selected ? ThemeColor.redColor : (hovering ? new Color(50, 50, 50, 150) : new Color(0,0,0,0)));
        RenderUtil.image(category.icon, posX + 3f, textY - 4f, 12, 12);
        regular18.drawString(text, posX + 20f, textY, Color.WHITE.getRGB());
    }

    @Override
    public void mouseClicked(int mouseX, int mouseY, int button) {
        boolean hovering = RenderUtil.isHovered(posX, posY + height / 4f, width, height, mouseX, mouseY);
        if (hovering && button == 0) {
            selected = true;
        }
        if (!hovering) {
            selected = false;
        }
    }
}
