/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Klient;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Ondrej
 */
public class HashTest{

  public static String hashToMD5(String text){
    try{
      java.security.MessageDigest md = java.security.MessageDigest.getInstance("MD5");
      byte[] array = md.digest(text.getBytes());
      StringBuffer sb = new StringBuffer();
      for(int i = 0; i < array.length; ++i){
        sb.append(Integer.toHexString((array[i] & 0xFF) | 0x100).substring(1, 3));
      }
      return sb.toString();
    }catch(java.security.NoSuchAlgorithmException e){
    }
    return null;
  }

  public HashTest(){
  }

  @BeforeClass
  public static void setUpClass(){
  }

  @AfterClass
  public static void tearDownClass(){
  }

  @Before
  public void setUp(){

  }

  @After
  public void tearDown(){
  }

  @Test
  public void testHashMD5(){
    String hash;
    hash = hashToMD5("jan");
    assertEquals(hash, "fa27ef3ef6570e32a79e74deca7c1bc3");
    hash = hashToMD5("karel");
    assertEquals(hash, "2cd324f30dc548396570da4e637c53ee");
    hash = hashToMD5("petr");
    assertEquals(hash, "2f0714f5365318775c8f50d720a307dc");
    hash = hashToMD5("admin");
    assertEquals(hash, "21232f297a57a5a743894a0e4a801fc3");
  }

}
