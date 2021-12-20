package Data;
import java.sql.*;

public class MakeAnswers {
    /*
    public static void main(String[] args) {
        MakeAnswers ma = new MakeAnswers();

        int id_start = 1358;
        String s = ma.songsFromDB(id_start);
        System.out.println(s);
    }
     */

    public boolean checkEng(String s) {
        if (!s.matches("[가-힣]*")) {
            return true;
        }
        return false;
    }

    public String parsing(String title) {
        String answer = title.split("\\(")[0].toLowerCase();
        answer = answer.replace(",", "");
        answer = answer.replace("-", "");
        answer = answer.replace(":", "");
        answer = answer.replace("'", "");
        answer = answer.replace(".", "");
        answer = answer.replace("!", "");
        answer = answer.replace("?", "");
        answer = answer.replace(" ", "");
        return answer;
    }

    public String songsFromDB(int id_start) {
        Connection con;
        StringBuilder builder = new StringBuilder();
        builder.append("insert into answer (id, answer) values ");

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
            String sql;

            sql = "select id, title from song where id > ? order by id";
            preparedStatement = con.prepareStatement(sql);
            preparedStatement.setInt(1, id_start);
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String title = resultSet.getString("title");
                int id = resultSet.getInt("id");
                if(checkEng(parsing(title)))
                    builder.append("(" + id + ", " + "'" + parsing(title) + "'" + "),\n");
            }

            resultSet.close();
            preparedStatement.close();
            con.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return builder.toString().trim();
    }
}