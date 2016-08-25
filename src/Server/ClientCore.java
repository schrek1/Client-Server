package Server;

import java.io.IOException;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.io.*;

/**
 *
 * @author Ondrej
 */
public class ClientCore implements Runnable{

  private static final boolean LOGGING = false;

  private Socket client;
  private PrintStream toClient;
  private BufferedReader fromClient;
  private DataSource dbSource;
  private String passwordFromDB;
  private boolean connectionEstabilished;

  ClientCore(Socket clientSocket, Server parentServer){
    this.client = clientSocket;
    this.dbSource = DataSource.getConection();
    this.connectionEstabilished = true;
    try{
      this.toClient = new PrintStream(this.client.getOutputStream());
      this.fromClient = new BufferedReader(new InputStreamReader(this.client.getInputStream()));
    }catch(IOException ex){
      if(LOGGING){
        Logger.getLogger(ClientCore.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
  }

  @Override
  public void run(){
    this.toClient.println(">Pripojeno!");
    while(this.getUsername());
    while(this.getPassword());
    this.closeConection();
  }

  private boolean getPassword(){
    String inputHash = "";
    this.toClient.println("/password");
    try{
      inputHash = this.fromClient.readLine();
    }catch(IOException ex){
      if(LOGGING){
        Logger.getLogger(ClientCore.class.getName()).log(Level.SEVERE, null, ex);
      }
      this.conectionLost();
    }
    return this.equalsHashedPass(inputHash);
  }

  private boolean equalsHashedPass(String inputHash){
    String remoteAddres = this.getRemoteAddress();
    System.out.println(">" + remoteAddres + " /password ");
    if(this.passwordFromDB.equals(inputHash)){
      System.out.println(remoteAddres + " password OK");
      return false;
    }else{
      System.out.println(remoteAddres + " password BAD");
      this.toClient.println("Spatne heslo!");
      return true;
    }
  }

  private boolean getUsername(){
    String username = "";
    this.toClient.println("/username");
    try{
      username = this.fromClient.readLine();
      this.passwordFromDB = dbSource.getPassByUsername(username);
    }catch(IOException ex){
      if(LOGGING){
        Logger.getLogger(ClientCore.class.getName()).log(Level.SEVERE, null, ex);
      }
      this.conectionLost();
    }
    return this.checkReturnedPassword(username);
  }

  private boolean checkReturnedPassword(String username){
    String remoteAddres = this.getRemoteAddress();
    System.out.println(">" + remoteAddres + " /username " + username);
    if(!this.passwordFromDB.isEmpty()){
      System.out.println(remoteAddres + " username OK");
      return false;
    }else{
      System.out.println(remoteAddres + " username BAD");
      this.toClient.println("Spatne uzivatelske jmeno!");
      return true;
    }
  }

  private void conectionLost(){
    System.err.println(">" + this.getRemoteAddress() + " CONECTION LOST ");
    this.connectionEstabilished = false;
    this.closeConection();
    Thread.currentThread().stop();
  }

  private String getRemoteAddress(){
    InetSocketAddress clientAddress = (InetSocketAddress)this.client.getRemoteSocketAddress();
    return "[" + clientAddress.getHostString() + ":" + clientAddress.getPort() + "]";
  }

  private void closeConection(){
    if(this.connectionEstabilished){
      System.out.println(this.getRemoteAddress() + " Spojeni bylo ukonceno.");
    }
    try{
      if(this.fromClient != null){
        this.fromClient.close();
      }
      if(this.toClient != null){
        this.toClient.close();
      }
      if(this.client != null){
        this.client.close();
      }
    }catch(IOException ex){
      if(LOGGING){
        Logger.getLogger(ClientCore.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
  }
}
