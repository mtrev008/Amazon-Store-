/*
 * Template JAVA User Interface
 * =============================
 *
 * Database Management Systems
 * Department of Computer Science &amp; Engineering
 * University of California - Riverside
 *
 * Target DBMS: 'Postgres'
 *
 */


import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.List;
import java.util.ArrayList;
import java.lang.Math;
import javax.swing.*;

/**
 * This class defines a simple embedded SQL utility class that is designed to
 * work with PostgreSQL JDBC drivers.
 *
 */
public class Amazon {
	
   public static String currUser;
   // reference to physical database connection.
   private Connection _connection = null;

   // handling the keyboard inputs through a BufferedReader
   // This variable can be global for convenience.
   static BufferedReader in = new BufferedReader(
                                new InputStreamReader(System.in));

   /**
    * Creates a new instance of Amazon store
    *
    * @param hostname the MySQL or PostgreSQL server hostname
    * @param database the name of the database
    * @param username the user name used to login to the database
    * @param password the user login password
    * @throws java.sql.SQLException when failed to make a connection.
    */

   public Amazon(String dbname, String dbport, String user, String passwd) throws SQLException {

      System.out.print("Connecting to database...");
      try{
         // constructs the connection URL
         String url = "jdbc:postgresql://localhost:" + dbport + "/" + dbname;
         System.out.println ("Connection URL: " + url + "\n");

         // obtain a physical connection
         this._connection = DriverManager.getConnection(url, user, passwd);
         System.out.println("Done");
      }catch (Exception e){
         System.err.println("Error - Unable to Connect to Database: " + e.getMessage() );
         System.out.println("Make sure you started postgres on this machine");
         System.exit(-1);
      }//end catch
   }//end Amazon

   // Method to calculate euclidean distance between two latitude, longitude pairs. 
   public double calculateDistance (double lat1, double long1, double lat2, double long2){
      double t1 = (lat1 - lat2) * (lat1 - lat2);
      double t2 = (long1 - long2) * (long1 - long2);
      return Math.sqrt(t1 + t2); 
   }
   /**
    * Method to execute an update SQL statement.  Update SQL instructions
    * includes CREATE, INSERT, UPDATE, DELETE, and DROP.
    *
    * @param sql the input SQL string
    * @throws java.sql.SQLException when update failed
    */
   public void executeUpdate (String sql) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the update instruction
      stmt.executeUpdate (sql);

      // close the instruction
      stmt.close ();
   }//end executeUpdate

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and outputs the results to
    * standard out.
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQueryAndPrintResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and output them to standard out.
      boolean outputHeader = true;
      while (rs.next()){
		 if(outputHeader){
			for(int i = 1; i <= numCol; i++){
			System.out.print(rsmd.getColumnName(i) + "\t");
			}
			System.out.println();
			outputHeader = false;
		 }
         for (int i=1; i<=numCol; ++i)
            System.out.print (rs.getString (i) + "\t");
         System.out.println ();
         ++rowCount;
      }//end while
      stmt.close ();
      return rowCount;
   }//end executeQuery

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the results as
    * a list of records. Each record in turn is a list of attribute values
    *
    * @param query the input query string
    * @return the query result as a list of records
    * @throws java.sql.SQLException when failed to execute the query
    */
   public List<List<String>> executeQueryAndReturnResult (String query) throws SQLException {
      // creates a statement object
      Statement stmt = this._connection.createStatement ();

      // issues the query instruction
      ResultSet rs = stmt.executeQuery (query);

      /*
       ** obtains the metadata object for the returned result set.  The metadata
       ** contains row and column info.
       */
      ResultSetMetaData rsmd = rs.getMetaData ();
      int numCol = rsmd.getColumnCount ();
      int rowCount = 0;

      // iterates through the result set and saves the data returned by the query.
      boolean outputHeader = false;
      List<List<String>> result  = new ArrayList<List<String>>();
      while (rs.next()){
        List<String> record = new ArrayList<String>();
		for (int i=1; i<=numCol; ++i)
			record.add(rs.getString (i));
        result.add(record);
      }//end while
      stmt.close ();
      return result;
   }//end executeQueryAndReturnResult

   /**
    * Method to execute an input query SQL instruction (i.e. SELECT).  This
    * method issues the query to the DBMS and returns the number of results
    *
    * @param query the input query string
    * @return the number of rows returned
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int executeQuery (String query) throws SQLException {
       // creates a statement object
       Statement stmt = this._connection.createStatement ();

       // issues the query instruction
       ResultSet rs = stmt.executeQuery (query);

       int rowCount = 0;

       // iterates through the result set and count nuber of results.
       while (rs.next()){
          rowCount++;
       }//end while
       stmt.close ();
       return rowCount;
   }

   /**
    * Method to fetch the last value from sequence. This
    * method issues the query to the DBMS and returns the current
    * value of sequence used for autogenerated keys
    *
    * @param sequence name of the DB sequence
    * @return current value of a sequence
    * @throws java.sql.SQLException when failed to execute the query
    */
   public int getCurrSeqVal(String sequence) throws SQLException {
	Statement stmt = this._connection.createStatement ();

	ResultSet rs = stmt.executeQuery (String.format("Select currval('%s')", sequence));
	if (rs.next())
		return rs.getInt(1);
	return -1;
   }

   /**
    * Method to close the physical connection if it is open.
    */
   public void cleanup(){
      try{
         if (this._connection != null){
            this._connection.close ();
         }//end if
      }catch (SQLException e){
         // ignored.
      }//end try
   }//end cleanup

   /**
    * The main execution method
    *
    * @param args the command line arguments this inclues the <mysql|pgsql> <login file>
    */
   public static void main (String[] args) {
      if (args.length != 3) {
         System.err.println (
            "Usage: " +
            "java [-classpath <classpath>] " +
            Amazon.class.getName () +
            " <dbname> <port> <user>");
         return;
      }//end if

      Greeting();
      Amazon esql = null;
      try{
         // use postgres JDBC driver.
         Class.forName ("org.postgresql.Driver").newInstance ();
         // instantiate the Amazon object and creates a physical
         // connection.
         String dbname = args[0];
         String dbport = args[1];
         String user = args[2];
         esql = new Amazon (dbname, dbport, user, "");
	


         boolean keepon = true;
         while(keepon) {
            // These are sample SQL statements
            System.out.println("MAIN MENU");
            System.out.println("---------");
            System.out.println("1. Create user");
            System.out.println("2. Log in");
            System.out.println("9. < EXIT");
            String authorisedUser = null;
            switch (readChoice()){
               case 1: CreateUser(esql); break;
               case 2: authorisedUser = LogIn(esql); break;
               case 9: keepon = false; break;
               default : System.out.println("Unrecognized choice!"); break;
            }//end switch
            if (authorisedUser != null) {
              boolean usermenu = true;
              while(usermenu) {
                System.out.println("MAIN MENU");
                System.out.println("---------");
                System.out.println("1. View Stores within 30 miles");
                System.out.println("2. View Product List");
                System.out.println("3. Place a Order");
                System.out.println("4. View 5 recent orders");

                //the following functionalities basically used by managers
                System.out.println("5. Update Product");
                System.out.println("6. View 5 recent Product Updates Info");
                System.out.println("7. View 5 Popular Items");
                System.out.println("8. View 5 Popular Customers");
                System.out.println("9. Place Product Supply Request to Warehouse");
		System.out.println("10. Access Admin Operations");

                System.out.println(".........................");
                System.out.println("20. Log out");
                switch (readChoice()){
                   case 1: viewStores(esql); break;
                   case 2: viewProducts(esql); break;
                   case 3: placeOrder(esql); break;
                   case 4: viewRecentOrders(esql); break;
                   case 5: updateProduct(esql); break;
                   case 6: viewRecentUpdates(esql); break;
                   case 7: viewPopularProducts(esql); break;
                   case 8: viewPopularCustomers(esql); break;
                   case 9: placeProductSupplyRequests(esql); break;
		   case 10: Admin(esql); break;
	
                   case 20: usermenu = false; break;
                   default : System.out.println("Unrecognized choice!"); break;
                }
              }
            }
         }//end while
      }catch(Exception e) {
         System.err.println (e.getMessage ());
      }finally{
         // make sure to cleanup the created table and close the connection.
         try{
            if(esql != null) {
               System.out.print("Disconnecting from database...");
               esql.cleanup ();
               System.out.println("Done\n\nBye !");
            }//end if
         }catch (Exception e) {
            // ignored.
         }//end try
      }//end try
   }//end main

   public static void Greeting(){
      System.out.println(
         "\n\n*******************************************************\n" +
         "              User Interface      	               \n" +
         "*******************************************************\n");
   }//end Greeting

   /*
    * Reads the users choice given from the keyboard
    * @int
    **/
   public static int readChoice() {
      int input;
      // returns only if a correct value is given.
      do {
         System.out.print("Please make your choice: ");
         try { // read the integer, parse it and break.
            input = Integer.parseInt(in.readLine());
            break;
         }catch (Exception e) {
            System.out.println("Your input is invalid!");
            continue;
         }//end try
      }while (true);
      return input;
   }//end readChoice

   /*
    * Creates a new user
    **/
   public static void CreateUser(Amazon esql){
      try{
         System.out.print("\tEnter name: ");
         String name = in.readLine();
         System.out.print("\tEnter password: ");
         String password = in.readLine();
         System.out.print("\tEnter latitude: ");   
         String latitude = in.readLine();       //enter lat value between [0.0, 100.0]
         System.out.print("\tEnter longitude: ");  //enter long value between [0.0, 100.0]
         String longitude = in.readLine();
         
         String type="Customer";

			String query = String.format("INSERT INTO USERS (name, password, latitude, longitude, type) VALUES ('%s','%s', %s, %s,'%s')", name, password, latitude, longitude, type);

         esql.executeUpdate(query);
         System.out.println ("User successfully created!");
      }catch(Exception e){
         System.err.println (e.getMessage ());
      }
   }//end CreateUser


   /*
    * Check log in credentials for an existing user
    * @return User login or null is the user does not exist
    **/
   public static String LogIn(Amazon esql){
      try{
         System.out.print("\tEnter name: ");
         String name = in.readLine();
         System.out.print("\tEnter password: ");
         String password = in.readLine();
         String query = String.format("SELECT * FROM USERS WHERE name = '%s' AND password = '%s'", name, password);
	 List<List<String>> userList = esql.executeQueryAndReturnResult(query);
         int userNum = esql.executeQuery(query);
	 currUser = userList.get(0).get(0);
	 if (userNum > 0){
		return name;
	 } 
         return null;
	 //currUser = userList.get(0).get(0);
      }catch(Exception e){
         System.err.println (e.getMessage ());
         return null;
      }
   }//end

// Rest of the functions definition go in here

// Admin functionality
   public static String Admin(Amazon esql){
	   try{  
		//Check if user is admin
	   	String query = String.format("SELECT name FROM Users WHERE userID = '%s' AND type = 'admin'", currUser);
		List<List<String>> userAdmin = esql.executeQueryAndReturnResult(query);
		if (userAdmin.size() > 0){
			//View all Products
			//Get user ID and product name from currUser
			System.out.print("\tEnter user ID: ");
			String userID = in.readLine();
			System.out.print("\tEnter column name you want to update: ");
			String nameColumn = in.readLine();
			//check if nameColumn exists in Users
                        /*String check = String.format("SELECT * FROM Users WHERE " + nameColumn + "!= ' '");
                        List<List<String>> nameCheck = esql.executeQueryAndReturnResult(check);
			if(nameCheck.size() == 0){
                                System.out.println("Column name does not exist.");
                                return null;
                        }*/
			System.out.print("\tEnter new criteria: ");
			String newName = in.readLine();
			System.out.print("\tEnter store ID: ");
			String storeID = in.readLine();
			System.out.print("\tEnter product name: ");
        		String productName = in.readLine();
			System.out.print("\tEnter column name you want to update: ");
			String productColumn = in.readLine();
			//check if productColumn exists in Product
                        /*String check1 = String.format("SELECT '%s' FROM Product", productColumn);
                        List<List<String>> productCheck = esql.executeQueryAndReturnResult(check1);
			if(productCheck.size() == 0){
                                System.out.println("Column name does not exist.");
                                return null;
                        }*/
			System.out.print("\tEnter new criteria: ");
			String newProduct = in.readLine();
			String user = String.format("SELECT * FROM Users WHERE userID = '%s'", userID);
			List<List<String>> userInfo = esql.executeQueryAndReturnResult(user);
			String product = String.format("SELECT * FROM Product WHERE productName = '%s' AND storeID = '%s'", productName, storeID);
			List<List<String>> productInfo = esql.executeQueryAndReturnResult(product);
			if (userInfo.size() > 0){ //userId exists in Users
			    if (productInfo.size() > 0){ //productName exists in storeID

				//set nameColumn to newName
				String queryUser = String.format("UPDATE Users SET %s = '%s' WHERE userID = '%s'", nameColumn, newName, userID); 
				esql.executeUpdate(queryUser);
				//set productColumn to newProduct
				String queryProduct = String.format("UPDATE Product SET %s = '%s' WHERE productName = '%s'", productColumn, newProduct, productName);
				esql.executeUpdate(queryProduct);
				esql.executeQueryAndPrintResult(user);
				esql.executeQueryAndPrintResult(product);
			    }else{
				System.out.println("\nProduct does not exist in store.\n");
				return null;
			    }
			}else{
			    System.out.println("\nUserID is not valid.\n");
			    return null;
			}
		
		return "Admin operations comleted successfully.";
		}else{
			System.out.println("\nOnly admins can access admin operations.\n");
		}
           }catch(Exception e){
	    System.err.println (e.getMessage ());
	   }
	return null;
   }
   
   public static void viewStores(Amazon esql) {
	try {
        // Fetch latitude and longitude of the current user
        String query = String.format("SELECT latitude, longitude FROM Users WHERE userID = '%s'", currUser);
        List<List<String>> userLocation = esql.executeQueryAndReturnResult(query);
        if (userLocation.size() == 0) {
            System.out.println("\nUser location not found.\n");
            return;
        }
        
        double userLat = Double.parseDouble(userLocation.get(0).get(0));
        double userLong = Double.parseDouble(userLocation.get(0).get(1));
       
        //Execute query to get stores within 30 miles
        query = ("SELECT * FROM Store");
        List<List<String>> storesList = esql.executeQueryAndReturnResult(query);
        
        System.out.println("Stores within 30 miles:");
        for (int i = 0; i < storesList.size(); i++) {
	    List<String> store = storesList.get(i);
            double storeLat = Double.parseDouble(store.get(2));
            double storeLong = Double.parseDouble(store.get(3));
            double distance = esql.calculateDistance(userLat, userLong, storeLat, storeLong);
            if (distance <= 30) {
                System.out.println(store.get(1) + " (Distance: " + distance + " miles)");
            }
        }
    } catch (Exception e) {
        System.err.println(e.getMessage());
    }
   }
   public static void viewProducts(Amazon esql) {
   	try {
        //Get store ID from the user
        System.out.print("\tEnter store ID: ");
        String storeID = in.readLine();
      
	String query = String.format("SELECT * FROM Product WHERE storeID = '%s'", storeID);
        esql.executeQueryAndPrintResult(query);
    } catch (Exception e) {
        System.err.println(e.getMessage());
    }
   }
   public static void placeOrder(Amazon esql) {
   	try {
        //Get order details from the user
        System.out.print("\tEnter store ID: ");
	String storeID = in.readLine();
        System.out.print("\tEnter product name: ");
        String productName = in.readLine();
        System.out.print("\tEnter number of units: ");
        int numberOfUnits = Integer.parseInt(in.readLine());

        // Execute query to fetch store location
        String query = String.format("SELECT latitude, longitude FROM Store WHERE storeID = '%s'", storeID);
        List<List<String>> storeLocation = esql.executeQueryAndReturnResult(query);
        
        if (storeLocation.size() == 0) {
            System.out.println("\nStore not found.\n");
            return;
        }
        
        double storeLat = Double.parseDouble(storeLocation.get(0).get(0));
        double storeLong = Double.parseDouble(storeLocation.get(0).get(1));
        
        //Get user location
        query = String.format("SELECT latitude, longitude FROM Users WHERE userID = '%s'", currUser);
        List<List<String>> userLocation = esql.executeQueryAndReturnResult(query);
        
        if (userLocation.size() == 0) {
            System.out.println("\nUser location not found.\n");
            return;
        }
        
        double userLat = Double.parseDouble(userLocation.get(0).get(0));
        double userLong = Double.parseDouble(userLocation.get(0).get(1));
        
        // Calculate distance between user and store
        double distance = esql.calculateDistance(userLat, userLong, storeLat, storeLong);
        
        // Place order if within 30 miles
        if (distance <= 30) {

	    String orderNumb = String.format("SELECT MAX(orderNumber) FROM Orders");
	    List<List<String>> mostRecentOrder = esql.executeQueryAndReturnResult(orderNumb);

	    int order = Integer.parseInt(mostRecentOrder.get(0).get(0)) + 1;
	    
            query = String.format("INSERT INTO Orders (orderNumber, customerID, storeID, productName, unitsOrdered, orderTime) VALUES ('%s', '%s', '%s', '%s', '%s', CURRENT_DATE)", order, currUser, storeID, productName, numberOfUnits);
            esql.executeUpdate(query);
            System.out.println("\nOrder placed successfully!\n");
        } else {
            System.out.println("\nYou can only place orders from stores within 30 miles.\n");
        }
    } catch (Exception e) {
        System.err.println(e.getMessage());
    }	
    }
      
   public static void viewRecentOrders(Amazon esql) {
   	try {
        //Get five most recent orders for the current user
        String query = String.format("SELECT * FROM Orders WHERE customerID = '%s' ORDER BY orderTime DESC LIMIT 5", currUser);
        esql.executeQueryAndPrintResult(query);
    } catch (Exception e) {
        System.err.println(e.getMessage());
    }	   
   }
   public static void updateProduct(Amazon esql) {
   	try {
	//Check if user is manager
	String query1 = String.format("SELECT name FROM Users WHERE userID = '%s' AND type = 'manager'", currUser);
        List<List<String>> userInfo = esql.executeQueryAndReturnResult(query1);
        if (userInfo.size() > 0) {
            //String userType = userInfo.get(0).get(0);

	    //Get storeID, product name, and new price from the current user
	    System.out.print("\tEnter store ID: ");
	    String storeID = in.readLine();
            System.out.print("\tEnter product name: ");
            String productID = in.readLine();
            System.out.print("\tEnter new price: ");
            String price = in.readLine();
	    System.out.print("\tEnter number of units: ");
	    String unitsNum = in.readLine();
	    
	    double priceVar;
            try {
                priceVar = Double.parseDouble(price);
            } catch (NumberFormatException e) {
                System.out.println("\nInvalid new price.\n");
                return;
            }
	    //Check if manager manages the store
	    String query = String.format("SELECT managerID FROM Store WHERE storeID = '%s'", storeID);
            List<List<String>> result = esql.executeQueryAndReturnResult(query);
            if (result.size() > 0) {
                String managerID = result.get(0).get(0);

	        //Update new product price in Products table
                String newProduct = String.format("UPDATE Product SET numberOfUnits = %s, pricePerUnit = %s WHERE productName = '%s'", unitsNum, price, productID);
                esql.executeUpdate(newProduct);
                System.out.println("\nProduct price updated successfully!\n");
		}
	    else {
                System.out.println("\nStore not found.\n");
            }
        } else {
            System.out.println("\nOnly managers can update products.\n");
        }
    } catch (Exception e) {
        System.err.println(e.getMessage());
    } 
   }
   public static void viewRecentUpdates(Amazon esql) {
   	 try {
        //Run query to get recent product updates
        String query = "SELECT * FROM ProductUpdates ORDER BY updatedOn DESC LIMIT 5";
        esql.executeQueryAndPrintResult(query);
    } catch (Exception e) {
        System.err.println(e.getMessage());
    }
   
   }
   public static void viewPopularProducts(Amazon esql) {
   	try {
        // Execute query to retrieve popular products
        String query = "SELECT productName, COUNT(*) as orders FROM Orders GROUP BY productName ORDER BY orders DESC LIMIT 5";
        esql.executeQueryAndPrintResult(query);
    } catch (Exception e) {
        System.err.println(e.getMessage());
    }
   }
   public static void viewPopularCustomers(Amazon esql) {
   	try {
        // Execute query to retrieve popular customers 
        String query = "SELECT customerID, COUNT(*) as orders FROM Orders GROUP BY customerID ORDER BY orders DESC LIMIT 5";
        esql.executeQueryAndPrintResult(query);
    } catch (Exception e) {
        System.err.println(e.getMessage());
    }	
   }
   public static void placeProductSupplyRequests(Amazon esql) {
   	try {    
	//Check if user is a manager
	String query1 = String.format("SELECT name FROM Users WHERE userID = '%s' AND type = 'manager'", currUser);
	List<List<String>> userInfo = esql.executeQueryAndReturnResult(query1);
	if (userInfo.size() > 0) {
		String userType = userInfo.get(0).get(0);
	//Get store ID, productName, number of units needed, and warehouseID from user
        System.out.print("\tEnter store ID: ");
        String storeID = in.readLine();
        System.out.print("\tEnter product name: ");
        String productName = in.readLine();
	System.out.print("\tEnter quantity: ");
	String quantity = in.readLine();
	System.out.print("\tEnter warehouse ID: ");
	String warehouseID = in.readLine();

        //Insert supply request into ProductSupplyRequests table
        String query = String.format("INSERT INTO ProductSupplyRequests(managerID, warehouseID, storeID, productName, unitsRequested) VALUES ('%s', '%s', '%s', '%s', '%s')", currUser, warehouseID, storeID, productName, quantity);
        esql.executeUpdate(query);
	String query2 = String.format("UPDATE Product SET numberOfUnits = numberOfUnits - '%s' WHERE storeID = '%s' AND productName = '%s'", quantity, storeID, productName);
        esql.executeUpdate(query2);
	
        System.out.println("\nSupply request placed successfully!\n");	
	}else{
		System.out.println("\nOnly managers can update products.\n");
	}
   	} catch (Exception e) {
        System.err.println(e.getMessage());
    }
   }

}//end Amazon

