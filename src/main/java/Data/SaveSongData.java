package Data;

import Game.Song;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.sql.*;
import java.util.ArrayList;

public class SaveSongData {

    /*
    public static void main(String[] args) {
        SaveSongData save = new SaveSongData();
        2020, 2019, 2018, 2017, 2016, 2015, 2014, 2013, 2012, 2011, 2010, 2009, 2008, 2007, 2006 완료

        int year = 2007;

        ArrayList<Song> songs = save.getMelonSongs(year);

        save.songsToDB(songs);

    }
    */

    public void songsToDB(ArrayList<Song> songs) {
        Connection con = null;

        String jdbc_driver = "org.mariadb.jdbc.Driver";
        String jdbc_url = "jdbc:mysql://localhost:3307/dozi_Song_Bot_db";
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
            String sql;

            for (Song song : songs) {
                sql = "Insert Into SONG(artist, title, url, img, category, year) Values(?, ?, ?, ?, ?, ?);";
                preparedStatement = con.prepareStatement(sql);
                preparedStatement.setString(1, song.getArtist());
                preparedStatement.setString(2, song.getTitle());
                preparedStatement.setString(3, song.getUrl());
                preparedStatement.setString(4, song.getImg());
                preparedStatement.setString(5, song.getCategory());
                preparedStatement.setInt(6, song.getYear());
                preparedStatement.executeUpdate();
            }

            preparedStatement.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean checkSong(String artist, String title) {
        Connection con;
        boolean result = true;

        String jdbc_driver = "org.mariadb.jdbc.Driver";
        String jdbc_url = "jdbc:mysql://localhost:3307/dozi_Song_Bot_db";
        String db_name = "root";
        String db_password = "3384msmt";

        try {
            Class.forName(jdbc_driver);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        try {
            con = DriverManager.getConnection(jdbc_url, db_name, db_password);

            PreparedStatement preparedStatement;
            String sql;

            sql = "select * from SONG where title = ? and artist = ?;";
            preparedStatement = con.prepareStatement(sql);
            preparedStatement.setString(1, title);
            preparedStatement.setString(2, artist);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next())
                result = false;

            resultSet.close();
            preparedStatement.close();
            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }

    // 가수이름 + 가수제목으로 유튜브에서 검색하여 리스트의 첫번째 동영상의 주소를 리턴
    public String getSongURL(String artist, String title) throws IOException, ParseException {
        String search = artist + " " + title + " 가사";
        String apiURL = "https://www.googleapis.com/youtube/v3/search";
        apiURL += "?key=";      // google API key
        apiURL += "&part=id&type=video&maxResults=5";
        apiURL += "&q=" + URLEncoder.encode(search, "UTF-8");

        URL url = new URL(apiURL);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "UTF-8"));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = br.readLine()) != null) {
            response.append(inputLine);
        }
        br.close();

        JSONParser jsonParser = new JSONParser();
        Object obj = jsonParser.parse(response.toString());
        JSONObject json = (JSONObject) obj;
        JSONArray items = (JSONArray) json.get("items");
        JSONObject target = (JSONObject) items.get(0);
        JSONObject id = (JSONObject) target.get("id");
        String videoId = (String) id.get("videoId");

        return "https://www.youtube.com/watch?v=" + videoId;
    }

    public ArrayList<Song> getMelonSongs(int year) {
        String webUrl = "https://www.melon.com/chart/age/list.htm?idx=1&chartType=YE&chartGenre=KPOP&chartDate=" + year + "&moved=Y";
        Document doc = null;
        ArrayList<Song> songs = new ArrayList<>();

        try {
            doc = Jsoup.connect(webUrl).get();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Elements elements = doc.select("input.input_check");

        for (int i = 1; i < elements.size(); i++) {
            String value = elements.get(i).attr("value");

            Song song = getMelonSongInfo(value);
            if(song != null)
                songs.add(song);
        }

        return songs;
    }

    public Song getMelonSongInfo(String song_id) {
        String webUrl = "https://www.melon.com/song/detail.htm?songId=" + song_id;        // pg=1 : 1~50 , pg=2 : 51~100
        Document doc = null;

        try {
            doc = Jsoup.connect(webUrl).get();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String title = doc.select("div.song_name").text().substring("곡명 ".length());
        title = parsingTitle(title);
        String artist = doc.select("div.artist a").first().text();
        if(!checkSong(artist, title))
            return null;
        String img = doc.select("div.thumb a.image_typeAll img").first().attr("src");
        String year = doc.select("dl.List dd").get(1).text().split("\\.")[0];
        String category = doc.select("dl.List dd").get(2).text();
        String url = artist + " " + title + " 가사";

        try {
            url = getSongURL(artist, title);
        } catch (IOException e) {
        } catch (ParseException e) {
        }

        System.out.println(artist + " - " + title + "   url = " + url);
        return new Song(artist, title, url, img, Integer.parseInt(year), category);
    }

    public String parsingTitle(String title) {
        if (title.length() > 4 && title.substring(0, 3).equals("19금"))
            return title.substring("19금 ".length());
        else return title;
    }
}
