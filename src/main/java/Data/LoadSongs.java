package Data;

import Game.Song;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;

public class LoadSongs {

    // 테스트용
    public static void main(String[] args) {
        HashSet<Integer> years = new HashSet<>();
        int limit = 200;

        LoadSongs load = new LoadSongs();
        ArrayList<Song> songs = load.songsFromDB(years, limit);

        for(Song song : songs){
            load.findNot(load.parsing(song.getTitle()));
        }
    }

    public void findNot(String s){
        if(!s.matches("[가-힣]*")){
            System.out.println(s);
        }
    }

    public String parsing(String title){
        String answer = title.split("\\(")[0].toLowerCase();
        answer = answer.replace(",", "");
        answer = answer.replace("-", "");
        answer = answer.replace("!'", "");
        answer = answer.replace("?", "");
        answer = answer.replace(":", "");
        answer = answer.replace("'", "");
        answer = answer.replace(" ", "");
        return answer;
    }

    // 해당하는 년도의 노래들을 랜덤해서 limit 만큼 리스트로 가져오는 함수
    public ArrayList<Song> songsFromDB(HashSet<Integer> years, int limit) {
        Connection con = null;
        ArrayList<Song> songs = new ArrayList<>();

        String jdbc_driver = "org.mariadb.jdbc.Driver";
        String jdbc_url = "jdbc:mysql://localhost:3307/Dozi_Song_Bot_db";
        String db_name = "root";
        String db_password = "3384msmt";

        try {
            Class.forName(jdbc_driver);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try {
            con = DriverManager.getConnection(jdbc_url, db_name, db_password);

            PreparedStatement preparedStatement = null;
            String sql = "select * from song";

            if(years.size() == 0) {
                sql = "select * from song order by rand() limit ?";
                preparedStatement = con.prepareStatement(sql);
                preparedStatement.setInt(1, limit);
            }
            else{
                sql = "select * from song where";
                boolean first = true;
                for(int year : years) {
                    if(first)
                        first = false;
                    else
                        sql += " or ";

                    if(year == 1)
                        sql += " (year < 2010)";
                    else if(year == 2)
                        sql += " (year >= 2010 and year < 16)";
                    else if(year == 3)
                        sql += " (year >= 2016)";
                }
                sql += "order by rand() limit ?";
                preparedStatement = con.prepareStatement(sql);
                preparedStatement.setInt(1, limit);
            }

            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()){
                String artist = resultSet.getString("artist");
                String title = resultSet.getString("title");
                String url = resultSet.getString("url");
                String img = resultSet.getString("img");
                String category = resultSet.getString("category");
                int id = resultSet.getInt("id");
                int year = resultSet.getInt("year");
                songs.add(new Song(artist, title, url, img, category, year, id));
            }

            for(Song song : songs){
                sql = "select answer from answer where id = ?";
                preparedStatement = con.prepareStatement(sql);
                preparedStatement.setInt(1, song.getId());
                resultSet = preparedStatement.executeQuery();
                while(resultSet.next())
                    song.addAnswer(resultSet.getString(1));
                song.makeHint();
            }

            resultSet.close();
            preparedStatement.close();
            con.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return songs;
    }
}
