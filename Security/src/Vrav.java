


import java.applet.Applet;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.crypto.Cipher;

import security.KeyGenerator;

public class Vrav extends Applet implements Runnable
{
	private static final long serialVersionUID = -4297335882692216363L;

	Socket socket;
	TextArea t1 = new TextArea();
	Thread th;
	private static final int port = 2004;
	public int counter;
	ServerSocket srv;
	
	PublicKey serverPublicKey;
	PrivateKey serverPrivateKey;
	PublicKey clientPublicKey;
	PrivateKey clientPrivateKey;
	
	
	PublicKey serverPublicSignKey;
	PrivateKey serverPrivateSignKey;
	PublicKey clientPublicSignKey;
	PrivateKey clientPrivateSignKey;
	

	HashMap<Integer,PublicKey> clientKeys = new HashMap<>();
	HashMap<Integer,ClientHandler> clients = new HashMap<>();
	HashMap<Integer,TextArea> textAreas = new HashMap<>();
	
	OutputStream wr; 
	InputStream rd;
	
	@Override
	public void init()
	{
		add(t1); 
		Thread th = new Thread(this);
		th.start();
		
	}
	
	void listeners() {
		t1.addTextListener(new TextListener() {
			
			@Override
			public void textValueChanged(TextEvent arg0) {
				// TODO Auto-generated method stub
				String msg = t1.getText();
				sendMsg(msg);
			}
		});
		
	}
	
	private void sendMsg(String msg) {
			if(srv == null) {
				byte bts[] = encodeString(msg + sign("".getBytes(),serverPrivateSignKey), serverPublicKey);
				try {
					synchronized(wr){
				    wr.write(bts.length & 255);
				    wr.write(bts.length >> 8);
				    wr.write(bts, 0, bts.length);
				    wr.flush();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}else {
				for(Entry<Integer,ClientHandler> c: clients.entrySet()) {
					c.getValue().sendMsg(encodeString("T 0 " + msg + sign("".getBytes(),serverPrivateSignKey),clientKeys.get(c.getKey())));
				}
				
			}
	}
	
	
	private void waitingForConnection() throws IOException
	{
			Socket s = srv.accept();
			counter++;
		    ClientHandler client = new ClientHandler(this, s, counter);
			clients.put(counter,client);
		    TextArea clientArea = new TextArea();
		    clientArea.setEditable(false);
		    textAreas.put(counter, clientArea);
		    //System.out.println("client" + counter +" connected");
		    add(clientArea);
		    this.doLayout();
			new Thread(client).start();
			
	}
	
	public void addAreaToClients(int id) {
		clients.get(id).addTextArea(encodeString("A 0", clientKeys.get(id)));
		for(Entry<Integer,ClientHandler> c: clients.entrySet()) {
			if(id != c.getKey()) {
				c.getValue().addTextArea(encodeString("A " +id, clientKeys.get(c.getKey())));
				clients.get(id).addTextArea(encodeString("A "+c.getKey(), clientKeys.get(id)));
			}
		}
	}

	public void run() 
	{
		try{
			  socket = new Socket("localhost", port);
			  wr = socket.getOutputStream();
			  rd = socket.getInputStream();
			  serverPublicKey = KeyGenerator.getPublicKeyFromFile();
			  serverPublicSignKey = KeyGenerator.getPublicSignKeyFromFile();
			
			  KeyPair mainKeyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair();
			  clientPrivateKey = mainKeyPair.getPrivate();
			  clientPublicKey = mainKeyPair.getPublic();
			  sleep(100);
			  
			  sendClientPublickKey();
			  listeners();
			  while(getMsg());
		}catch(Exception e){
			try {
				srv = new ServerSocket(port);
				listeners();
				serverPrivateKey = KeyGenerator.getPrivateKeyFromFile();
				serverPrivateSignKey = KeyGenerator.getPrivateSignKeyFromFile();
				//System.out.println("ServerClientCreated");
				while(true){
					waitingForConnection();
					//System.out.println("waiting again client connected");
				}
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				//e1.printStackTrace();
			}
			
		}
		
		
	}
	
	public boolean getMsg()
	{			
		try{String str;
			synchronized(rd) {
				str = readAndcreateString();
			}
		if(str.length() > 0) {
			
				String[] msg = str.split(" ");
				int id;
			
				switch(msg[0]) {
				case "A":
					id = Integer.parseInt(msg[1]);
					TextArea area = new TextArea();
					area.setEditable(false);
					add(area);
					doLayout();
					textAreas.put(id, area);
					break;
				case "R":
					id = Integer.parseInt(msg[1]);
					remove(textAreas.get(id));
					textAreas.remove(id);
					doLayout();
					break;
				case "T":
					//System.out.println(msg.length);
					id = Integer.parseInt(msg[1]);
					String text = msg[2];
					textAreas.get(id).setText(text);
					break;
				default:
					return false;
				}
				
		}}catch(Exception e) {
		}
		return true;
	}
	
	public void sendMsgForAll(int id, byte[] text) {
		String t = decodeString(text, serverPrivateKey);
		String msg = "T " + id + " " +  t;
		textAreas.get(id).setText(t);
		textAreas.get(id).doLayout();
		for(Entry<Integer,ClientHandler> c: clients.entrySet()) {
				if(id != c.getKey()) {
					c.getValue().sendMsg(encodeString(msg, clientKeys.get(c.getKey())));
				}
		}
	}
	

	
	
	public void removeClient(int id) {
		clients.remove(id);
		remove(textAreas.get(id));
		textAreas.remove(id);
		for(Entry<Integer,ClientHandler> c: clients.entrySet()) {
			c.getValue().removeTextArea(encodeString("R " + id, clientKeys.get(c.getKey())));
		}
	}
	
	
	public String readAndcreateString()
	{
		try {
			int nbts = rd.read() + (rd.read() << 8);
			byte bts[] = new byte[nbts];
			int i = 0; // how many bytes did we read so far
			do {
				int j = rd.read(bts, i, bts.length - i);
				if (j > 0) i += j;
				else break;
			} while (i < bts.length);
			String x = decodeString(bts, clientPrivateKey);
			if(!x.equals("")) return x;
			return new String(bts);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	
	private byte[] encodeString(String input, PublicKey key) {
		try {
			Cipher crypto = Cipher.getInstance("RSA");
			crypto.init(Cipher.ENCRYPT_MODE, key);
			return crypto.doFinal(input.getBytes());
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	
	private String decodeString(byte[] br, PrivateKey key) {
		try{
			Cipher crypto = Cipher.getInstance("RSA");
			crypto.init(Cipher.DECRYPT_MODE, key);
			return new String(crypto.doFinal(br));
		}catch(Exception e) {
			//e.printStackTrace();
		}
		return "";
	}
	
	private void sendClientPublickKey() {
		 X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(
					clientPublicKey.getEncoded());
		byte[] bts = x509EncodedKeySpec.getEncoded();
		try {
				synchronized(wr){
			    wr.write(bts.length & 255);
			    wr.write(bts.length >> 8);
			    wr.write(bts, 0, bts.length);
			    wr.flush();
				}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public void addClientWithKey(int id, byte[] b) {
    	PublicKey publicKey = null;
		try {
			X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(
					b);
			publicKey = KeyFactory.getInstance("RSA").generatePublic(x509EncodedKeySpec);
		} catch (Exception e) {
			e.printStackTrace();
		}
    	clientKeys.put(id, publicKey);
	}
	
	private void sleep(int time) 
	{
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static String sign(byte[] plainText, PrivateKey privateKey){
	    try{
	    	Signature privateSignature = Signature.getInstance("NONEwithRSA");
	    	privateSignature.initSign(privateKey);
		    privateSignature.update(plainText);

		    byte[] signature = privateSignature.sign();
		    return Base64.getEncoder().encodeToString(signature);
	    }
	    catch(Exception e) {
	    	e.printStackTrace();
	    }
		return null;
	    
	}
	
	public static boolean verify(String plainText, String signature, PublicKey publicKey) throws Exception {
	    Signature publicSignature = Signature.getInstance("NONEwithRSA");
	    publicSignature.initVerify(publicKey);
	    publicSignature.update(plainText.getBytes());

	    byte[] signatureBytes = Base64.getDecoder().decode(signature);

	    return publicSignature.verify(signatureBytes);
	}
}
