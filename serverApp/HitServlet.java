import javax.servlet.http.*;
import javax.servlet.*;
import java.io.*;
import java.text.ParseException;
import java.net.*;
import java.sql.*;
import java.util.*;

public class HitServlet extends HttpServlet {
    private static int mCount;
    private static String message;
    private static double dbval;
    private static double dbvalStep = 0.25;
    private static double dbvalMIDDLE = 7.5;
    private static double dbvalLEFT = 5;
    private static double dbvalRIGHT = 10;
    
  
    public void doGet(HttpServletRequest request,
		      HttpServletResponse response)
	throws ServletException, IOException {

	String dbUrl="jdbc:mysql://localhost:3306/motorcontroldb?serverTimezone="+TimeZone.getDefault().getID();
	String username ="mcontroller";
	String password ="password123";
	Connection myConnection = null;

	dbval = readDB(dbUrl, username, password, myConnection);
	


	String queryStr = request.getQueryString();
	if(queryStr != null) {
	    String delimsQS = "[=]";
	    String[] tokensQS = queryStr.split(delimsQS);
	    int tokensLengthQS = tokensQS.length;
	    String paramPrev = tokensQS[tokensLengthQS - 1];
	

	    if((paramPrev.equals("left"))){
		if(dbval != 0){
		    if (dbval > dbvalLEFT) {
			dbval -= dbvalStep;
			writeDB(dbUrl, username, password, dbval, myConnection);
		    }
		    else {
			dbval = dbvalLEFT;
			writeDB(dbUrl, username, password, dbval, myConnection);
		    }
		    message  = "you pressed left, Hits: " + ++mCount + " pwm is: " + dbval;	// use response to debug servlet
		    response.setContentType("text/plain");
		    response.setContentLength(message.length());
		    ServletOutputStream out = response.getOutputStream();
		    out.println(message);
		    response.flushBuffer();
		}
		else {
		    message = "you pressed left, Hits: " + ++mCount;
		    response.setContentType("text/plain");
		    response.setContentLength(message.length());
		    ServletOutputStream out1 = response.getOutputStream();
		    out1.println(message);
		    response.flushBuffer();
		}
	  
	    } else if((paramPrev.equals("right"))){
		if(dbval != 0){
		    if (dbval < dbvalRIGHT){
			dbval += dbvalStep;
			writeDB(dbUrl, username, password, dbval, myConnection);
		    }
		    else {
			dbval = dbvalRIGHT;
			writeDB(dbUrl, username, password, dbval, myConnection);
		    }
		    message = "you pressed right, Hits: " + ++mCount + " pwm is: " + dbval;
		    response.setContentType("text/plain");
		    response.setContentLength(message.length());
		    ServletOutputStream out = response.getOutputStream();
		    out.println(message);
		    response.flushBuffer();
		}
		else {
		    message = "you pressed right, Hits: " + ++mCount;
		    response.setContentType("text/plain");
		    response.setContentLength(message.length());
		    ServletOutputStream out2 = response.getOutputStream();
		    out2.println(message);
		    response.flushBuffer();
		}
	  
	    } else {
		if (dbval != 0){
		    message = "Hits: " + ++mCount + " pwm is: " + dbval;
		    response.setContentType("text/plain");
		    response.setContentLength(message.length());
		    ServletOutputStream out = response.getOutputStream();
		    out.println(message);
		    response.flushBuffer();
		}
		else {
		    message = "Hits: " + ++mCount;
		    response.setContentType("text/plain");
		    response.setContentLength(message.length());
		    ServletOutputStream out = response.getOutputStream();
		    out.println(message);
		    response.flushBuffer();
		}
	    }
	} else {
	    if (dbval != 0){
		
		message = "Hits: " + ++mCount + " pwm is: " + dbval;
		response.setContentType("text/plain");
		response.setContentLength(message.length());
		ServletOutputStream out = response.getOutputStream();
		out.println(message);
		response.flushBuffer();
	    }
	    else {
		message = "Hits: " + ++mCount;
		response.setContentType("text/plain");
		response.setContentLength(message.length());
		ServletOutputStream out = response.getOutputStream();
		out.println(message);
		response.flushBuffer();
	    }
	}
    }	   
    
    public static double readDB(String dbUrl, String username, String password, Connection myConnection){

	double dbval = 0;
       
	try {
            Class.forName("com.mysql.cj.jdbc.Driver");
	    //Class.forName("org.mariadb.jdbc.DRIVER");
        } catch (Exception ex) {
            // handle the error
	    	System.out.println("registration failure: "+ex.getMessage());
        }
	
	try{
	   
	    // get a connection (step 1)
	    myConnection = DriverManager.getConnection(dbUrl,username,password);
	    //create a statement object (step 2)
	    Statement myStatement = myConnection.createStatement();
	    // execute Query (Step 3)
	    ResultSet myResultSet = myStatement.executeQuery("SELECT * FROM motorcontroldb.dbactual");
	    //process the result set.
	    while(myResultSet.next()){
		dbval = myResultSet.getDouble("actualval");
	    }
	}catch(SQLException e){
	    System.out.println("SQLException: " + e.getMessage());
	    System.out.println("SQLState: " + e.getSQLState());
	    System.out.println("VendorError: " + e.getErrorCode());
	}
	    
	return dbval;    
    }

    public static void writeDB(String dbUrl, String username, String password, double dbval, Connection myConnection){

	PreparedStatement addEntry = null;
	String addEntryString = "UPDATE motorcontroldb.dbactual SET actualval = " + dbval + " WHERE actualno = 1";

	try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (Exception ex) {
            // handle the error
	    	System.out.println(ex.getMessage());
        }

	try {
	    myConnection = DriverManager.getConnection(dbUrl,username,password);
	    myConnection.setAutoCommit(false);
	    addEntry = myConnection.prepareStatement(addEntryString);
	    addEntry.executeUpdate();
	    myConnection.commit();

	     if (addEntry != null) {
		addEntry.close();
		}
	    myConnection.setAutoCommit(true);
	    
	    
	} catch (SQLException e){
	    System.out.println("SQLException: " + e.getMessage());
	    System.out.println("SQLState: " + e.getSQLState());
	    System.out.println("VendorError: " + e.getErrorCode());
	    if (myConnection != null) {
		try {
		    System.err.print("Transaction is being rolled back");
		    myConnection.rollback();
		} catch(SQLException excep) {
		    System.out.println("SQLException: " + excep.getMessage());
		}
	    }
	}
	
    }
    
}
