package Data;

import Game.Song;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

public class SaveSongDataByPlayList {
    SaveSongData data = new SaveSongData();

    /*
    public static void main(String[] args) {
        SaveSongDataByPlayList save = new SaveSongDataByPlayList();

        ArrayList<Song> songs = save.getMelonSongs();

        save.data.songsToDB(songs);
    }
     */

    public ArrayList<Song> getMelonSongs() {
        String webUrl = "https://www.melon.com/mymusic/playlist/mymusicplaylistview_listSong.htm?plylstSeq=503564761";
        Document doc = null;
        ArrayList<Song> songs = new ArrayList<>();

        try {
            doc = Jsoup.connect(webUrl).get();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Elements elements = doc.select("button.like");

        for(Element element : elements){
            String id = element.attr("data-song-no");

            Song song = data.getMelonSongInfo(id);
            if(song != null)
                songs.add(song);
        }

        return songs;
    }
}
