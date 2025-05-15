package cn.stars.reversal.music.thread;

import cn.stars.reversal.Reversal;
import cn.stars.reversal.music.api.MusicAPI;
import cn.stars.reversal.music.api.base.PlayList;
import cn.stars.reversal.music.ui.gui.impl.PlayListGUI;
import cn.stars.reversal.music.ui.gui.impl.PlayListListGUI;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * @author ChengFeng
 * @since 2024/8/13
 **/
public class GetPlayListsThread extends Thread {
    public GetPlayListsThread() {
        setName("Music-GetPlayLists");
    }

    @Override
    public void run() {
        PlayListListGUI gui = (PlayListListGUI) Reversal.musicManager.screen.categoryButtons.get(0).getGui();

        List<PlayList> userPlayLists = MusicAPI.getUserPlayLists();
        List<PlayList> recommendedPlayLists = MusicAPI.getRecommendedPlayLists();
        PlayList dailySongs = MusicAPI.getDailySongs();

        PlayList toRemove = null;
        for (PlayList playList : userPlayLists) {
            if (playList.getName().equalsIgnoreCase(MusicAPI.user.getNickname() + "喜欢的音乐") || playList.getName().equalsIgnoreCase("我喜欢的音乐") || playList.getName().contains("我喜欢的音乐")) {
                PlayListGUI gui1 = (PlayListGUI) Reversal.musicManager.screen.categoryButtons.get(1).getGui();
                gui1.setPlayList(playList);
                Reversal.threadPoolExecutor.execute(new FetchPlayListThread(playList, gui1.buttons));
                toRemove = playList;
            }
        }
        if (toRemove != null) userPlayLists.remove(toRemove);


        List<PlayList> playLists = new ArrayList<>(userPlayLists);
        playLists.add(dailySongs);
        playLists.addAll(recommendedPlayLists);

        HashSet<PlayList> playLists1 = new HashSet<>(playLists);
        new ArrayList<>(playLists1).forEach(gui::addPlayList);
        MusicAPI.user.setLoaded(true);
    }
}
