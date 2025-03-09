/*
 * Reversal Client - A PVP Client with hack visual.
 * Copyright 2025 Aerolite Society, Some rights reserved.
 */
package cn.stars.reversal.music.api.base;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class LyricPair {
    public List<LyricLine> lyrics;
    public List<LyricLine> translatedLyrics;

    public LyricPair(List<LyricLine> lyricLines, List<LyricLine> translatedLyricLines) {
        this.lyrics = lyricLines;
        this.translatedLyrics = translatedLyricLines;
    }
}
