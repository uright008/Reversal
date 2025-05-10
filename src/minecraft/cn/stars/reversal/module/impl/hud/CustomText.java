/*
 * Reversal Client - A PVP Client with hack visual.
 * Copyright 2025 Aerolite Society, Some rights reserved.
 */
package cn.stars.reversal.module.impl.hud;

import cn.stars.reversal.Reversal;
import cn.stars.reversal.event.impl.Render2DEvent;
import cn.stars.reversal.event.impl.Shader3DEvent;
import cn.stars.reversal.font.FontManager;
import cn.stars.reversal.module.Category;
import cn.stars.reversal.module.Module;
import cn.stars.reversal.module.ModuleInfo;
import cn.stars.reversal.value.impl.*;

import java.awt.*;

@ModuleInfo(name = "CustomText", localizedName = "module.CustomText.name", description = "Show a custom text on screen", localizedDescription = "module.CustomText.desc", category = Category.HUD)
public class CustomText extends Module {
    public final TextValue textValue = new TextValue("Text", this, "Example Custom Text;<r>Use the flag to return;");
    public final ColorValue colorValue = new ColorValue("Color", this);
    private final NumberValue size = new NumberValue("Size", this, 16, 4, 64, 1);
    private final BoolValue bloom = new BoolValue("Bloom", this, false);
    private final BoolValue bold = new BoolValue("Bold", this, false);
    private final BoolValue gradient = new BoolValue("Gradient", this, false);

    public CustomText() {
        setX(100);
        setY(100);
        setCanBeEdited(true);
    }

    @Override
    public void onRender2D(Render2DEvent event) {
        drawText();
        setWidthAndHeight();
    }

    @Override
    public void onShader3D(Shader3DEvent event) {
        if (bloom.enabled && event.isBloom()) drawText();
    }
    
    private String[] getAnalysedText() {
        String text = textValue.getText();
        return text.split("<r>");
    }

    private void drawText() {
        float x = getX() + 1;
        float y = getY() + 1;
        for (String s : this.getAnalysedText()) {
            if (bold.isEnabled()) {
                if (gradient.isEnabled()) {
                    float off = 0;
                    for (int i = 0; i < s.length(); i++) {
                        final Color c = colorValue.getColor(i);

                        final String character = String.valueOf(s.charAt(i));
                        FontManager.getRegularBold((int) size.getValue()).drawString(character, x + off, y, c.getRGB());
                        off += FontManager.getRegularBold((int) size.getValue()).width(character);
                    }
                } else {
                    final Color c = colorValue.getColor();
                    FontManager.getRegularBold((int) size.getValue()).drawString(s, x, y, c.getRGB());
                }
            } else {
                if (gradient.isEnabled()) {
                    float off = 0;
                    for (int i = 0; i < s.length(); i++) {
                        final Color c = colorValue.getColor(i);

                        final String character = String.valueOf(s.charAt(i));
                        FontManager.getRegularBold((int) size.getValue()).drawString(character, x + off, y, c.getRGB());
                        off += FontManager.getRegular((int) size.getValue()).width(character);
                    }
                } else {
                    final Color c = colorValue.getColor();
                    FontManager.getRegular((int) size.getValue()).drawString(s, x, y, c.getRGB());
                }
            }
            y += FontManager.getRegular((int)size.getValue()).height();
        }
    }

    private void setWidthAndHeight() {
        float maxWidth = 0;
        if (bold.isEnabled()) {
            for (String s : getAnalysedText()) {
                maxWidth = Math.max(maxWidth, FontManager.getRegularBold((int)size.getValue()).width(s) + 5);
            }
        } else {
            for (String s : getAnalysedText()) {
                maxWidth = Math.max(maxWidth, FontManager.getRegular((int)size.getValue()).width(s) + 5);
            }
        }
        setWidth(maxWidth);
        setHeight(FontManager.getRegular((int)size.getValue()).height() * getAnalysedText().length + 5);
    }

}
