package socket;

import java.io.*;
import java.net.*;
import java.util.*;

//feito por Guilherme Rossi - 1757997 - Ultima versão
final class HttpRequest implements Runnable {

    final static String CRLF = "\r\n";
    Socket socket;

    public HttpRequest(Socket socket) throws Exception {
        this.socket = socket;
    }

    // método run() chamado quando a thread é criada
    public void run() {
        try {
            processRequest();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private void processRequest() throws Exception {
        /* Aqui lemos as entradas e saidas de dados */
        InputStreamReader is = new InputStreamReader(socket.getInputStream());
        DataOutputStream os = new DataOutputStream(socket.getOutputStream());

        BufferedReader br = new BufferedReader(is);

        /* Request HTTP */
        String requestLine = br.readLine();
        //System.out.println(requestLine);
        
        /* Serve para facilitar a leitura de palavras */
        StringTokenizer tokens = new StringTokenizer(requestLine);
        /* Passa para o proximo GET */
        tokens.nextToken();
        
        /* Salva o nome do arquivo */
        String fileName = tokens.nextToken();
        //System.out.println(fileName);

        /* Le o caminho do arquivo */
        String caminho = System.getProperty("user.dir");
        //System.out.println(caminho);
        
        /* Se não for especificado o arquivo volta para tela inicial */
        if (fileName.compareTo("/") == 0) 
            fileName = caminho + "/src/main.html";
        
        else
            fileName = caminho + "/src/" + fileName;
        

        /* Abre o arquivo */
        File file = new File(fileName);
        FileInputStream fis = null;
        boolean fileExists = true;
        
        /* FileNotFoundException se o caminho ou nome do arquivo estiverem errados */
        try {
            fis = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            System.out.println(e);
            fileExists = false;
        }

        
        if (fileExists)
            System.out.println("Arquivo existe: " + fileName);
        else
            System.out.println("Arquivo não existe: " + fileName);
        

        /* Printa o cabeçalho de request */
        System.out.println(requestLine);
        String headerLine = null;
        while ((headerLine = br.readLine()).length() != 0)
            System.out.println(headerLine);

        /* Construção da mensagem de wireshark */
        String statusLine = null;
        String contentTypeLine = null;
        String entityBody = null;
        if (fileExists) {
            statusLine = "HTTP/1.1 200 OK" + CRLF;
            contentTypeLine = "Content-Type: "
                    + contentType(fileName) + CRLF;
        } else {
            statusLine = "HTTP/1.1 404 Not Found" + CRLF;
            contentTypeLine = "Content-Type: text/html" + CRLF;
            entityBody = "<HTML>"
                    + "<HEAD><TITLE>Ops!</TITLE></HEAD>"
                    + "<BODY>Not found</BODY></HTML>";
        }
        /* Printa a linha de status */
        os.writeBytes(statusLine);

        /* Printa a linha do tipo de conteúdo */
        os.writeBytes(contentTypeLine);

        /* Printa uma linha em branco, indicando o final do cabeçalho */
        os.writeBytes(CRLF);

        /* Envia o corpo da mensagem caso o arquivo não seja encontrado */
        if (fileExists) {
            sendBytes(fis, os);
            fis.close();
        } else {
            os.writeBytes(entityBody);
        }

        /* Fecha tudo */
        os.close();
        br.close();
        socket.close();
    }

    private static void sendBytes(FileInputStream fis, OutputStream os) throws Exception {
        // Constroi um buffer de 1k para responder ao cliente
        byte[] buffer = new byte[1024];
        int bytes = 0;

        // Copia o pedido na saída
        while ((bytes = fis.read(buffer)) != -1) {
            os.write(buffer, 0, bytes);
        }
    }

    private static String contentType(String fileName) {
        if (fileName.endsWith(".htm") || fileName.endsWith(".html")) {
            return "text/html";
        }
        if (fileName.endsWith(".png")) {
            return "image/png";
        }
        if (fileName.endsWith(".gif")) {
            return "image/gif";
        }
        if (fileName.endsWith(".jpegs") || fileName.endsWith(".jpg")) {
            return "image/jpeg";
        }
        if (fileName.endsWith(".css")) {
            return "text/css";
        }
        return "application/octet-stream";
    }
}
