package security;

import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.util.Base64;

public class SignatureHandler {
	
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
