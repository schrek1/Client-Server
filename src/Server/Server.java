package Server;

import java.io.IOException;
import java.net.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ondrej
 */
public class Server{

  private static final boolean LOGGING = false;

  private final int DEFAULT_PORT = 10997;
  private ServerSocket server;

  public Server(){
    try{
      DataSource.getConection().prepareDB();
      this.server = new ServerSocket(DEFAULT_PORT);
    }catch(IOException ex){
      if(LOGGING){
        Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
  }

  void run(){
    try{
      while(true){
        Socket clientSocket = this.server.accept();
        this.makeClientConection(clientSocket);
      }
    }catch(IOException ex){
      if(LOGGING){
        Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
      }
    }finally{
      this.closeConnections();
    }
  }

  private void makeClientConection(Socket clientSocket){
    Thread newClientThread = new Thread(new ClientCore(clientSocket, this));
    newClientThread.setDaemon(true);
    newClientThread.start();
    System.out.println("Klient (" + clientSocket.getInetAddress().toString().substring(1) + ") pripojen na portech [local:" 
            + clientSocket.getLocalPort() + " | remote:" + clientSocket.getPort() + "]");
  }

  private void closeConnections(){
    if(this.server != null){
      try{
        this.server.close();
      }catch(IOException ex){
        Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
  }

  public static void main(String[] args){
    new Server().run();
  }
}
