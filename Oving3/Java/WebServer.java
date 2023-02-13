import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class WebServer {
    public static void main(String[] args) throws IOException {
        final int PORTNR = 80;

        ServerSocket tjener = new ServerSocket(PORTNR);
        System.out.println("Logg for tjenersiden. Naa venter vi...");
        ArrayList<Thread> threads = new ArrayList<>();
        while (true){
            Socket forbindelse = tjener.accept();  // venter inntil en klient kobler til
            PrintWriter writer = new PrintWriter(forbindelse.getOutputStream(), true);
            CalculatorWeb calculator = new CalculatorWeb(forbindelse);
            Thread thread = new Thread(calculator);

            String[] headers;
            headers = calculator.getHeaders();

            thread.start();
            threads.add(thread);
            writer.println("HTTP/1.0 200 OK");
            writer.println("Content-Type: text/html; charset=utf-8");
            writer.println("");
            writer.println("<!DOCTYPE html>");
            writer.println("<html>");
            writer.println("<body>");
            writer.println("<h1>Welcome to hell</h1>");
            writer.println("<h3>Headers:</h3>");
            writer.println("<ul>");
            for (String header : headers) {
                if(header != null)
                    writer.println("<li>" + header + "</li>");
            }
            writer.println("</ul>");
            writer.println("</body>");
            writer.println("</html>");
            writer.flush();
            System.out.println("En klient har koblet seg til.");
            forbindelse.close();
        }
    }
}

class CalculatorWeb implements Runnable{

    private Socket socket;
    private InputStreamReader leseforbindelse;
    private BufferedReader reader;
    private PrintWriter skriveren;

    public CalculatorWeb(Socket socket) throws IOException{
        this.socket = socket;
        leseforbindelse = new InputStreamReader(socket.getInputStream());
        reader = new BufferedReader(leseforbindelse);
        skriveren = new PrintWriter(socket.getOutputStream(), true);
    }

    public String[] getHeaders() throws IOException {
        String[] headers = new String[255];
        int i = 0;
        String enLinje = reader.readLine();  // mottar en linje med tekst
        while (enLinje != null && !enLinje.isBlank()) {
            headers[i] = enLinje;
            i++;
            enLinje = reader.readLine();
        }
        return headers;
    }

    private double calculate(String equation){
        String[] parts = equation.split(" ");
        double a = Double.parseDouble(parts[0]);
        double b = Double.parseDouble(parts[2]);
        String operator = parts[1];
        double result = 0;
        switch(operator){
            case "+":
                result = a + b;
                break;
            case "-":
                result = a - b;
                break;
            case "*":
                result = a * b;
                break;
            case "/":
                result = a / b;
                break;
        }
        return result;
    }

    @Override
    public void run() {

        skriveren.println("Hei, du har kontakt med tjenersiden!");
        skriveren.println("Skriv hva du vil, saa skal jeg gjenta det, avslutt med linjeskift.");

        try {
            /* Mottar data fra klienten */
            String enLinje = reader.readLine();  // mottar en linje med tekst
            while (enLinje != null) {  // forbindelsen på klientsiden er lukket
                System.out.println("En klient skrev: " + enLinje);
                skriveren.println("Received equation: " + enLinje);  // sender svar til klienten
                skriveren.println("Answer is: " + calculate(enLinje));
                enLinje = reader.readLine();
            }

            /* Lukker forbindelsen */
            reader.close();
            skriveren.close();
            socket.close();
        } catch (IOException e) {

        }
    }
}
