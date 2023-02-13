import java.io.*;
import java.net.*;
import java.util.ArrayList;

class SocketServer {
  public static void main(String[] args) throws IOException {
    final int PORTNR = 1250;

    ServerSocket tjener = new ServerSocket(PORTNR);
    System.out.println("Logg for tjenersiden. Nå venter vi...");
    ArrayList<Thread> threads = new ArrayList<>();
    while (true){
        Socket forbindelse = tjener.accept();  // venter inntil en klient kobler til
        System.out.println("En klient har koblet seg til.");
        Calculator calculator = new Calculator(forbindelse);
        Thread thread = new Thread(calculator);
        thread.start();
        threads.add(thread);
    }
  }
}


class Calculator implements Runnable{

    Socket socket;
    InputStreamReader leseforbindelse;
    BufferedReader leseren;
    PrintWriter skriveren;

    public Calculator(Socket socket) throws IOException{
      this.socket = socket;
      leseforbindelse = new InputStreamReader(socket.getInputStream());
      leseren = new BufferedReader(leseforbindelse);
      skriveren = new PrintWriter(socket.getOutputStream(), true);
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
      skriveren.println("Skriv hva du vil, så skal jeg gjenta det, avslutt med linjeskift.");

      try {
        /* Mottar data fra klienten */
        String enLinje = leseren.readLine();  // mottar en linje med tekst
        while (enLinje != null) {  // forbindelsen på klientsiden er lukket
          System.out.println("En klient skrev: " + enLinje);
          skriveren.println("Received equation: " + enLinje);  // sender svar til klienten
          skriveren.println("Answer is: " + calculate(enLinje));
          enLinje = leseren.readLine();
        }

        /* Lukker forbindelsen */
        leseren.close();
        skriveren.close();
        socket.close();
      } catch (IOException e) {

      }
    }
  }



