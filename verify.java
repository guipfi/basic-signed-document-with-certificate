import java.security.Signature;
import java.io.*;
import java.io.File;
import java.io.FileNotFoundException; 
import java.util.Scanner;
import java.util.Base64;
import java.security.cert.X509Certificate;
import java.security.cert.CertificateFactory;

public class verify {
  public static void main(String[] args) {

    // Base file path where the signed document is located
    String baseFile;
    // The certificate file path (.crt file)
    String certificateFile;;
    // The document name to be generated
    String documentName;
    // Algorithm used
    String algorithm;
    // Hash used
    String hash;
    // Who signed the document
    String signedBy;
    // Document content
    byte[] documentContent;
    // Signature content
    byte[] signature;

    System.out.println("Insert the file path where data signed is located(example: nossosNomes.txt -- if is in the same path as the jar file");
    Scanner s = new Scanner(System.in);
    baseFile = s.nextLine();
    System.out.println("Insert the file path where the certificate is located(example: MyCertificate.crt -- if is in the same path as the jar file");
    certificateFile = s.nextLine();
    s.close();

    try {
      File doc = new File(baseFile);
      Scanner reader = new Scanner(doc);
      String data;
      data = reader.nextLine(); // Ignore first line
      data = reader.nextLine(); // Get document name
      documentName = String.copyValueOf(data.toCharArray(), 4, data.length()-4); // Save document name
      data = reader.nextLine(); // Get algorithm mode
      algorithm = String.copyValueOf(data.toCharArray(), 4, data.length()-4);
      data = reader.nextLine(); // Get hash mode
      hash = String.copyValueOf(data.toCharArray(), 5, data.length()-5); // Concat hash mode to algorithm mode
      data = reader.nextLine(); // Get who signed
      signedBy = String.copyValueOf(data.toCharArray(), 10, data.length()-10);
      data = reader.nextLine(); // Jump line
      data = reader.nextLine(); // Begin doc mark up
      data = reader.nextLine(); // Get document bytes (BASE64 Encoded)
      documentContent = Base64.getDecoder().decode(data); // Decode document bytes
      data = reader.nextLine(); // End doc mark up
      data = reader.nextLine(); // Begin signature mark up
      data = reader.nextLine();  // Signature
      signature = Base64.getDecoder().decode(data); // Decode signature bytes
      reader.close();
    } catch(FileNotFoundException e) {
      System.out.println("An error occurred");
      e.printStackTrace();
      return;
    }

    try {
      InputStream inStream = new FileInputStream(certificateFile);
      CertificateFactory cf = CertificateFactory.getInstance("X.509");
      X509Certificate cert = (X509Certificate)cf.generateCertificate(inStream);
      // Get who signed the certificate
      String CN = cert.getSubjectX500Principal().getName();
      int delimiterPosition = CN.indexOf(",");
      CN = String.copyValueOf(CN.toCharArray(), 3, delimiterPosition-3);
      // Verify if is the same to the document
      if(!CN.equals(signedBy)) {
        System.out.println("Erro no assinante do certificado(CN)");
        return;
      }
      // Verifying the signature
      Signature signatureVerify = Signature.getInstance(hash+"with"+algorithm);
      signatureVerify.initVerify(cert.getPublicKey());
      signatureVerify.update(documentContent);
      boolean isCorrect = signatureVerify.verify(signature);
      if(isCorrect) {
        try {
          FileWriter result = new FileWriter(documentName);
          result.write(new String(documentContent));
          result.close();
          System.out.println("Documento íntegro e assinado por "+ signedBy);
        } catch (IOException e) {
          System.out.println("An error occurred.");
          e.printStackTrace();
        }
      } else {
        System.out.println("A integridade da mensagem está comprometida.");
      }
    } catch(Exception e) {
      System.out.println("An error occurred");
      e.printStackTrace();
      return;
    }
    
    return;
  }
}