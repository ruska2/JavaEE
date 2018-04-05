/*
 * Hotove:
 * 1.
 * 2. stored procedures, transaction, commit, rollback insertRandomsJPA()
 * */

import java.sql.*;
import java.util.ArrayList;
import java.util.Random;

public class GetAllRows{
	
  static String[] cars = {"Audi","BMW","Skoda","VW","Honda","Mazda","Renault","Seat","Fiat"};
  static String[] colors = {"red","blue","brown","black","white","green","yellow"};
  static Random rnd = new Random();
  static ResultSet yellowAndRedCars;
  static ArrayList<String> ids = new ArrayList<>();
  
  public static void main(String[] args) {
    Connection con = null;
    String url = "jdbc:mysql://kempelen.ii.fmph.uniba.sk:3306/";
    String db = "ee";
    String driver = "com.mysql.jdbc.Driver";
    String user = "ee";
    String pass = "pivo";
    try{
      Class.forName(driver).newInstance();
      con = DriverManager.getConnection(url+db, user, pass);
      try{
    	  
    	  //VYTVORENIE TABULKY
    	createTable(con);
    	System.out.println("TABLE_RUSKA CREATED");
    	
    	// 100 RANDOM AUT S RANDOM FARBOU PRIDANE DO DB
    	// POKROCILE FUNCKIA JDBC TRANSACTION, PREPARED STATEMENT, COMMIT, ROLLBACK
      	insertRandomsJPA(con);
      	// KLASICKY INSERT
        //insertRandoms(con);
      	System.out.println("RANDOM 100 DATA INSERTED");
    	
      	//VYPIS CERVEN A ZLTE AUTA
      	System.out.println("YELLOW OR RED CARS:");
      	System.out.println(getYellowOrRedCars(con));
      	
      	//PREFARBI TIETO AUTA NA CIERNU
      	System.out.println("REPAINT YELLOW AND RED CARS");
      	paintYellowAndRedCarsToBlack(con);
      	
      	// VYMAZ OSTATNE AUTA
      	deleteOtherCars(con);
      	System.out.println("DELETE OTHER CARS");
      	
      	//VYPIS OSTAVAJUCE AUTA
      	System.out.println("Remaining cars count: " + countReimainingRows(con));
    	
      	//ZRUS TABULKU
      	dropTable(con);
    	System.out.println("TABLE_RUSKA DROPED");
    	
    	
        
        con.close();
      }
      catch (SQLException s){
    	  s.printStackTrace();
        System.out.println("SQL code does not execute.");
      }    
    }
    catch (Exception e){
      e.printStackTrace();
    }
  }
  
  
  public static void createTable(Connection con) throws SQLException {
	  Statement st = con.createStatement();
	  String sql = "CREATE TABLE TAB_RUSKA " +
              "(id INTEGER not NULL, " +
              " car VARCHAR(255), " + 
              " color VARCHAR(255), " + 
              " PRIMARY KEY ( id ))";
      st.executeUpdate(sql);
  }
  
  public static void dropTable(Connection con) throws SQLException {
	  Statement st = con.createStatement();
	  String sql = "DROP TABLE TAB_RUSKA ";
      st.executeUpdate(sql);
  }
  
  public static void insertRandoms(Connection con) throws SQLException {
	  for(int i = 1; i <= 100; i++) {
		  Statement st = con.createStatement();
		  String car = cars[rnd.nextInt(cars.length)];
		  String color = colors[rnd.nextInt(colors.length)];
		  String sql = "INSERT INTO TAB_RUSKA " +
                  "VALUES("+i+", '"+ car +"', '"+ color +"')";
		  st.executeUpdate(sql);
	  }
  }
  
  public static String getYellowOrRedCars(Connection con) throws SQLException {
	  Statement st = con.createStatement();
	  String res = "";
	  String sql = "SELECT * FROM TAB_RUSKA WHERE color = 'yellow' OR color = 'red'";
	  yellowAndRedCars = st.executeQuery(sql);
	  
	  while ( yellowAndRedCars.next()) {
		    String id   =  yellowAndRedCars.getString(1);
		    ids.add(id);
		    String car =  yellowAndRedCars.getString(2);
		    String color =  yellowAndRedCars.getString(3);
		    String all = id + " " + car + " " + color + "\n";
		    res+= all;
	  }
	  
	  return res;
  }
  
  public static void paintYellowAndRedCarsToBlack(Connection con) throws SQLException {
	  Statement st = con.createStatement();
	  for(String id : ids) {
		  String sql = "UPDATE TAB_RUSKA " +
                  "SET color = 'black' WHERE id = "+ id;
		  st.executeUpdate(sql);
	  }
  }
  
  public static void deleteOtherCars(Connection con) throws SQLException {
	  Statement st = con.createStatement();
	  for(int i = 1; i <= 100; i++) {
		  if(!ids.contains(i+"")) {
			  String sql = "DELETE FROM TAB_RUSKA " +
	                  "WHERE id = "+ i;
			  st.executeUpdate(sql);
		  } 
	  }
  }
  
  public static int countReimainingRows(Connection con) throws SQLException {
	  Statement st = con.createStatement();
	  int count = 0;
	  String sql = "SELECT * FROM TAB_RUSKA";
	  ResultSet rs = st.executeQuery(sql);
	  
	  while ( rs.next()) {
		  count++;
	  }
	  
	  return count;
  }
  
  public static void insertRandomsJPA(Connection con){
	  PreparedStatement insertRandom = null;
	  String dbname = "TAB_RUSKA";
	  String sql = "INSERT INTO "+ dbname +
			  " VALUES(?,?,?)";
	  
	  try {
		  con.setAutoCommit(false);
		  insertRandom = con.prepareStatement(sql);
		  for(int i = 1; i <= 100; i++) {
			  insertRandom.setInt(1, i);
			  insertRandom.setString(2, cars[rnd.nextInt(cars.length)]);
			  insertRandom.setString(3, colors[rnd.nextInt(colors.length)]);
			  insertRandom.execute();
			  con.commit();
		 }
	  }
	  catch(Exception e) {
	        if (con != null) {
	            try {
	                System.err.print("Transaction is being rolled back");
	                con.rollback();
	            } catch(SQLException excep) {
	            	excep.printStackTrace();
	            }
	        }
	  }finally {
		  if(insertRandom != null)
			try {
				insertRandom.close();
				con.setAutoCommit(true);
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	  }
	  
  }
}
