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
    	createTable(con);
    	System.out.println("TABLE_RUSKA CREATED");
      	insertRandoms(con);
      	System.out.println("RANDOM 100 DATA INSERTED");
    	System.out.println("YELLOW OR RED CARS:");
      	System.out.println(getYellowOrRedCars(con));
      	paintYellowAndRedCarsToBlack(con);
      	System.out.println("REPAINT YELLOW AND RED CARS");
      	deleteOtherCars(con);
      	System.out.println("DELETE OTHER CARS");
      	System.out.println("Remaining cars count: " + countReimainingRows(con));
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
}
