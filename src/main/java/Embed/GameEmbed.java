package Embed;

import Game.Game;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.interactions.components.Button;

import java.awt.*;
import java.util.List;
import java.util.*;

public class GameEmbed {
    Game game;

    public GameEmbed(Game game) {
        this.game = game;
    }

    public void showGameInfo() {
        EmbedBuilder embed = new EmbedBuilder();

        embed.setTitle("게임 정보");
        embed.setColor(Color.CYAN);
        embed.addField("게임 매니저", game.getGameManager().getName(), true);
        embed.addField("게임 진행 채널", game.getTextChannel().getName(), true);
        embed.addBlankField(true);
        embed.addField("문제 수", "" + game.getSongSet().getNumOfSong(), true);
        if (game.isHintOn())
            embed.addField("힌트", "O", true);
        else
            embed.addField("힌트", "X", true);
        embed.addBlankField(true);

        HashSet<Integer> years = game.getSongSet().getYears();
        StringBuilder yearString = new StringBuilder();
        for (int year : years) {
            if(year == 1)
                yearString.append("00년대\n");
            else if(year == 2)
                yearString.append("10년대초반\n");
            else if(year == 3)
                yearString.append("10년대후반\n");
        }
        if (years.size() == 0)
            yearString.append("ALL");
        embed.addField("출제 문제", yearString.toString(), false);

        Set<User> users = game.getGamers().keySet();
        StringBuilder userString = new StringBuilder();
        for (User user : users) {
            userString.append(user.getName());
            userString.append("\n");
        }
        embed.addField("현재 참가자", userString.toString(), false);

        game.getTextChannel().sendMessageEmbeds(embed.build())
                .setActionRow(
                        Button.success("user_in", "참가"),
                        Button.secondary("user_out", "참가취소"))
                .queue();
    }

    public void answerMessage(String name) {
        QuestionEndMessage(name + " 정답!!", Color.GREEN);
    }

    public void skipMessage() {
        QuestionEndMessage("스킵!!", Color.RED);
    }

    public void timeOverMessage() {
        QuestionEndMessage("시간 초과!!", Color.RED);
    }

    private void QuestionEndMessage(String title, Color color) {
        EmbedBuilder embed = new EmbedBuilder();

        embed.setTitle(title);
        embed.setColor(color);
        embed.addField(game.getSong().getArtist() + " - " + game.getSong().getTitle(), game.getSong().getCategory(), false);
        embed.setThumbnail(game.getSong().getImg());

        embed.addField("스코어", scoreString(), true);

        game.getTextChannel().sendMessageEmbeds(embed.build()).queue();
    }

    public void endMessage() {
        EmbedBuilder embed = new EmbedBuilder();

        String scores = scoreString();

        embed.setTitle(scores.split("-")[0] + " 우승!!!");
        embed.setColor(Color.CYAN);
        embed.addField("전체 스코어", scores, true);
        embed.setFooter("Dozi bot 을 이용해 주셔서 감사합니다!!");

        game.getTextChannel().sendMessageEmbeds(embed.build()).queue();
    }

    public String scoreString() {
        List<Map.Entry<User, Integer>> entries = new LinkedList<>(game.getGamers().entrySet());

        StringBuilder userString = new StringBuilder();

        for (Map.Entry<User, Integer> entry : entries) {
            userString.append(entry.getKey().getName() + "-------" + entry.getValue() + "점");
            userString.append("\n");
        }

        return userString.toString();
    }

    public void showHint() {
        if (game.isHintOn()) {
            EmbedBuilder embed = new EmbedBuilder();

            embed.setTitle("초성 힌트 !!!");
            embed.setColor(Color.CYAN);
            embed.addField(game.getSong().getHint(), "", false);

            game.getTextChannel().sendMessageEmbeds(embed.build()).queue();
        }
    }

    public void showSongSetting(){
        EmbedBuilder embed = new Help().songList();

        game.getTextChannel().sendMessageEmbeds(embed.build())
                .setActionRow(
                        Button.primary("Song_Set_1", "00년대"),
                        Button.primary("Song_Set_2", "10년대초반"),
                        Button.primary("Song_Set_3", "10년대후반"))
                .queue();
    }
}
