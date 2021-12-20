package Game;

import Data.GetToken;
import Embed.Help;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.ButtonClickEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.Button;

import java.util.HashMap;

public class Main extends ListenerAdapter {
    public static void main(String[] args) throws Exception {
        GetToken token = new GetToken();
        JDABuilder builder = JDABuilder.createDefault(token.getToken());

        JDA jda = builder.build();

        jda.addEventListener(new Main());
    }

    HashMap<Guild, Game> games;   // 진행중인 게임들

    public Main() {
        games = new HashMap<Guild, Game>();
    }

    @Override
    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {

        Message message = event.getMessage();
        User user = message.getAuthor();
        String content = message.getContentRaw();
        TextChannel tc = event.getChannel();
        Guild guild = event.getGuild();

        /*
        event.getChannel().sendMessage("button").setActionRow(
                Button.secondary("Btn", "Button")
        ).queue();
         */

        String[] command = content.split(" ", 2);

        if (user.isBot())
            return;

        if (command[0].equals("!help") || command[0].equals("!헬프") || command[0].equals("!가이드") || command[0].equals("!도움말")) {
            Help help = new Help();
            tc.sendMessageEmbeds(help.basicHelp().build()).queue();
        } else if (command[0].equals("!시작설명")) {
            Help help = new Help();
            tc.sendMessageEmbeds(help.beforeHelp().build()).queue();
        } else if (command[0].equals("!게임설명")) {
            Help help = new Help();
            tc.sendMessageEmbeds(help.gameHelp().build()).queue();
        } else if (command[0].equals("!문제설명")) {
            Help help = new Help();
            tc.sendMessageEmbeds(help.songList().build()).queue();
        }

        /*
            서버에 새게임을 추가하거나 추가된 게임을 삭제
         */
        if (command[0].equals("!새게임")) {
            if (!games.containsKey(tc)) {
                if (event.getMember().getVoiceState().getChannel() == null) {
                    tc.sendMessage("음성 채널에 접속해주세요.").queue();
                    return;
                } else {
                    tc.sendMessage("게임이 생성되었습니다.").queue();
                    games.put(guild, new Game(event.getAuthor(), event.getChannel(), event.getMember().getVoiceState().getChannel()));
                }
            } else
                tc.sendMessage("이미 진행중인 게임이 있습니다.").queue();
        } else if (games.containsKey(guild) && user == games.get(guild).getGameManager() && command[0].equals("!게임취소") && !games.get(guild).isStart()) {
            games.remove(guild);
            tc.sendMessage("게임이 취소 되었습니다.").queue();
        }

        if (games.containsKey(guild)) {
            if (games.get(guild).handleCommand(content, user, tc))
                games.remove(guild);
        }
    }

    @Override
    public void onButtonClick(ButtonClickEvent event) {
        if (event.getComponentId().equals("user_in")) {
            Guild guild = event.getGuild();
            games.get(guild).user_in(event.getUser(), event);
        } else if (event.getComponentId().equals("user_out")) {
            Guild guild = event.getGuild();
            games.get(guild).user_out(event.getUser(), event);
        } else if (event.getComponentId().equals("Song_Set_1")) {
            Guild guild = event.getGuild();
            games.get(guild).getSongSet().setYears(1, event);
        } else if (event.getComponentId().equals("Song_Set_2")) {
            Guild guild = event.getGuild();
            games.get(guild).getSongSet().setYears(2, event);
        } else if (event.getComponentId().equals("Song_Set_3")) {
            Guild guild = event.getGuild();
            games.get(guild).getSongSet().setYears(3, event);
        }

        super.onButtonClick(event);
    }
}