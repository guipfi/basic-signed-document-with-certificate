import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.cert.X509Certificate;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.Scanner;

public class create {
  public static void main(String[] args) {
    // Filename wich contains the data to be signed
    String fileName;
    // The file wich contains the private key(.p12 file)
    String key;
    // The password for the key
    String password;
    // Who signed the document
    String author;
    // Algorithm used to criptograph the signature
    String algorithm="RSA";
    // Algorithm used to hash
    String hash="SHA1";

    System.out.println("Insert the file path where data is located(example: nossosNomes.txt -- if is in the same path as the jar file");
    Scanner s = new Scanner(System.in);
    fileName = s.nextLine();
    System.out.println("Insert the file path where the key is located(example: MyKey.p12 -- if is in the same path as the jar file");
    key = s.nextLine();
    System.out.println("Insert the key password");
    password = s.nextLine();
    s.close();

    try {
      // Get the PKCS12 key from the file
      KeyStore keyStore = KeyStore.getInstance("PKCS12");
      keyStore.load(new FileInputStream(key), password.toCharArray());
      // Get the alias name inside the certificate
      String alias = keyStore.aliases().nextElement();
      // Getting the private key providing alias and password
      PrivateKey privateKey = (PrivateKey)keyStore.getKey(alias, password.toCharArray());
      X509Certificate cert = (X509Certificate)keyStore.getCertificate(alias);
      // Get the author(who will sign the document)
      author = cert.getSubjectX500Principal().getName();
      int delimiterPosition = author.indexOf(",");
      author = String.copyValueOf(author.toCharArray(), 3, delimiterPosition-3);
      // Create a signature with private key and the data
      Signature signature = Signature.getInstance(hash+"with"+algorithm);
      signature.initSign(privateKey);
      byte[] messageBytes = Files.readAllBytes(Paths.get(fileName));
      signature.update(messageBytes);
      byte[] digitalSignature = signature.sign();

      // Write results to the file
      int lastIndex = fileName.indexOf("."); 
      String signedFileName = fileName.substring(0, lastIndex); 
      FileWriter result = new FileWriter(signedFileName+"-signed.txt");
      result.write("-----BEGIN DOCSIGNED-----\n");
      result.write("doc:"+fileName+"\n");
      result.write("alg:"+algorithm+"\n");
      result.write("hash:"+hash+"\n");
      result.write("assinante:"+author+"\n\n");
      result.write("-----BEGIN DOC-----\n");
      result.write(new String(Base64.getEncoder().encode(messageBytes))+"\n");
      result.write("-----END DOC-----\n");
      result.write("-----BEGIN SIGNATURE-----\n");
      result.write(new String(Base64.getEncoder().encode(digitalSignature))+"\n");
      result.write("-----END SIGNATURE-----\n");
      result.write("-----END DOCSIGNED-----\n");
      result.close();
      System.out.println("Successfully signed the file");
    } catch(Exception e) {
      System.out.println("An error occurred");
      e.printStackTrace();
    }
  }
}
