package Game;

import java.util.HashSet;

public class Hint {

    public String getHint(HashSet<String> answers){
        for(String answer : answers){
            if(answer.matches("[가-힣]*")){
                return makeHint(answer);
            }
        }
        return "없음";
    }

    public String makeHint(String s){
        StringBuilder stringBuilder = new StringBuilder();

        for(char c : s.toCharArray()){
            stringBuilder.append(getChs(c));
        }

        return stringBuilder.toString();
    }

    public String getChs(char c){
        String [] chs = {
                "ㄱ", "ㄲ", "ㄴ", "ㄷ", "ㄸ",
                "ㄹ", "ㅁ", "ㅂ", "ㅃ", "ㅅ",
                "ㅆ", "ㅇ", "ㅈ", "ㅉ", "ㅊ",
                "ㅋ", "ㅌ", "ㅍ", "ㅎ"
        };

        return chs[((c-0xAC00)/28/21)];
    }
}
