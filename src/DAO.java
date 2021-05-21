import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.ArrayList;

public class DAO {

    private static Connection connection=null;
    private static ArrayList<String> lastQueryResultPK;



    public static void setUpDBConnection(String ip,String dbName, String username,String password) throws SQLException {

        connection= DriverManager.getConnection("jdbc:postgresql://"+ip+":5432/"+dbName,username,password);

    }


    public static void closeConnection() throws SQLException {
        connection.close();
        connection=null;
    }

    public static boolean isConnected(){
        return connection!=null;
    }



}
