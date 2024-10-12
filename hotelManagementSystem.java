import java.sql.*;
import java.util.Scanner;


public class hotelManagementSystem {

    public static void main(String[] args) {
        final String url = "jdbc:mysql://localhost:3306/hotel_db";
        final String username = "root";
        final String password = "root123";

        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("Connection Created");
        }
        catch(Exception e){
            System.out.println(e.getMessage());
        }
        try{
            Connection connection = DriverManager.getConnection(url, username, password);

            while (true) {

                System.out.println();
                System.out.println("************ HOTEL MANAGEMENT SYSTEM **************");
                System.out.println("1. RESERVE ROOM ****");
                System.out.println("2. VIEW RESERVATIONS ****");
                System.out.println("3. GET ROOM NUMBER ****");
                System.out.println("4. UPDATE RESERVATION ****");
                System.out.println("5. DELETE RESERVATION ****");
                System.out.println("0. EXIT ****");
                System.out.println("************* Enter an Option **********************");
                Scanner scanner = new Scanner(System.in);
                int option = scanner.nextInt();

                switch (option){
                    case 1:
                        reserveRoom(connection,scanner);
                        break;
                    case 2:
                        viewReservation(connection);
                        break;
                    case 3:
                        getRoomNumber(connection,scanner);
                        break;
                    case 4:
                        updateReservation(connection,scanner);
                        break;
                    case 5:
                        deleteReservation(connection,scanner);
                        break;
                    case 0:
                        exit();
                        scanner.close();
                        connection.close();
                        return;
                    default:
                        System.out.println("Invalid Option! Try Again");
                }

            }

        }catch (Exception e){
            System.out.println(e.getMessage());
        }

    }//MAIN FUNCTION END

    private static void reserveRoom(Connection connection, Scanner scanner){

        System.out.print("Enter Guest Name: ");
        String guestName = scanner.next();
        scanner.nextLine();
        System.out.print("Enter Room Number: ");
        int roomNumber = scanner.nextInt();
        System.out.print("Enter Contact Number: ");
        String contactNumber = scanner.next();

        String sql = "INSERT INTO hotel (guest_name, room_number, contact_number) VALUES('"+guestName+"','"+roomNumber+"','"+contactNumber+"')";
        try(Statement statement = connection.createStatement()){
                int affectedRows = statement.executeUpdate(sql);

        if(affectedRows>0){
            System.out.println("Reservation Successful");
        }else {
            System.out.println("Reservation Not Successful");
        }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }

    }     //reserveRoom Function END

    private static void viewReservation(Connection connection) {
        String sql = "SELECT * FROM hotel";

        try(Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql)){

            System.out.println("************************************* Current Reservation *************************************");
            System.out.println("+----------------+--------------------+-------------+----------------+------------------------+");
            System.out.println("| Reservation ID | Guest              | Room Number | Contact Number | Reservation Date       |");
            System.out.println("+----------------+--------------------+-------------+----------------+------------------------+");

            while (resultSet.next()){
            int reservationId = resultSet.getInt("reservation_id");
            String guestName = resultSet.getString("guest_name");
            int roomNumber = resultSet.getInt("room_number");
            String contactNumber = resultSet.getString("contact_number");
            String reservationDate = resultSet.getTimestamp("reservation_date").toString();

                System.out.printf("| %-14d | %-18s | %-11d | %-14s | %-22s | \n",reservationId,guestName,roomNumber,contactNumber,reservationDate);

            }
            System.out.println("+----------------+--------------------+-------------+----------------+------------------------+");


        }catch(Exception e){
            System.out.println(e.getMessage());

        }
    }     //viewReservations Function END

    public static void getRoomNumber(Connection connection, Scanner scanner){
            try{
                System.out.print("Enter Reservation ID: ");
                int reservationId = scanner.nextInt();
                System.out.print("Enter Guest Name: ");
                String guestName = scanner.next();

                String sql = "SELECT room_number FROM hotel WHERE reservation_id = "+reservationId+" AND guest_name = '"+guestName+"'; ";

                try(Statement statement = connection.createStatement();
                ResultSet resultSet = statement.executeQuery(sql)){         //If this is true then resultSet = true;

                    if(resultSet.next()){
                        int roomNumber = resultSet.getInt("room_number");
                        System.out.println("Room Number for Reservation ID "+ reservationId +" and Guest "+guestName+" is : "+ roomNumber);
                }else{
                    System.out.println("Reservation not found");
                }
            }

            }catch (Exception e){
                System.out.println(e.getMessage());
            }
    }     //getRoomNumber Function END


    public static void updateReservation(Connection connection, Scanner scanner){

        System.out.print("Enter Reservation ID to Update: ");
        int reservationId = scanner.nextInt();

        try{
            if (!reservationExists(connection,reservationId)){
                System.out.println("Reservation not Found.");
             return;
            }

            System.out.println("Enter New Guest Name: ");
            String newGuestName = scanner.next();
            System.out.println("Enter New Room Number: ");
            String newRoomNumber = scanner.next();
            System.out.println("Enter New Contact Number: ");
            String newContactNumber = scanner.next();

            String sql = "UPDATE hotel SET guest_name = '"+newGuestName+"', room_number = "+newRoomNumber+" , contact_number = '"+newContactNumber +"' "+
                    "WHERE reservation_id = "+reservationId;

            try(Statement statement = connection.createStatement()){
            int affectedRows = statement.executeUpdate(sql);
            if(affectedRows>0){
                System.out.println("Reservation Updated Successfully");
            }else {
                System.out.println("Reservation Not Updated");
            }

        }

        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }     //updateReservation Function END


    public static void deleteReservation(Connection connection, Scanner scanner){

        System.out.println("Enter Reservation_ID to DELETE: ");
        int reservationId = scanner.nextInt();
        try{
            if(!reservationExists(connection,reservationId)){

            System.out.println("Reservation not Found");
            return;
        }
        String sql = "DELETE FROM hotel WHERE reservation_id = "+reservationId;

        try(Statement statement = connection.createStatement()){

            int affectedRows = statement.executeUpdate(sql);
            if(affectedRows>0){
                System.out.println("Reservation Deleted Successfully");
            }else {
                System.out.println("Reservation Not Deleted");
            }

        }

        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }     //deleteReservation Function END


    private static boolean reservationExists(Connection connection, int reservationId){

        String sql = "SELECT reservation_id FROM hotel WHERE reservation_id = "+reservationId;

        try(Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql)){

            return resultSet.next();

        }catch (Exception e){
            System.out.println(e.getMessage());
        return false;
        }
    }     //reservationExists Function END

    public static void exit() throws InterruptedException {
        int i = 5;
        System.out.print("Exiting the System");
        while (i!=0){
            System.out.print(".");
        Thread.sleep(500);
        i--;
        }

        System.out.println();
        System.out.println("Thanks for using our Services");
    }     //exit Function END

}//Class end
