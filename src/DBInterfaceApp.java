
import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;

public class DBInterfaceApp {



    static Scanner scanner = new Scanner(System.in);

    static DBInterface db = new DBInterface();




    public static void main(String[] args) {



       while(true){


           System.out.println("1.Connect to database");

           System.out.println("2.Search Hotel with prefix");

           System.out.println("3.Exit program");
           String[] hotelPK=null;
           String[] primaryKeys= null;
           String clientID = null;

           switch(waitForInput("Select option: ")){
               case "1":
                    /*String ip = waitForInput("Enter the ip address of the database: ");
                    String dbName = waitForInput("Enter the name of the database: ");
                    String username = waitForInput("Enter username: ");
                    String pwd = waitForInput("Enter password: ");*/

                   try {
                       db.connectToDatabase("127.0.0.1", "db_project_2","postgres","ocelotej2002");
                       System.out.println("Succesfully connected to database.");
                   } catch (SQLException throwables) {
                       System.out.println("Unable to connect to database. ");
                   }


                   break;

               case "2":

                   if(db.isConnected()){
                       String hPrefix = waitForInput("Enter hotel prefix: ");

                       try {
                           System.out.println(db.resultSetString(db.getHotels(hPrefix),1));
                           hotelPK = db.findTuplePKs(Integer.parseInt(waitForInput("Please enter the hotel")));

                       } catch (SQLException throwables) {
                           throwables.printStackTrace();
                       }

                       boolean exitCond=false;
                       while (!exitCond) {

                           System.out.println("0.Return to main menu.");

                           System.out.println("1.Search clients of hotel.");

                           System.out.println("2.Show roombookings of a given hotel client.");

                           System.out.println("3.Show available rooms in specific date interval.");

                            try{
                                switch(waitForInput("Select option: ")){
                                    case "0":
                                        exitCond=true;
                                        break;
                                    case "1":
                                        String surnamePrefix = waitForInput("Enter last name prefix: ");

                                        System.out.println(db.resultSetString(db.getClientsOfHotel(hotelPK[0],surnamePrefix),1));

                                        break;
                                    case "2":
                                        clientID = waitForInput("Enter Client ID: ");

                                        System.out.println(db.resultSetString(db.getRoomBookingsOfClient(hotelPK[0],clientID),2));

                                        while(waitForInput("Update roombookings? y/n").equals("y")){

                                            String[] roombookingPK = db.findTuplePKs(Integer.parseInt(waitForInput("Please enter the roombooking")));

                                            String newCheckIn = waitForInput("Enter the new checkin date in format yyyy-mm-dd: ");
                                            String newCheckOut = waitForInput("Enter the new checkout date in format yyyy-mm-dd: ");
                                            String newRate  = waitForInput("Enter the new rate: ");

                                            db.updateRoombooking(roombookingPK,newCheckIn,newCheckOut,newRate);


                                        }

                                        break;
                                    case "3":

                                        String startDate = waitForInput("Enter the starting date: ");
                                        String endDate = waitForInput("Enter the ending date: ");

                                        System.out.println(db.resultSetString(db.getAvailableRoomsInDateInterval(hotelPK[0],startDate,endDate),1));

                                        if (waitForInput("Do you want to make a reservation? y/n").equals("y")){

                                            String idRoom = waitForInput("Enter the room you want to reserve: ");
                                            clientID = waitForInput("Enter the client you are booking the room for: ");

                                            db.createRoombooking(idRoom,clientID,startDate,endDate);

                                        }

                                        break;

                                    default:


                                }
                            } catch (SQLException throwables) {
                           throwables.printStackTrace();
                       }



                       }



                   }else
                       System.out.println("No connected database.Please try again.");



                   break;

               case "3":
                   try {
                       db.closeConnection();
                   } catch (SQLException throwables) {
                       throwables.printStackTrace();
                   }
                   System.exit(0);
                   break;

               default:
                   System.out.println("Invalid input, please try again.");
           }
       }


    }

    static String waitForInput(String inputStr){

        System.out.println(inputStr);

        return scanner.next();

    }







}
