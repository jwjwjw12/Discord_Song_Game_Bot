package Data;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class LoadQuestions {

    // 테스트용
    public static void main(String[] args) {
        LoadQuestions load = new LoadQuestions();

        HashMap<Integer, Integer> map = load.QuestionsFromDB();

        Set<Map.Entry<Integer, Integer>> entries = map.entrySet();

        for(Map.Entry<Integer, Integer> entry : entries)
            System.out.println(entry.getKey() + "년 : " + entry.getValue());
    }

    // Year, count 형식의 년도에 맞는 문제 수를 가져오기 위함
    public HashMap<Integer, Integer> QuestionsFromDB() {
        Connection con;
        HashMap<Integer, Integer> map = new HashMap<>();

        String jdbc_driver = "org.mariadb.jdbc.Driver";;
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
            PreparedStatement preparedStatement;
            ResultSet resultSet;

            String sql ="select count(*) from song where year < 2010";
            preparedStatement = con.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();

            if(resultSet.next()){
                int cnt = resultSet.getInt(1);
                map.put(1, cnt);
            }

            sql ="select count(*) from song where year >= 2010 and year < 2016";
            preparedStatement = con.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();

            if(resultSet.next()){
                int cnt = resultSet.getInt(1);
                map.put(2, cnt);
            }

            sql ="select count(*) from song where year >= 2016";
            preparedStatement = con.prepareStatement(sql);
            resultSet = preparedStatement.executeQuery();

            if(resultSet.next()){
                int cnt = resultSet.getInt(1);
                map.put(3, cnt);
            }

            resultSet.close();
            preparedStatement.close();
            con.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return map;
    }
}
