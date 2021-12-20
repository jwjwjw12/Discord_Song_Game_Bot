package Game;

import java.util.HashSet;

public class Song {
    private String artist;
    private String title;
    private String url;
    private String hint;
    private String img;
    private String category;
    private int year;
    private int id;
    private HashSet<String> answers;

    public Song(String artist, String title, String url, String img, int year, String category){
        this.artist = artist;
        this.title = title;
        this.url = url;
        this.img = img;
        this.year = year;
        this.category = category;
    }

    public Song(String artist, String title, String url, String img, String category, int year, int id){
        this.artist = artist;
        this.title = title;
        this.url = url;
        this.img = img;
        this.category = category;
        this.year = year;
        this.id = id;
        answers = new HashSet<>();
        addAnswer(title);
    }

    public String getArtist() {
        return artist;
    }
    public String getTitle() {
        return title;
    }
    public String getUrl() {
        return url;
    }
    public String getHint() {
        return hint;
    }
    public String getImg() {
        return img;
    }
    public String getCategory() {
        return category;
    }
    public int getYear() {
        return year;
    }
    public int getId(){ return id; }

    public HashSet<String> getAnswers() {
        return answers;
    }

    public void addAnswer(String title){
        String answer = parsing(title);
        answers.add(answer);
    }

    public String parsing(String title){
        String answer = title.split("\\(")[0].toLowerCase();
        answer = answer.replace(",", "");
        answer = answer.replace("-", "");
        answer = answer.replace("!'", "");
        answer = answer.replace("?", "");
        answer = answer.replace(":", "");
        answer = answer.replace("'", "");
        answer = answer.replace(".", "");
        answer = answer.replace(" ", "");
        return answer;
    }

    public boolean isAnswer(String answer){
        if(answers.contains(answer))
            return true;
        else
            return false;
    }

    public void makeHint(){
        Hint hint = new Hint();

        this.hint = hint.getHint(answers);
    }
}
