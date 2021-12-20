package Data;

import java.sql.*;

public class GetToken {

    public String getToken(){
        Connection con;
        String token = "";

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
            String sql = "select * from token";

            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next()){
                token = resultSet.getString(1);
            }

            resultSet.close();
            preparedStatement.close();
            con.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return token;
    }
}
