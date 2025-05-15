package cn.stars.reversal.music.api.player;

import cn.stars.reversal.Reversal;
import cn.stars.reversal.module.impl.hud.MusicInfo;
import cn.stars.reversal.module.impl.hud.MusicVisualizer;
import cn.stars.reversal.music.api.base.LyricLine;
import cn.stars.reversal.music.api.base.Music;
import cn.stars.reversal.music.thread.ChangeMusicThread;
import cn.stars.reversal.ui.atmoic.island.Atomic;
import cn.stars.reversal.util.math.RandomUtil;
import cn.stars.reversal.util.misc.FileUtil;
import cn.stars.reversal.util.misc.ModuleInstance;
import com.github.kokorin.jaffree.ffmpeg.FFmpeg;
import com.github.kokorin.jaffree.ffmpeg.UrlInput;
import com.github.kokorin.jaffree.ffmpeg.UrlOutput;
import javafx.embed.swing.JFXPanel;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author ChengFeng
 * @since 2024/8/14
 **/
public class MusicPlayer {
    @Getter
    private Music music;
    @Getter
    private MediaPlayer mediaPlayer;
    @Getter
    @Setter
    private PlayMode playMode = PlayMode.ORDERED;
    @Getter
    private float[] magnitudes;

    private List<Music> currentMusicList;

    public static final ArrayList<Integer> randomPlayedIndex = new ArrayList<>();

    public MusicPlayer() {
        new JFXPanel();
    }

    public double getCurrentTime() {
        return mediaPlayer.getCurrentTime().toMillis();
    }

    public void setCurrentTime(double time) {
        mediaPlayer.seek(Duration.millis(time));
    //    MusicLyricWidget widget = (MusicLyricWidget) Client.instance.uiManager.getWidget(MusicLyricWidget.class);
    //    widget.release();
        music.getLyrics().forEach(LyricLine::reset);
        music.getTranslatedLyrics().forEach(LyricLine::reset);
    }

    public double getVolume() {
        return mediaPlayer.getVolume() * 100d;
    }

    public void setVolume(double volume) {
        mediaPlayer.setVolume(volume / 100d);
    }

    public void setMusic(Music music) {
        MusicVisualizer.magnitudeInterp = null;

        String songURL = music.getSongURL();
        Media media;

        if (songURL == null) {
            Reversal.threadPoolExecutor.execute(new ChangeMusicThread(music));
            return;
        }

        if (songURL.endsWith("flac")) {
            File song = FileUtil.getFileOrPath("Cache" + File.separator + music.getId() + ".flac");
            File converted = FileUtil.getFileOrPath("Cache" + File.separator + music.getId() + ".wav");
            if (!song.exists()) {
                FFmpeg ffmpeg = FFmpeg.atPath(new File("../ffmpeg/bin").toPath());

                // 构建转换命令
                ffmpeg.addInput(UrlInput.fromUrl(songURL))
                        .addOutput(UrlOutput.toPath(converted.toPath()))
                        .addArguments("-c:a", "pcm_s16le")
                        .addArguments("-threads", Runtime.getRuntime().availableProcessors() + "")
                        .execute();
            }

            media = new Media(converted.toURI().toString());
        } else {
            media = new Media(songURL);
        }

        double volume = mediaPlayer == null ? 1d : mediaPlayer.getVolume();
        if (mediaPlayer != null) mediaPlayer.stop();
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setOnEndOfMedia(() -> {
            switch (ModuleInstance.getModule(cn.stars.reversal.module.impl.addons.MusicPlayer.class).playMode.getMode()) {
                case "Next": {
                    this.next();
                    break;
                }
                case "Random": {
                    this.random();
                    break;
                }
                case "Repeat": {
                    mediaPlayer.seek(mediaPlayer.getStartTime());
                    mediaPlayer.play();
                }
            }
        });
        mediaPlayer.setAudioSpectrumInterval(0.025);
        mediaPlayer.setAudioSpectrumListener((timestamp, duration, magnitudes, phases) -> {
            this.magnitudes = magnitudes;
        });
        mediaPlayer.setVolume(volume);
        music.getLyrics().forEach(LyricLine::reset);
    //    ((MusicLyricWidget) Client.instance.uiManager.getWidget(MusicLyricWidget.class)).release();
        ModuleInstance.getModule(MusicInfo.class).reset();
        Atomic.INSTANCE.reset();

        mediaPlayer.play();

        this.music = music;
    }

    public int getPlayedLyricCount() {
        int count = 0;
        for (LyricLine lyric : music.getLyrics()) {
            if (getCurrentTime() > lyric.getStart() + lyric.getDuration()) count++;
        }
        return count;
    }

    public int getPlayedTranslateCount() {
        int count = 0;
        for (LyricLine lyric : music.getLyrics()) {
            if (getCurrentTime() > lyric.getStart() + lyric.getDuration() && music.getTranslateMap().containsKey(lyric)) count++;
        }
        return count;
    }

    public void play() {
        mediaPlayer.play();
    }

    public void pause() {
        mediaPlayer.pause();
    }

    public void previous() {
        int index = currentMusicList.indexOf(music) - 1;
        if (index < 0) index = currentMusicList.size() - 1;
        setMusic(currentMusicList.get(index));
    }

    public void next() {
        int index = currentMusicList.indexOf(music) + 1;
        if (index >= currentMusicList.size() - 1) index = 0;
        setMusic(currentMusicList.get(index));
    }

    public void random() {
        int result = RandomUtil.INSTANCE.nextInt(0, currentMusicList.size() - 1);
        if (randomPlayedIndex.contains(result)) {
            this.random();
        } else {
            randomPlayedIndex.add(result);
            setMusic(currentMusicList.get(result));
        }
    }

    public void setMusicList(List<Music> list) {
        currentMusicList = list;
        if (playMode == PlayMode.SHUFFLE) Collections.shuffle(currentMusicList);
    }

    public boolean isPaused() {
        return mediaPlayer.getStatus() == MediaPlayer.Status.PAUSED;
    }

    public String getCurrentLyric(boolean translated) {
        if (music == null) return "无音乐";
        // 歌词为空
        if (music.getLyrics().isEmpty()) {
            return "纯音乐，请欣赏";
        }

        // 获取歌词列表
        List<LyricLine> lyrics = (music.hasTranslate || !music.getTranslatedLyrics().isEmpty()) && translated ? music.getTranslatedLyrics() : music.getLyrics();
        double currentTime = getCurrentTime();

        // 遍历时只考虑当前时间已经跨越的歌词行
        if (getCurrentTime() < lyrics.get(0).getStart()) {
            return "...";
        }

        // 优化：使用二分查找来快速定位当前时间所在的歌词行
        int start = 0, end = lyrics.size() - 1;
        while (start <= end) {
            int mid = start + (end - start) / 2;
            LyricLine lyricLine = lyrics.get(mid);

            // 如果当前时间在该行歌词的时间范围内
            if (currentTime >= lyricLine.getStart() && (mid == lyrics.size() - 1 || currentTime < lyrics.get(mid + 1).getStart())) {
                return lyricLine.getLine();
            }
            // 如果当前时间小于该行歌词的开始时间，往前查找
            if (currentTime < lyricLine.getStart()) {
                end = mid - 1;
            } else {
                // 否则往后查找
                start = mid + 1;
            }
        }

        // 如果没有找到，返回默认的歌词（例如"..."）
        return "...";
    }
}
