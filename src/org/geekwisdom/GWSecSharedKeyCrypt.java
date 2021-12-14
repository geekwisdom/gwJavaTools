package org.geekwisdom;
/* *************************************************************************************
' Script Name: GWSecSharedKeyCrypt
' **************************************************************************************
' @(#)    Purpose:
' @(#)    This is the GEEK WISDOM SECURITY ENCRYPTION MODULE
' @(#)    It's purpose is to implement an UPGRADABLE secure shared key
' @(#)    encrypt/decrypt algorithim based on a private key known only by
' @(#)    two parties. As new encryption algorhims come out existing ones
' @(#)    used in this module can be upgraded to more secure versions!
'         In Addition the same algorhim can be encrypt/decrypt for PHP
'	   JAVA and .NET
' TODO: Add 'reflection' ability to call 3rd party encrption ipmlementati
' Add 3rd party of Serphant and Twofish
' Add Rotating algorthim bw AES, Serpant and Twofish
' Derive SHA-512 keys and consider using WHIRLPOOL hash

' **************************************************************************************
'  Written By: Brad Detchevery
			   2274 RTE 640, Hanwell NB
'
' Created:     2020-06-22 - Initial Architecture
' 
' **************************************************************************************/
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.TimeZone;
import java.util.zip.*;

import javax.crypto.Cipher;
import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.geekwisdom.GWException;
import org.geekwisdom.GWDataTable;
import org.geekwisdom.GWDataRow;
public class GWSecSharedKeyCrypt 

{
	private GWDataTable MessageProperties;
	private String SecretKey;
	
	
	 public GWSecSharedKeyCrypt(String Password, String hashcost, String salt)
	    {
	        //NOte there are several methods for getting the application name. If the settings manager is constructued 
	        //with s specific name then this is waht we will use, otherwise will use the name of the ese
		 	
		 SecretKey secretkey;
		 long cost;
		 byte[] mkey;
	        if (hashcost  == null || salt == null )
	        {
	        MessageProperties = fetchMachineProps();	
	        }
	        
	        if (hashcost ==null ) cost=Long.parseLong(this.MessageProperties.getRow(0).get("HASHCOST"));
	        else cost=Long.parseLong(hashcost);
	        if (salt ==null ) salt=this.MessageProperties.getRow(0).get("MACHINEKEY");
	        mkey = hex2bin(salt);
	        try {
	            SecretKeyFactory skf = SecretKeyFactory.getInstance( "PBKDF2WithHmacSHA256" );
	            //( "test", salt, cost, 256 );
	            PBEKeySpec spec = new PBEKeySpec(Password.toCharArray(), mkey, (int) cost, 256);
	            //PBEKeySpec spec = new PBEKeySpec(Password.getBytes("UTF-8"), mkey, (int) cost, 256);
	            secretkey = skf.generateSecret( spec );
	            byte[] res = secretkey.getEncoded( );
	            
	            this.SecretKey = bin2hex(res);
	            
	            
    
	          
	        } catch ( NoSuchAlgorithmException | InvalidKeySpecException e ) {
	            throw new RuntimeException( e );
	        }
	    }
	 
	 private long fetchHashCost()
	 {
	 //Determine how fash this machine is and get approperiate bcrypt cost value
	 double timeTarget =  50 * 1000000; 
	 byte [] salt = this.get_random_bytes(16);
	 int cost = 7;
	 double start;
	 double end;
	 SecretKeyFactory skf;
	 PBEKeySpec spec;
	 try {
	 skf = SecretKeyFactory.getInstance( "PBKDF2WithHmacSHA256" );
	 
     //( "test", salt, cost, 256 );
     spec = new PBEKeySpec("test".toCharArray(), salt, cost, 256);
	 }
	 catch (Exception e1) { throw new RuntimeException(e1); }
	 do {
	     cost++;
	     start = System.nanoTime();
	     
	     try {
	            SecretKey key = skf.generateSecret( spec );
	            byte[] res = key.getEncoded( );
	        } catch ( InvalidKeySpecException e ) {
	            throw new RuntimeException( e );
	        }
	     end = System.nanoTime();
	     
	 } while ((end - start) < timeTarget);
	 return cost;
	 }

	 
	 private GWDataTable fetchMachineProps()
	 {
		//generate a random machine key to be used with the password
		//that is specific to this machine
		 String property = "java.io.tmpdir";
		 String tempDir = System.getProperty(property);

		 GWDataTable machineInfo = new GWDataTable();
		 String filename;
		 String dir;
		 dir=System.getenv("HOME");
		 filename = dir + File.separator + "." + "gwMACKey";
		 File f = new File(filename);
		 if (!(f.exists()))
		 {
			 dir=System.getenv("USERPROFILE");
			 filename = dir + File.separator + "." + "gwMACKey";
			 f = new File(filename);
		 }
		 if (!(f.exists()))
		 {
			 dir=tempDir;
			 filename = dir + File.separator + "." + "gwMACKey";
			 f = new File(filename);
		 }
	 
		 if (!(f.exists()))
		 {
			 long cost = this.fetchHashCost();
			 GWDataRow newrow = new GWDataRow();
			 //LinkedHashMap<String,String> newrow = new LinkedHashMap<String,String>();
			 newrow.set("HASHCOST","" + cost);
			 newrow.set("MACHINEKEY",this.createMachineKey());
			 machineInfo.add(newrow);
			 String xml_data=machineInfo.toXml();
			 //compress and store to a file
			 String sourceFile = "machine.key";
			 GWPackage zipFile = new GWPackage(filename);
			 int test = zipFile.AddFileFromString("machine.key", xml_data);
		     if (test == 0) return null;
		     
	     }
		 else
		 {
		 //read form zip file

		        GWPackage zipFile = new GWPackage(filename);
		        String xmldata = zipFile.GetStringFromFile("machine.key");
		        if (xmldata == null) return null;
		        machineInfo.loadXml(xmldata);
                
		 }
		 return machineInfo;
	 }
	 
	 private String createMachineKey()
	 {
		 byte[] bytes= this.get_random_bytes(16);
		 return bin2hex(bytes);
		 
	 }
	 
	 private String get_key(String the_method, String dateInput)
	 {
		 
		 String p1 = this.SecretKey;
			//Not peerfect - but close enough for now
			String method_to_use = this.getMethod(the_method);

			switch (the_method.toLowerCase())
			{
				case "org.geekwisdom.aes-256-cbc": return p1.substring(0, 64);
				case "org.geekwisdom.aes-128-cfb": return p1.substring(0, 32);
				case "org.geekwisdom.aes-128-cbc": return p1.substring(0, 32);
				case "org.geekwisdom.aes-192-cfb": return p1.substring(0, 48);
				case "org.geekwisdom.gw-v1": return this.getGWV1Key(dateInput);
					

			}
			GWCryptProvider test = getCryptProvider(method_to_use);
			if (test == null) return this.SecretKey.substring(0, 64); //256 bits by default!
			else return test.MakeSecretKey(this.SecretKey);

		 

	 }
	 
		private String getGWV1Key(String dateInput)
        {
			String p1 = this.SecretKey;
			try
			{ 
		    	Date date1=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'").parse(dateInput);  
		    	Calendar calendar = Calendar.getInstance();
		    	calendar.setTime(date1);
		    	int dayOfYear = calendar.get(Calendar.DAY_OF_YEAR);    
		    	int whichpart = dayOfYear % 4;
				return p1.substring(whichpart, 64+whichpart);
					}
			catch (Exception e)
            {
				return p1.substring(0, 64);
            }
		}
 
	 
	 public String encrypt (String message,String method) 
	 {
	 //$method="AES-256-CBC"
		 // returns $encrypted_message
	 	//messageformat
	 //"IV"
	 //"HMAC"
	 //"MESSAGE"
	 //"TIMESTAMP"
	 //ALOG (EG: AES-267
	 
	 GWDataRow newrow = new GWDataRow();
	 TimeZone tz = TimeZone.getTimeZone("UTC");
	 DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'"); // Quoted "Z" to indicate UTC, no timezone offset
	 df.setTimeZone(tz);
	 String now = df.format(new Date());
	 newrow.set("GW_TIMESTAMP",now);
	 String newMethodName=method;
	 int hasComma = method.indexOf(',');
	 if (hasComma < 0)
	 {
		 String className="";
		 int hasdot = method.indexOf('.');
		 if (hasdot > 0)
		 {
			 String parts [] = method.split("\\.");
			 newMethodName = method + ", " + parts[0].trim();
		 }
		 else
		     newMethodName=method;
	 }
	 newrow.set("GW_METHOD",""+ newMethodName);
	 int ivlen = this.get_iv_length(method);
	 byte [] iv = this.get_random_bytes(ivlen);
	
	 newrow.set("GW_SALT",base64_encode(iv));
	 String encKey = get_key(method,now);
	 //String p1 = hex2str(encKey);
	 byte [] key=hex2bin(encKey);
	 
	
	 
	 byte [] ciphertext = this.do_encrypt(message, newMethodName,key, iv);
	 if (ciphertext == null) return null;
	 byte[] combined = new byte[ ciphertext.length + iv.length];
	 //byte [] calcmac = hash_hmac_256(hex2str(bin2hex(ciphertext))+ hex2str(bin2hex(iv)),this.SecretKey);
	 byte [] hash = hash_hmac_256(bin2hex(ciphertext) + bin2hex(iv),this.SecretKey);
	 
	 newrow.set("GW_HMAC",bin2hex(hash));
	 
	 newrow.set("GW_MESSAGE",base64_encode(ciphertext));
	 GWDataTable result = new GWDataTable();
	 result.add(newrow);
	 return result.toXml();
	 }

	 public String decrypt(String encrypted_message)
	 {
	 //returns $message
	
	 GWDataTable messageBlock = new GWDataTable();
	 messageBlock.loadXml(encrypted_message);
	 GWDataRow messageDetails = (GWDataRow) messageBlock.getRow(0);
	 byte [] hash=hex2bin(messageDetails.get("GW_HMAC"));
     byte [] iv = base64_decode(messageDetails.get("GW_SALT"));
	 

	 String method=messageDetails.get("GW_METHOD");
	 String msg=messageDetails.get("GW_MESSAGE");
	 String timestamp=messageDetails.get("GW_TIMESTAMP");
	 byte[] ciphertext = base64_decode(msg);
     String encKey = get_key(method,timestamp);
	// String p1 = hex2bin(encKey);
	 byte [] key=hex2bin(encKey);
	  
	 String message = this.do_decrypt(ciphertext,method,key,iv);
	 if (message == null) { return null; } 
	  
	 byte [] calcmac = hash_hmac_256(bin2hex(ciphertext)+ bin2hex(iv),this.SecretKey);
	 boolean retval = Arrays.equals(calcmac,hash);
	 if (!(retval)) { return null; }
	 return message;
	 }


	 public String encrypt (String message)
	 {
		 return encrypt (message,"org.geekwisdom.gw-v1");
	 }
	 
	 private byte [] hash_hmac_256(String message, String keystr)
	 {
		 byte[] fingerprint =message.getBytes();
		 byte [] key = keystr.getBytes();
		 
		 try {
		 Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
		  SecretKeySpec secret_key = new SecretKeySpec(key, "HmacSHA256");
		  sha256_HMAC.init(secret_key);
		  return sha256_HMAC.doFinal(fingerprint);
		 }
		 catch (Exception e) { return null; }
	 }
	 private int get_iv_length(String method)
	 {
		 //Not peerfect - but close enough for now
		 String firstpart = method.substring(15,15+3);
		 switch(firstpart.toLowerCase()) 
	        { 
	            case "aes": 
	                return 16; 
	                 
	            case "cam": 
	                return 16; 
	                 
	            case "see": 
	                return 16; 
				case "gw-":
					return 16;
	                 
	            default: 
					GWCryptProvider test = getCryptProvider(method);
					if (test == null) return 8;
					else return test.get_iv_length();
	                
	        } 
		 
	 }
	 
	 private byte [] do_encrypt(String message_in, String method,byte [] key, byte [] iv)
	 {
		 
		 String message = message_in;

	      	 
		 try
		    {
			 IvParameterSpec ivspec = new IvParameterSpec(iv);
			 SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
		     String the_method = this.getMethod(method);
		     if (the_method.equals(method))
			{
		    	 //Not a native Java method
	    	 
			     GWCryptProvider mycrypt = getCryptProvider(method);
				 return mycrypt.Encrypt(message_in, key, iv);
				 }
				 
		     else
		     {
			 Cipher cipher = Cipher.getInstance(this.getMethod(method));
			 //Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
		        
		        cipher.init(Cipher.ENCRYPT_MODE, secretKey,ivspec);
		        return cipher.doFinal(message.getBytes());
		     }
		    } 
		    catch (Exception e) 
		    {
		        return null;
		    }
	 
	 }
	 
	 private String do_decrypt(byte [] ciphertext, String method, byte []key, byte [] iv)
	 {
		 
		 try
		    {
			 IvParameterSpec ivspec = new IvParameterSpec(iv);   
			 SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
		     String the_method = this.getMethod(method);
		     if (the_method.equals(method))
			{
		    	 //Not a native Java method
					GWCryptProvider test = getCryptProvider(method);
					if (test == null) return null;
					else return test.Decrypt(ciphertext, key, iv); 
			}
		     else
		     {
		        Cipher cipher = Cipher.getInstance(the_method);
		        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivspec);
		        return new String(cipher.doFinal(ciphertext));
		     }
		    } 
		    catch (Exception e) 
		    {
		        return null;
		    }
	 
	 }
	
	 
	 
	 
	 private String getMethod(String method_in)
	 {
	 //return the correct method for specific language (.NET, JAVA, PHP)
		 //"AES/CBC/PKCS5Padding"
		 //add more later!
		 switch (method_in.toLowerCase())
		 {
		 case "org.geekwisdom.aes-256-cbc": return "AES/CBC/PKCS5Padding";
		 case "org.geekwisdom.aes-128-cfb":  return "AES/CFB/PKCS5Padding";
		 case "org.geekwisdom.aes-128-cbc":  return "AES/CBC/PKCS5Padding";
		 case "org.geekwisdom.aes-192-cfb":  return "AES/CBC/PKCS5Padding";
		 case "org.geekwisdom.gw-v1": return "AES/CBC/PKCS5Padding";
		 case "org.geekwisdom.des-ede3": return "DESede/CBC/PKCS5Padding";
		 default: return method_in;
		 
		 }
		 }

	 private byte[] get_random_bytes(int size)
	 {
		 byte[] values = new byte[size];
		 SecureRandom secureRandom = new SecureRandom();
		 secureRandom.nextBytes(values);
		 return values;
	 }
	 
	 
	 private String base64_encode(byte [] input)
	 {
		 String encodedString = 
				  Base64.getEncoder().encodeToString(input);
		 return encodedString;
	 }
	 
	 private String base64_encode_str(String inputstr)
	 {
		 byte [] input;
		 try {
		  input = inputstr.getBytes("UTF-8");
		 }
		 catch (Exception e) { return null; }
		 String encodedString = 
				  Base64.getEncoder().encodeToString(input);
		 return encodedString;
	 }
	 
	 private byte [] base64_decode(String input)
	 {
		 byte [] decoded = 
				  Base64.getDecoder().decode(input);
		 return decoded;
	 }
	 
	 private String base64_decode_str(String input)
	 {
		 byte [] decoded = 
				  Base64.getDecoder().decode(input);
		 return new String(decoded);
	 }
	 
	 private static String bin2hex(byte[] bytes) 
	    
	 {
	 final char[] hexArray = "0123456789abcdef".toCharArray(); 
	char[] hexChars = new char[bytes.length * 2];
	        for ( int j = 0; j < bytes.length; j++ ) {
	            int v = bytes[j] & 0xFF;
	            hexChars[j * 2] = hexArray[v >>> 4];
	            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
	        }
	        return new String(hexChars);
	    }

	 private GWCryptProvider getCryptProvider(String fullQualifiedName)
	 {
		 String className="";
		 int hasComma = fullQualifiedName.indexOf(',');
		 if (hasComma > 0)
		 {
			 String parts [] = fullQualifiedName.split(",");
			 className = parts[0].trim();
		 }
		 else
			 className=fullQualifiedName;
		 
		 try {
		 Class c = Class.forName(className);
		 Constructor constructor  = (Constructor) c.getConstructor();
		 Object objInstance      = constructor.newInstance();
		 return (GWCryptProvider) objInstance;
		 }
		 catch (Exception e) 
		 {
		 return null;
		 }
		 
	 }
	 
private String bin2hex(String hexString) 

{
byte[] barray=null;
try {
	barray = hexString.getBytes("UTF-8");
} catch (UnsupportedEncodingException e) {
	// TODO Auto-generated catch block
	e.printStackTrace();
}
return bin2hex(barray);
   }
	
		
	private byte [] hex2bin(String s) {
	    int len = s.length();
	    byte[] data = new byte[len / 2];
	    for (int i = 0; i < len; i += 2) {
	        
	    	
	    	data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
	                             + Character.digit(s.charAt(i+1), 16));

	    }
	    return data;
	}

	private String hex2str(String s) {
	    
	    return new String (hex2bin(s));
	}

	 
	 
	 
}