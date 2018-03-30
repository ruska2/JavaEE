package security;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class KeyGenerator {
	
	public static void main(String[] args) {
		new KeyGenerator().writeKeysToFiles();
		System.out.println("Keys generated to bin folder!");
	}
	
	KeyPair key;
	
	public KeyGenerator(){
		try {
			key = KeyPairGenerator.getInstance("RSA").generateKeyPair();
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private PrivateKey getPrivate(){
		return key.getPrivate();
	}
	
	private PublicKey getPublic(){
		return key.getPublic();
	}
	
	public void writeKeysToFiles(){
		try {
			writePublicKeyToFile();
			writePrivateKeyToFile();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void writePublicKeyToFile() throws IOException {
	    X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(
				getPublic().getEncoded());
	    FileOutputStream fos = new FileOutputStream("public.key");
		fos.write(x509EncodedKeySpec.getEncoded());
		fos.close();
	        
		
	}

	private void writePrivateKeyToFile() throws IOException {
		PKCS8EncodedKeySpec pkcs8EncodedKeySpec = new PKCS8EncodedKeySpec(
				getPrivate().getEncoded());
		FileOutputStream fos = new FileOutputStream("private.key");
		fos.write(pkcs8EncodedKeySpec.getEncoded());
		fos.close();
		
	}
	
	public static PublicKey getPublicKeyFromFile() {
		File filePublicKey = new File("../public.key");
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(filePublicKey);

		byte[] encodedPublicKey = new byte[(int) filePublicKey.length()];
			fis.read(encodedPublicKey);
			fis.close();
		
		X509EncodedKeySpec publicKeySpec = new X509EncodedKeySpec(
				encodedPublicKey);
		KeyFactory keyFactory;
			keyFactory = KeyFactory.getInstance("RSA");
		PublicKey publicKey = keyFactory.generatePublic(publicKeySpec);
		return publicKey;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;	
	}
	
	public static PrivateKey getPrivateKeyFromFile() {
		File filePrivateKey = new File("../private.key");
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(filePrivateKey);

		byte[] encodedPrivateKey = new byte[(int) filePrivateKey.length()];
			fis.read(encodedPrivateKey);
			fis.close();
		
		PKCS8EncodedKeySpec privateKeySpec = new PKCS8EncodedKeySpec(
					encodedPrivateKey);
		KeyFactory keyFactory;
			keyFactory = KeyFactory.getInstance("RSA");
		PrivateKey privateKey = keyFactory.generatePrivate(privateKeySpec);
		return privateKey;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;	
	}
	
	
}
