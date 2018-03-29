package security;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.*;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class SecurityHandler {
	
	KeyPair key;
	
	public SecurityHandler(){
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
	
	
}
