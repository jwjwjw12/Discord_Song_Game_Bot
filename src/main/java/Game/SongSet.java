package Game;

import Data.LoadSongs;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;

import java.util.ArrayList;
import java.util.HashSet;

public class SongSet {
    /*
        게임에 포함될 문제 Set, 게임 문제 수
        문제 set 1 : 00 년대
        문제 set 2 : 10 년대 초반
        문제 set 3 : 10 년대 후반
     */
    private int numOfSong;
    private HashSet<Integer> years;

    public SongSet(int numOfSong) {
        this.numOfSong = numOfSong;
        years = new HashSet<>();
    }

    public void setNumOfSong(int numOfSong) {
        this.numOfSong = numOfSong;
    }
    public int getNumOfSong() {
        return numOfSong;
    }
    public HashSet<Integer> getYears() {
        return years;
    }

    public void setYears(Integer i, ButtonClickEvent event){

        String s = "";
        if(i == 1)
            s = "2000년대";
        else if(i == 2)
            s = "2010년대 초반";
        else if(i == 3)
            s = "2010년대 후반";

        if(years.contains(i)) {
            years.remove(i);
            event.reply(s + " 문제가 제거되었습니다.").queue();
        }
        else {
            years.add(i);
            event.reply(s + " 문제가 추가되었습니다.").queue();
        }
    }

    public ArrayList<Song> getSongList(){
        LoadSongs load = new LoadSongs();
        return load.songsFromDB(years, numOfSong);
    }
}
