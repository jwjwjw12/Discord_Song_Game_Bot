package Game;

import Embed.GameEmbed;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

import java.util.HashMap;
import java.util.HashSet;

public class CommandHandler {
    Game game;
    GameEmbed embed;

    public CommandHandler(Game game, GameEmbed embed) {
        this.game = game;
        this.embed = embed;
    }

    /*
        게임 시작 전의 명령어들을 다룸
     */
    public void beforeStartCommand(String command, User user, TextChannel tc) {
        HashMap<User, Integer> gamers = game.getGamers();
        /*
            매니저가 아닌 유저들이 사용할 수 있는 명령어들
            - 새로운 유저의 참여
            - 기존의 유저의 참여 취소
            - 게임 정보 조회
        */
        if ((command.equals("!게임정보") || command.equals("!게임") || command.equals("!정보")) && gamers.keySet().contains(user)) {
            embed.showGameInfo();
        }
        if (user == game.getGameManager()) {
            if (command.startsWith("!")) {
                String[] commands = command.split(" ", 2);

                /*
                    매니저의 게임 기본 세팅 설정
                    - 문제에 들어갈 년도 추가, 삭제
                    - 문제 수 설정
                    - 힌트 유무 설정
                 */

                if (commands[0].equals("!문제설정")) {
                    embed.showSongSetting();
                } else if (commands[0].equals("!문제수") && commands.length == 2) {
                    if (commands[1].matches("\\d*"))
                        game.getSongSet().setNumOfSong(Integer.parseInt(commands[1]));
                    embed.showGameInfo();
                } else if ((commands[0].equals("!힌트O") || commands[0].equals("!힌트o")) && !game.isHintOn()) {
                    game.setHintOn(true);
                    embed.showGameInfo();
                } else if ((commands[0].equals("!힌트X") || commands[0].equals("!힌트x")) && game.isHintOn()) {
                    game.setHintOn(false);
                    embed.showGameInfo();
                }

                /*
                    매니저의 게임 시작
                 */
                else if ((commands[0].equals("!시작"))) {
                    game.startGame();
                }
            }
        }

    }

    /*
        게임 진행 중 명령어들을 다루는 함수, 정답을 맞춘 경우 1를 리턴
     */
    public int afterStartCommand(String command, User user, TextChannel tc) {

        if (command.equals("!스킵")) {
            HashSet<User> skip = game.getSkip();
            int all = game.getGamers().size();
            skip.add(user);
            if (skip.size() == all) {
                return 3999;
            } else {
                EmbedBuilder embed = new EmbedBuilder();
                embed.setTitle("현재 스킵 인원 : " + skip.size() + "/" + all);
                tc.sendMessageEmbeds(embed.build()).queue();
            }
        }

        String answer = command.toLowerCase();
        answer = answer.replace(" ", "");

        if (game.getSong().isAnswer(answer)) {
            game.getGamers().put(user, game.getGamers().get(user) + 1);

            return 1;
        }

        if (user == game.getGameManager()) {
            if(command.equals("!다시"))
                game.playAgain();
        }

        return -1;
    }

}
