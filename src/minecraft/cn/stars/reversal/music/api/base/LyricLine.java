/*
 * Reversal Client - A PVP Client with hack visual.
 * Copyright 2025Aerolite Society, All rights reserved.
 */
package cn.stars.reversal.music.api.base;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class LyricLine {
    int start;
    int duration;
    List<LyricChar> chars;
    boolean match;

    public LyricLine(int charStartTime, int charDuration, List<LyricChar> chars, boolean match) {
        this.start = charStartTime;
        this.duration = charDuration;
        this.chars = chars;
        this.match = match;
    }

    public void reset() {
    //    this.start = 0;
    //    this.duration = 0;
    //    this.chars = null;
        this.match = false;
    }

    public String getLine() {
        StringBuilder result = new StringBuilder();
        for (LyricChar lyricChar : this.chars) {
            result.append(lyricChar.getCharacter());
        }
        return result.toString();
    }
}
