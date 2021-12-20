package Embed;

import Data.LoadQuestions;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.User;

import java.awt.*;
import java.util.*;
import java.util.List;

public class Help {

    public EmbedBuilder basicHelp(){
        EmbedBuilder embed = new EmbedBuilder();

        embed.setTitle("가이드");
        embed.setColor(Color.WHITE);
        embed.addField("!시작설명", "게임을 세팅하고 시작하기 위한 설명입니다.", false);
        embed.addField("!게임설명", "게임 진행 방법에 대한 설명입니다.", false);
        embed.addField("!문제설명", "선택할 수 있는 문제들을 볼 수 있습니다.", false);
        embed.setFooter("Doz Song Game Bot  Help");

        return embed;
    }

    public EmbedBuilder beforeHelp(){
        EmbedBuilder embed = new EmbedBuilder();

        embed.setTitle("게임 시작 가이드");
        embed.setColor(Color.WHITE);
        embed.addField("!새게임", "새로운 게임을 생성합니다. 해당 명령어를 실행한 유저가 게임 매니저가 됩니다.", false);
        embed.addField("!게임취소", "게임을 생성한 게임 매니저가 해당 명령어를 실행하면 게임을 취소합니다.", false);
        embed.addField("!게임정보", "게임에 참여된 유저가 해당 명령어를 실행하면 해당 게임의 정보를 출력합니다.", false);
        embed.addField("!문제설정", "게임 매니저가 해당 명령어를 실행 후 나오는 버튼을 통해 문제를 설정할 수 있습니다.", false);
        embed.addField("!문제수", "게임을 생성한 게임 매니저가 해당 명령어를 실행하면 해당 게임의 문제 수를 xxx 개로 설정합니다. ex !문제수 100", false);
        embed.addField("!힌트O !힌트X", "해당 게임에 힌트 사용유무를 설정합니다. 힌트는 노래 시작 50초 경과 시 출력됩니다.", false);
        embed.addField("게임 참여", "게임 정보 화면 하단의 버튼을 통해 게임에 참여하거나 참여 취소합니다. 게임매니저는 참여 취소가 불가능 합니다.", false);
        embed.addField("게임 매니저", "게임을 최초 생성한 유저입니다. 해당 유저가 게임을 진행하게 됩니다.", false);
        embed.setFooter("Doz Song Game Bot  Before Help");

        return embed;
    }

    public EmbedBuilder gameHelp(){
        EmbedBuilder embed = new EmbedBuilder();

        embed.setTitle("게임 진행 가이드");
        embed.setColor(Color.WHITE);
        embed.addField("!시작", "게임 매니저가 해당 명령어를 실행하면 게임이 시작됩니다.", false);
        embed.addField("!스킵", "게임 참여자가 모두 스킵을 하면 해당 문제가 스킵됩니다.", false);
        embed.addField("!다시", "노래가 들리지 않을 경우 게임 매니저가 해당 명령어를 사용하면 노래를 다시 재생합니다.", false);
        embed.addField("게임 진행", "게임 시작 후 문제를 맞추거나 스킵하거나 시간초과가 되면 2초 후 자동으로 다음 문제의 노래를 실행합니다.", false);
        embed.addField("정답 맞추기", "정답은 이 채널에 입력하면 됩니다. 띄워쓰기는 상관없고, 정답이 영어일때는 한글도 가능하며, 영어는 대소문자 구분없이 가능합니다.", false);
        embed.addField("제한시간, 힌트", "노래는 1분 30초 동안 재생되며, 1분 30초가 지나면 자동으로 스킵됩니다. 힌트를 사용한 경우 노래 시작후 1분후에 출력됩니다.", false);
        embed.setFooter("Doz Song Game Bot  Game Help");

        return embed;
    }

    public EmbedBuilder songList(){
        LoadQuestions load = new LoadQuestions();
        List<Map.Entry<Integer, Integer>> entries = new LinkedList<>(load.QuestionsFromDB().entrySet());

        EmbedBuilder embed = new EmbedBuilder();

        embed.setTitle("선택 가능 문제들");
        embed.setColor(Color.WHITE);

        for(Map.Entry<Integer, Integer> entry : entries) {
            String s = "";
            if(entry.getKey() == 1)
                s = "2000년대";
            else if(entry.getKey() == 2)
                s = "2010년대 초반";
            else if(entry.getKey() == 3)
                s = "2010년대 후반";
            embed.addField(s, entry.getValue() + "개", false);
        }

        embed.setFooter("Doz Song Game Bot  Year");

        return embed;
    }
}
