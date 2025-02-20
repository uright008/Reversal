package net.minecraft.world.storage;

import cn.stars.reversal.util.animation.rise.Animation;
import cn.stars.reversal.util.animation.rise.Easing;
import lombok.Getter;
import net.minecraft.world.WorldSettings;

@Getter
public class SaveFormatComparator implements Comparable<SaveFormatComparator>
{
    private final String fileName;
    private final String displayName;
    private final long lastTimePlayed;
    private final long sizeOnDisk;
    private final boolean requiresConversion;
    private final WorldSettings.GameType theEnumGameType;
    private final boolean hardcore;
    private final boolean cheatsEnabled;
    private final Animation hoverAnimation = new Animation(Easing.EASE_OUT_EXPO, 1000);
    private final Animation selectAnimation = new Animation(Easing.EASE_OUT_EXPO, 1000);

    public SaveFormatComparator(String fileNameIn, String displayNameIn, long lastTimePlayedIn, long sizeOnDiskIn, WorldSettings.GameType theEnumGameTypeIn, boolean requiresConversionIn, boolean hardcoreIn, boolean cheatsEnabledIn)
    {
        this.fileName = fileNameIn;
        this.displayName = displayNameIn;
        this.lastTimePlayed = lastTimePlayedIn;
        this.sizeOnDisk = sizeOnDiskIn;
        this.theEnumGameType = theEnumGameTypeIn;
        this.requiresConversion = requiresConversionIn;
        this.hardcore = hardcoreIn;
        this.cheatsEnabled = cheatsEnabledIn;
    }

    public boolean requiresConversion()
    {
        return this.requiresConversion;
    }

    public int compareTo(SaveFormatComparator p_compareTo_1_)
    {
        return this.lastTimePlayed < p_compareTo_1_.lastTimePlayed ? 1 : (this.lastTimePlayed > p_compareTo_1_.lastTimePlayed ? -1 : this.fileName.compareTo(p_compareTo_1_.fileName));
    }

    public WorldSettings.GameType getEnumGameType()
    {
        return this.theEnumGameType;
    }

    public boolean isHardcoreModeEnabled()
    {
        return this.hardcore;
    }

    public boolean getCheatsEnabled()
    {
        return this.cheatsEnabled;
    }
}
