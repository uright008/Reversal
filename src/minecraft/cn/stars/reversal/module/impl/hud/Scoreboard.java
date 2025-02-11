package cn.stars.reversal.module.impl.hud;

import cn.stars.reversal.event.impl.Render2DEvent;
import cn.stars.reversal.module.Category;
import cn.stars.reversal.module.Module;
import cn.stars.reversal.module.ModuleInfo;
import cn.stars.reversal.util.render.RenderUtil;
import cn.stars.reversal.util.render.RoundedUtil;
import cn.stars.reversal.value.impl.BoolValue;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.EnumChatFormatting;

import java.awt.*;
import java.util.Collection;
import java.util.List;

import static net.minecraft.client.gui.Gui.drawRect;

@ModuleInfo(name = "Scoreboard", chineseName = "记分板", description = "Show the scoreboard",
        chineseDescription = "显示计分板", category = Category.HUD)
public class Scoreboard extends Module {
    public final BoolValue background = new BoolValue("Background", this, true);
    public final BoolValue rounded = new BoolValue("Rounded", this, false);
    public final BoolValue postProcessing = new BoolValue("Post Processing", this, false);
    public final BoolValue modernFont = new BoolValue("Modern Font", this, false);
    public Scoreboard() {
        setCanBeEdited(true);
        setX(100);
        setY(100);
    }

    public void renderScoreboard(ScoreObjective scoreObjective)
    {
        net.minecraft.scoreboard.Scoreboard scoreboardInstance = scoreObjective.getScoreboard();
        Collection<Score> scoreCollection = scoreboardInstance.getSortedScores(scoreObjective);
        List<Score> filteredScores = Lists.newArrayList(Iterables.filter(scoreCollection, p_apply_1_ -> p_apply_1_.getPlayerName() != null && !p_apply_1_.getPlayerName().startsWith("#")));

        if (filteredScores.size() > 15)
        {
            scoreCollection = Lists.newArrayList(Iterables.skip(filteredScores, scoreCollection.size() - 15));
        }
        else
        {
            scoreCollection = filteredScores;
        }

        int maxStringWidth = fontWidth(scoreObjective.getDisplayName());

        for (Score score : scoreCollection)
        {
            ScorePlayerTeam playerTeam = scoreboardInstance.getPlayersTeam(score.getPlayerName());
            String playerScoreString = ScorePlayerTeam.formatPlayerName(playerTeam, score.getPlayerName()) + ": " + EnumChatFormatting.RED + score.getScorePoints();
            maxStringWidth = Math.max(maxStringWidth, fontWidth(playerScoreString));
        }

        if (modernFont.enabled) maxStringWidth /= 2;

        float scoreboardHeight = scoreCollection.size() * fontHeight();
        float scoreboardStartY = getY() + scoreboardHeight + 8;
        int scoreboardStartX = getX() - getWidth() + 5;
        int horizontalMargin = 3;
        int currentLineIndex = 0;
        int finalMaxStringWidth = maxStringWidth;
        int extraWidth = modernFont.enabled ? 8 : 0;

        if (background.enabled) {
            if (rounded.enabled) {
                NORMAL_RENDER_RUNNABLES.add(() -> {
                    RenderUtil.roundedRectangle(scoreboardStartX, scoreboardStartY - fontHeight() - scoreboardHeight, finalMaxStringWidth + horizontalMargin, fontHeight() + scoreboardHeight, 4, new Color(0, 0, 0, 80));
                });
                if (postProcessing.enabled) {
                    MODERN_BLOOM_RUNNABLES.add(() -> RenderUtil.roundedRectangle(scoreboardStartX, scoreboardStartY - fontHeight() - scoreboardHeight, finalMaxStringWidth + horizontalMargin, fontHeight() + scoreboardHeight, 4, Color.BLACK));
                    MODERN_BLUR_RUNNABLES.add(() -> RenderUtil.roundedRectangle(scoreboardStartX, scoreboardStartY - fontHeight() - scoreboardHeight, finalMaxStringWidth + horizontalMargin, fontHeight() + scoreboardHeight, 4, Color.BLACK));
                }
            } else {
                NORMAL_RENDER_RUNNABLES.add(() -> {
                    RenderUtil.rect(scoreboardStartX, scoreboardStartY - fontHeight() - scoreboardHeight, finalMaxStringWidth + horizontalMargin, fontHeight(), new Color(0, 0, 0, 20));
                    RenderUtil.rect(scoreboardStartX, scoreboardStartY - fontHeight() - scoreboardHeight, finalMaxStringWidth + horizontalMargin, fontHeight() + scoreboardHeight, new Color(0, 0, 0, 80));
                });
                if (postProcessing.enabled) {
                    MODERN_BLOOM_RUNNABLES.add(() -> RenderUtil.rect(scoreboardStartX, scoreboardStartY - fontHeight() - scoreboardHeight, finalMaxStringWidth + horizontalMargin, fontHeight() + scoreboardHeight, Color.BLACK));
                    MODERN_BLUR_RUNNABLES.add(() -> RenderUtil.rect(scoreboardStartX, scoreboardStartY - fontHeight() - scoreboardHeight, finalMaxStringWidth + horizontalMargin, fontHeight() + scoreboardHeight, Color.BLACK));
                }
            }
        }

        for (Score score1 : scoreCollection)
        {
            ++currentLineIndex;
            ScorePlayerTeam currentPlayerTeam = scoreboardInstance.getPlayersTeam(score1.getPlayerName());
            String playerNameString = ScorePlayerTeam.formatPlayerName(currentPlayerTeam, score1.getPlayerName());
            String playerScoreValueString = EnumChatFormatting.RED + "" + score1.getScorePoints();
            float currentLineY = scoreboardStartY - currentLineIndex * fontHeight();
            float scoreboardEndX = scoreboardStartX + maxStringWidth + horizontalMargin;

            NORMAL_RENDER_RUNNABLES.add(() -> {
                drawString(playerNameString, scoreboardStartX, currentLineY, Color.WHITE.getRGB());
                drawString(playerScoreValueString, scoreboardEndX - fontWidth(playerScoreValueString) + extraWidth, currentLineY, new Color(255,80,80, 255).getRGB());
            });


            if (currentLineIndex == scoreCollection.size())
            {
                String objectiveDisplayName = scoreObjective.getDisplayName();
                NORMAL_RENDER_RUNNABLES.add(() -> {
                    drawString(objectiveDisplayName, scoreboardStartX + finalMaxStringWidth / 2f - fontWidth(objectiveDisplayName) / 2f + extraWidth, currentLineY - fontHeight(), Color.WHITE.getRGB());
                });
            }

        }

        // Set the width and height of the scoreboard
        setWidth(maxStringWidth + horizontalMargin + 12);
        setAdditionalWidth(-getWidth());
        setHeight((int) (scoreboardHeight + 15));
    }

    public float fontHeight() {
        if (modernFont.enabled) return psm18.height() - 2;
        else return mc.fontRendererObj.FONT_HEIGHT;
    }
    
    public float drawString(String string, float x, float y, int color) {
        if (modernFont.enabled) return psm18.drawString(string, x, y, color);
        else return mc.fontRendererObj.drawString(string, (int) x, (int) y, color);
    }
    
    public int fontWidth(String string) {
        if (modernFont.enabled) return psm18.width(string);
        else return mc.fontRendererObj.getStringWidth(string);
    }

}
