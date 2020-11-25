package socket;
import java.net.* ;


public final class WebServer {
    public static void main(String argv[]) throws Exception {
	/* Porta que iremos utilizar */ 
	int port = 6789;
        
	/* Socket que ouvira a porta */
	ServerSocket socket = new ServerSocket(port);

	while (true) {
	    //Cria novo socket para comunicação 
	    Socket connection = socket.accept();
	    
	    //Construo um objeto para auxiliar a solicitação HTTP
	    HttpRequest request = new HttpRequest(connection);
	    
	    //Cria uma thread para processar o request
	    Thread thread = new Thread(request);
	    
	    thread.start();
	}
    }
}
