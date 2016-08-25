package Server;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ondrej
 */
public class DataSource{

  private final String HOST = "db4free.net";
  private final String DB_NAME = "testschrek";
  private final String USER = "schrek";
  private final String PASS = "Password1*";
  private final String URL = "jdbc:mysql://" + HOST + ":3306/" + DB_NAME;

  private Connection connection;
  private static DataSource instance = null;

  private DataSource(){
    this.controlerReady();
    this.connectToDb();
  }

  public static DataSource getConection(){
    if(instance == null){
      return new DataSource();
    }
    return instance;
  }

  private void controlerReady(){
    try{
      Class.forName("com.mysql.jdbc.Connection");
    }catch(ClassNotFoundException ex){
      Logger.getLogger(DataSource.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  private void connectToDb(){
    try{
      this.connection = DriverManager.getConnection(URL, USER, PASS);
    }catch(SQLException ex){
      Logger.getLogger(DataSource.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  private Statement getStatement(){
    Statement st = null;
    try{
      st = this.connection.createStatement();
      return st;
    }catch(SQLException ex){
      Logger.getLogger(DataSource.class.getName()).log(Level.SEVERE, null, ex);
    }
    return null;
  }

  /**
   * @return MD5 hash password if not found user -> ""
   */
  public synchronized String getPassByUsername(String username){
    ResultSet result;
    try{
      result = this.getStatement().executeQuery("SELECT pass FROM accounts WHERE username = '" + username + "'");
      if(result.next()){
        return result.getString("pass");
      }
    }catch(SQLException ex){
      Logger.getLogger(DataSource.class.getName()).log(Level.SEVERE, null, ex);
    }
    return "";
  }

  public void prepareDB(){
    try{
      this.getStatement().execute("DROP TABLE IF EXISTS accounts");
      this.getStatement().execute("CREATE TABLE accounts(username varchar(20), pass varchar(70))");
      this.getStatement().execute("INSERT INTO accounts VALUES('jan', 'fa27ef3ef6570e32a79e74deca7c1bc3')");
      this.getStatement().execute("INSERT INTO accounts VALUES('karel', '2cd324f30dc548396570da4e637c53ee')");
      this.getStatement().execute("INSERT INTO accounts VALUES('petr', '2f0714f5365318775c8f50d720a307dc')");
      this.getStatement().execute("INSERT INTO accounts VALUES('admin', '21232f297a57a5a743894a0e4a801fc3')");
    }catch(SQLException ex){
      Logger.getLogger(DataSource.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  public void closeConection(){
    try{
      this.connection.close();
    }catch(SQLException ex){
      Logger.getLogger(DataSource.class.getName()).log(Level.SEVERE, null, ex);
    }
  }

  public static void main(String[] args){
    DataSource db = DataSource.getConection();
//    System.out.println(db.getPassByUsername("admin"));
  }

}
