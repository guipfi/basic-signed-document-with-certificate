# Documento Assinado - Instruções  

O projeto consiste em 2 arquivos executáveis signDocument.jar e verifySignature.jar.  

**Para gerar as classes compiladas:**    
javac create.java;   
javac verify.java;   

**Para criar o jar:**  
jar cfe signDocument.jar create create.class;  
jar cfe verifySignature.jar verify verify.class;  

**Para gerar um documento assinado é necessário primeiro criar um documento que será
assinado, por exemplo:**

**Documento:**  
exemplo.txt  
**Conteúdo:**  
Texto exemplo para assinatura.  

**Para executar o signDocument.jar:**  
java -jar signDocument.jar  

**Para assinar o documento será requisitado:**
1. O file path do arquivo a ser assinado
    exemplo: “exemplo.txt”,
2. File path que contém a chave privada(arquivo de extensão .p12)
    exemplo: “Guilherme Pereira Fantini.p12”
3. Senha da chave privada
    exemplo: “Seguranca”(para o meu certificado)
Com isso, será gerado um arquivo com a assinatura que contém <nome do
documento>-signed.txt(exemplo: nossosNomes-signed.txt).

**Para fazer o processo de verificação, é necessário executar o verifySignature.jar**
java -jar verifySignature.jar

**Na verificação do documento será requisitado:**
1. O file path do arquivo assinado
exemplo: “exemplo-signed.txt”
2. File path que contém o certificado(arquivo de extensão .crt)
exemplo: “Guilherme Pereira Fantini.crt”

Dessa forma, será gerado o arquivo com o nome especificado no campo ​doc ​do arquivo da assinatura, caso o arquivo tenha uma assinatura válida e correta.
