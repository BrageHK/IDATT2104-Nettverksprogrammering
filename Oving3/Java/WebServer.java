import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class WebServer implements Runnable{

    Socket socket;
    PrintWriter writer;
    private InputStreamReader inputStreamReader;
    private BufferedReader reader;

    public WebServer(Socket forbindelse) throws IOException{
        this.socket = forbindelse;
        inputStreamReader = new InputStreamReader(forbindelse.getInputStream());
        reader = new BufferedReader(inputStreamReader);
        writer = new PrintWriter(forbindelse.getOutputStream(), true);
    }

    @Override
    public void run() {
        PrintWriter writer = null;
        try {
            writer = new PrintWriter(socket.getOutputStream(), true);

            writer.println("HTTP/1.0 200 OK");
            writer.println("Content-Type: text/html; charset=utf-8");
            writer.println("");
            writer.println("<!DOCTYPE html>");
            writer.println("<html>");
            writer.println("<body>");
            writer.println("<img src=\"https://avatars.githubusercontent.com/u/73225892?v=4\" >");
            writer.println("<h1>Yo! Hvem er dette???</h1>");
            writer.println("<h3>Headers:</h3>");
            writer.println("<ul>");

            String enLinje = null;  // mottar en linje med tekst
            enLinje = reader.readLine();
            while (enLinje != null && !enLinje.isEmpty()){
                writer.println("<li>" + enLinje + "</li>");
                enLinje = reader.readLine();
            }

            writer.println("</ul>");
            writer.println("</body>");
            writer.println("</html>");
            writer.flush();

            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        final int PORTNR = 80;

        ServerSocket tjener = new ServerSocket(PORTNR);
        System.out.println("Logg for tjenersiden. Naa venter vi...");
        ArrayList<Thread> threads = new ArrayList<>();
        while (true){
            Socket forbindelse = tjener.accept();  // venter inntil en klient kobler til
            System.out.println("En klient har koblet seg til.");
            WebServer webServer = new WebServer(forbindelse);
            Thread thread = new Thread(webServer);
            thread.start();
            threads.add(thread);
        }
    }
}



