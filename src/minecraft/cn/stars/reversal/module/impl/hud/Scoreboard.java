package cn.stars.reversal.module.impl.hud;

import cn.stars.reversal.module.Category;
import cn.stars.reversal.module.Module;
import cn.stars.reversal.module.ModuleInfo;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.util.EnumChatFormatting;

import java.util.Collection;
import java.util.List;

import static net.minecraft.client.gui.Gui.drawRect;

@ModuleInfo(name = "Scoreboard", chineseName = "记分板", description = "Show the scoreboard",
        chineseDescription = "显示计分板", category = Category.HUD)
public class Scoreboard extends Module {
    public Scoreboard() {
        setCanBeEdited(true);
        setX(100);
        setY(100);
    }

    public void renderScoreboard(ScoreObjective scoreObjective, ScaledResolution screenResolution)
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

        int maxStringWidth = mc.fontRendererObj.getStringWidth(scoreObjective.getDisplayName());

        for (Score score : scoreCollection)
        {
            ScorePlayerTeam playerTeam = scoreboardInstance.getPlayersTeam(score.getPlayerName());
            String playerScoreString = ScorePlayerTeam.formatPlayerName(playerTeam, score.getPlayerName()) + ": " + EnumChatFormatting.RED + score.getScorePoints();
            maxStringWidth = Math.max(maxStringWidth, mc.fontRendererObj.getStringWidth(playerScoreString));
        }

        int scoreboardHeight = scoreCollection.size() * mc.fontRendererObj.FONT_HEIGHT;
        int scoreboardStartY = getY() + scoreboardHeight + 8;
        int scoreboardStartX = getX() - getWidth() + 5;
        int horizontalMargin = 3;
        int currentLineIndex = 0;

        for (Score score1 : scoreCollection)
        {
            ++currentLineIndex;
            ScorePlayerTeam currentPlayerTeam = scoreboardInstance.getPlayersTeam(score1.getPlayerName());
            String playerNameString = ScorePlayerTeam.formatPlayerName(currentPlayerTeam, score1.getPlayerName());
            String playerScoreValueString = EnumChatFormatting.RED + "" + score1.getScorePoints();
            int currentLineY = scoreboardStartY - currentLineIndex * mc.fontRendererObj.FONT_HEIGHT;
            int scoreboardEndX = scoreboardStartX + maxStringWidth + horizontalMargin;
            drawRect(scoreboardStartX - 2, currentLineY, scoreboardEndX, currentLineY + mc.fontRendererObj.FONT_HEIGHT, 1342177280);
            mc.fontRendererObj.drawString(playerNameString, scoreboardStartX, currentLineY, 553648127);
            mc.fontRendererObj.drawString(playerScoreValueString, scoreboardEndX - mc.fontRendererObj.getStringWidth(playerScoreValueString), currentLineY, 553648127);

            if (currentLineIndex == scoreCollection.size())
            {
                String objectiveDisplayName = scoreObjective.getDisplayName();
                drawRect(scoreboardStartX - 2, currentLineY - mc.fontRendererObj.FONT_HEIGHT - 1, scoreboardEndX, currentLineY - 1, 1610612736);
                drawRect(scoreboardStartX - 2, currentLineY - 1, scoreboardEndX, currentLineY, 1342177280);
                mc.fontRendererObj.drawString(objectiveDisplayName, scoreboardStartX + maxStringWidth / 2 - mc.fontRendererObj.getStringWidth(objectiveDisplayName) / 2, currentLineY - mc.fontRendererObj.FONT_HEIGHT, 553648127);
            }
        }

        // Set the width and height of the scoreboard
        setWidth(maxStringWidth + horizontalMargin + 12);
        setAdditionalWidth(-getWidth());
        setHeight(scoreboardHeight + 15);
    }

}
