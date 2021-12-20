package Game;

import Embed.GameEmbed;
import Game.Audio.GameAudioPlayer;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;

import java.util.*;

public class Game {
    private boolean isEnd;                     // 현재 게임이 진행중인지를 나타내는 변수
    private boolean isStart;                    // 게임 시작전에만 사용자들의 참가, 취소를 받기위함
    private boolean hintOn = true;              // 게임에 힌트 유무 default = true
    private User gameManager;                   // 해당 게임의 매니저에 해당하는 User 객체
    private TextChannel textChannel;            // 해당 게임이 진행되는 텍스트 채널
    private HashMap<User, Integer> gamers;      // 해당 게임의 참가자들, 각 점수
    private ArrayList<Song> songs;              // 해당 게임에 진행될 노래들의 모음
    private Song song;                          // 현재 문제에 해당하는 노래
    private HashSet<User> skip;                 // 모든 유저가 동의 하면 스킵
    private SongSet songSet;
    private CommandHandler handler;             // 명령어를 처리해주는 핸들러
    private GameEmbed embed;                    // 임베드 메시지 출력을 위한 객채
    private GameAudioPlayer audioPlayer;        // 오디오 컨트롤러
    private int count = 0;

    // 생성자, 게임매니저와 게임이 진행될 채널을 설정해줌
    public Game(User gameManager, TextChannel textChannel, VoiceChannel voiceChannel){
        this.gameManager = gameManager;
        this.textChannel = textChannel;
        gamers = new HashMap<>();
        gamers.put(gameManager, 0);
        isEnd = false;
        isStart = false;
        skip = new HashSet<>();
        songSet = new SongSet(50);
        embed = new GameEmbed(this);
        handler = new CommandHandler(this, embed);
        audioPlayer = new GameAudioPlayer(voiceChannel);
        embed.showGameInfo();
    }

    public boolean isStart() {
        return isStart;
    }
    public boolean isHintOn() {
        return hintOn;
    }
    public User getGameManager() {
        return gameManager;
    }
    public TextChannel getTextChannel() {
        return textChannel;
    }
    public HashMap<User, Integer> getGamers() {
        return gamers;
    }
    public ArrayList<Song> getSongs() {
        return songs;
    }
    public Song getSong() {
        return song;
    }
    public HashSet<User> getSkip() {
        return skip;
    }
    public SongSet getSongSet() {
        return songSet;
    }
    public void setHintOn(boolean hintOn) {
        this.hintOn = hintOn;
    }

    // 게임 시작 전의 명령어들을 다루는 함수
    public boolean handleCommand(String command, User user, TextChannel tc) {

        // 사용자들의 게임 참가, 취소, 정보보기 명령어를 다뤄줌
        if (!isStart) {
            if(command.startsWith("!"))
                handler.beforeStartCommand(command, user, tc);
        }
        else if(gamers.containsKey(user)){
            int result = handler.afterStartCommand(command, user, tc);
            if(result == 1){
                count = 1557;
                embed.answerMessage(user.getName());
                playNext();
            }
            else if(result == 3999){
                count = 3999;
                embed.skipMessage();
                playNext();
            }
        }

        return isEnd;
    }

    public void user_in(User user, ButtonClickEvent event){
        if(!gamers.containsKey(user)) {
            gamers.put(user, 0);
            event.reply(user.getName() + "님이 게임에 참여하였습니다.").queue();
            embed.showGameInfo();
        }
    }

    public void user_out(User user, ButtonClickEvent event){
        if(gamers.containsKey(user)){
            if(user == gameManager){
                event.reply("게임 매니저는 참여취소를 할 수 없습니다.").queue();
                return;
            }
            gamers.remove(user);
            event.reply(user.getName() + "님이 게임 참여를 취소하였습니다.").queue();
            embed.showGameInfo();
        }
    }

    // 게임 시작시 실행되는 함수
    public void startGame(){
        // 더이상 참가, 취소가 불가능하게 하며 노래들을 불러오고 첫 노래를 넘겨줌
        songs = songSet.getSongList();

        isStart = true;

        song = songs.remove(0);

        audioPlayer.loadAndPlay(textChannel, song.getUrl());
        countTimer();
    }

    public void playAgain(){
        audioPlayer.skipTrack(textChannel);
        count = 0;
        audioPlayer.loadAndPlay(textChannel, song.getUrl());
    }

    public void playNext(){
        if(songs.size() == 0) {
            gameEnd();
            return;
        }
        song = songs.remove(0);
        skip = new HashSet<>();
        try {
            Thread.sleep(2000);
            audioPlayer.loadAndPlay(textChannel, song.getUrl());
            count = 0;
            countTimer();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void gameEnd(){
        embed.endMessage();
        isEnd = true;
    }

    // 타이머
    private void countTimer(){
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                //TODO Auto-generated method stub
                if (count <= 90) { // count 값이 5보다 작거나 같을때까지 수행
                    count++; //실행횟수 증가
                    if(count % 10 == 0)
                        System.out.println("count = " + count);
                }
                // 제한시간 1분 30초 초과 시 타이머 종료
                if(count == 91) {
                    timer.cancel();
                    embed.timeOverMessage();
                    System.out.println("시간 초과로 노래 종료");
                    audioPlayer.skipTrack(textChannel);
                    count = 0;
                    playNext();
                }
                // 1분 경과 시 힌트출력
                else if(count == 51){
                    embed.showHint();
                }
                // 정답을 맞춘 경우
                else if(count == 1557){
                    timer.cancel(); //타이머 종료
                    System.out.println("정답으로 노래 종료");
                    audioPlayer.skipTrack(textChannel);
                    count = 0;
                }
                // 스킵한 경우
                else if(count == 3999){
                    timer.cancel(); //타이머 종료
                    System.out.println("스킵으로 노래 종료");
                    audioPlayer.skipTrack(textChannel);
                    count = 0;
                }
            }
        };
        timer.schedule(task, 1000, 1000);
    }
}
