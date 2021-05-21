import java.sql.*;
import java.util.ArrayList;

public class DBInterface {


    private ArrayList<String> lastQueryResultPK;
    private DAO dao;


    public DBInterface() {

        lastQueryResultPK = null;
        dao =null;
    }


    public ResultSet getHotels(String prefix) throws SQLException {

        String query = "select * from hotel where name like '" + prefix + "%' order by name asc";

        Statement statement = connection.createStatement();

        return statement.executeQuery(query);

    }

    public String[] findTuplePKs(int rowNum) throws SQLException {

        return lastQueryResultPK.get(rowNum-1).split("~");

    }


    public ResultSet getClientsOfHotel(String hotelPK,String surnamePrefix) throws SQLException {

        String query= "with roomsOfHotel as" +
           "(" +
                    "select \"idRoom\" from room where \"idHotel\"= " + hotelPK +
           " ),"+

           " clientIDsOfHotel as"+
            "("+
                   " select \"bookedforpersonID\" as \"idClient\" from roombooking join roomsOfHotel on \"idRoom\"=\"roomID\"" +
            "),"+

            "clientInfo as"+
            "("+
                    "select \"idClient\" as \"idPerson\" , documentclient from client join clientIDsOfHotel using(\"idClient\")"+
            ")"+

            "select * from person join clientInfo using(\"idPerson\")"+
            "where lname like '" + surnamePrefix + "%'"+
            "order by fname,lname asc";


        Statement statement = connection.createStatement();

        return statement.executeQuery(query);

    }


    public ResultSet getRoomBookingsOfClient(String hotelPK ,String clientID) throws SQLException {
        String query= "with roomsOfHotel as" +
                "(" +
                "select \"idRoom\" from room where \"idHotel\"= " + hotelPK +
                " )" +
                " select * from roombooking join roomsOfHotel on \"idRoom\"=\"roomID\" where \"bookedforpersonID\"=" + clientID;

        Statement statement = connection.createStatement();

        return statement.executeQuery(query);


    }

    public  ResultSet getAvailableRoomsInDateInterval(String hotelID , String startDate,String endDate) throws SQLException {
        String query = "with hotel_rooms " +
                "as" +
                "(" +
                "select \"idRoom\" from room where \"idHotel\" =" + hotelID+
                ")," +
                "" +
                "rooms_unavailable_in_range " +
                "as" +
                "(" +
                "select distinct \"roomID\" from roombooking where (checkin >=" + "'"+startDate+"'" +"::date and checkin<" + "'"+endDate+"'"+"::date)" +
                "or (checkout >"   + "'"+startDate+"'" +"::date and checkout <=" +  "'"+endDate+"'"+"::date)" +
                "or (checkin < " + "'"+startDate+"'" +"::date and checkout >" + "'"+endDate+"'"+"::date)" +
                ")," +
                "unavailable_rooms_of_hotel " +
                "as " +
                "(" +
                "select \"idRoom\" from rooms_unavailable_in_range join room on \"roomID\"=\"idRoom\" where \"idHotel\" = "+ hotelID+
                ")," +
                "available_rooms " +
                "as" +
                "(" +
                "select * from hotel_rooms " +
                "except" +
                " select * from unavailable_rooms_of_hotel" +
                ")" +
                "" +
                "select \"idRoom\", number, roomtype from available_rooms join room using(\"idRoom\")";


        Statement statement = connection.createStatement();

        return statement.executeQuery(query);

    }

    public void createRoombooking(String idRoom, String clientID, String checkin, String checkout) throws SQLException {

        int maxHotelBookingID=0;

        String hotelbookingIDQuery = "select max(idhotelbooking) from hotelbooking";


        Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);

        Statement statement2 = connection.createStatement();



        ResultSet rs = statement.executeQuery(hotelbookingIDQuery);
        if(rs.next())
            maxHotelBookingID = rs.getInt(1);

        int newHotelBookingId=(maxHotelBookingID+1);

        String insertHotelBooking = "insert into hotelbooking values("+ newHotelBookingId+"," +
                                     "'"+ Date.valueOf(Date.valueOf(checkin).toLocalDate().minusDays(20)) + "',"+
                                     "'"+ Date.valueOf(Date.valueOf(checkin).toLocalDate().minusDays(10)) + "',"+
                                     "NULL,"+ clientID +","+"false,"+"NULL,"+"'confirmed')";
        statement2.execute(insertHotelBooking);


        Statement statement3=connection.createStatement();

        String insertRoomBooking=   "insert into roombooking values("+ newHotelBookingId+"," +
                                    idRoom + ","+ clientID + "," +
                                    "'"+ checkin + "',"+
                                    "'"+ checkout + "',0)";


        statement3.execute(insertRoomBooking);



    }

    public void updateRoombooking(String[] roombookingPK,String newCheckIn,String newCheckOut,String newRate) throws SQLException {

        String query = "select * from roombooking where \"hotelbookingID\" = " + roombookingPK[0]+ "and \"roomID\" =" + roombookingPK[1];

        Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);

        ResultSet rs = statement.executeQuery(query);

        if(rs.next()) {
            rs.updateDate(4, Date.valueOf(newCheckIn));
            rs.updateDate(5, Date.valueOf(newCheckOut));
            rs.updateDouble(6, Double.parseDouble(newRate));
            rs.updateRow();
        }

    }



    public String resultSetString(ResultSet rs,int numOfPKs) throws SQLException {
        lastQueryResultPK = new ArrayList<>();

        StringBuilder sb = new StringBuilder();

        int numOfColumns = rs.getMetaData().getColumnCount();

        int rowNum = 1;

        while(rs.next()){
            StringBuilder pks = new StringBuilder();


            for ( int counter = 0; counter < numOfPKs; counter++) {
                pks.append(rs.getString(counter+1));
                pks.append('~');
            }

            lastQueryResultPK.add(pks.toString());

            sb.append(rowNum++ + " ");


            for (int i = 1; i <=numOfColumns ; i++) {
                sb.append( rs.getString(i) + " ");
            }

            sb.append("\n");

        }

        return sb.toString();

    }


    public boolean isConnected() {
        return connection!=null;
    }


}
