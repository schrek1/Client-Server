package Klient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.*;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Ondrej
 */
public class Klient{

  private static final boolean LOGGING = false;

  private final int DEFAULT_PORT = 10997;
  private final String LOOPBACK_ADDRESS = InetAddress.getLoopbackAddress().getHostAddress();

  private Socket socket;
  private InetSocketAddress server;
  private BufferedReader fromServer;
  private PrintStream toServer;
  private BufferedReader keyboardInput;

  public Klient(){
    this.init(LOOPBACK_ADDRESS, DEFAULT_PORT);
  }

  public Klient(String address, int port){
    this.init(address, port);
  }

  private void init(String address, int port){
    try{
      this.server = new InetSocketAddress(address, port);
      this.socket = new Socket();
      this.socket.connect(server);
      this.fromServer = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
      this.toServer = new PrintStream(this.socket.getOutputStream());
      this.keyboardInput = new BufferedReader(new InputStreamReader(System.in));
    }catch(IOException ex){
      System.err.println("Nepripojeno!");
    }
  }

  public void run(){
    try{
      String recieveMessage;
      while((recieveMessage = this.fromServer.readLine()) != null){
        if(recieveMessage.startsWith("/")){
          this.performAction(recieveMessage);
        }else{
          System.out.println(recieveMessage);
        }
      }
    }catch(Exception ex){
      if(LOGGING){
        Logger.getLogger(Klient.class.getName()).log(Level.SEVERE, null, ex);
      }
    }finally{
      this.closeConnections();
    }
  }

  private void performAction(String command){
    switch(command){
      case "/username":
        this.sendUsername();
        break;
      case "/password":
        this.sendPassword();
        break;
      default:
        throw new IllegalArgumentException("Neplatna akce");
    }
  }

  private void sendPassword(){
    try{
      String passwordHash;
      System.out.print(">Zadejte heslo: ");
      //TODO zprovoznit readPassword
      //password = Arrays.toString(System.console().readPassword());
      passwordHash = this.textToMD5(this.keyboardInput.readLine());
      this.toServer.println(passwordHash);
    }catch(Exception ex){
      if(LOGGING){
        Logger.getLogger(Klient.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
  }

  private void sendUsername(){
    try{
      System.out.print(">Zadejte uzivatelske jmeno: ");
      String username = this.keyboardInput.readLine();
      this.toServer.println(username);
    }catch(IOException ex){
      if(LOGGING){
        Logger.getLogger(Klient.class.getName()).log(Level.SEVERE, null, ex);
      }
      System.err.println("Spatny vstup!");
    }
  }

  private String textToMD5(String text){
    try{
      java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
      byte[] array = md.digest(text.getBytes());
      StringBuffer sb = new StringBuffer();
      for(int i = 0; i < array.length; ++i){
        sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
      }
      return sb.toString();

    }catch(java.security.NoSuchAlgorithmException e){
      if(LOGGING){
        Logger.getLogger(Klient.class.getName()).log(Level.SEVERE, null, e);
      }
    }
    return null;
  }

  private void closeConnections(){
    try{
      System.out.println(">Spojeni bylo ukonceno.");
      if(this.socket != null){
        this.socket.close();
      }
    }catch(IOException ex){
      if(LOGGING){
        Logger.getLogger(Klient.class.getName()).log(Level.SEVERE, null, ex);
      }
    }
  }

  public static void main(String[] args){
    new Klient().run();
  }

}
