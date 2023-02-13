import java.io.*;
import java.net.*;
import java.util.ArrayList;

class SocketServer {
  public static void main(String[] args) throws IOException {
    final int PORTNR = 1250;

    ServerSocket tjener = new ServerSocket(PORTNR);
    System.out.println("Server is running");
    ArrayList<Thread> threads = new ArrayList<>();
    while (true){
        Socket forbindelse = tjener.accept();  // venter inntil en klient kobler til
        System.out.println("A client has connected");
        Calculator calculator = new Calculator(forbindelse);
        Thread thread = new Thread(calculator);
        thread.start();
        threads.add(thread);
    }
  }
}


class Calculator implements Runnable{

    Socket socket;
    InputStreamReader inputStreamReader;
    BufferedReader reader;
    PrintWriter printWriter;

    public Calculator(Socket socket) throws IOException{
      this.socket = socket;
      inputStreamReader = new InputStreamReader(socket.getInputStream());
      reader = new BufferedReader(inputStreamReader);
      printWriter = new PrintWriter(socket.getOutputStream(), true);
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

      printWriter.println("Welcome to the calculator server! Please enter an equation in the format: a + b");
      printWriter.println("You can use +, -, * and / as operators.");

      try {
        /* Mottar data fra klienten */
        String readLine = reader.readLine();  // mottar en linje med tekst
        while (readLine != null) {  // forbindelsen på klientsiden er lukket
          System.out.println("A Client Wrote" + readLine);
          printWriter.println("Received equation: " + readLine);  // sender svar til klienten
          printWriter.println("Answer is: " + calculate(readLine));
          readLine = reader.readLine();
        }

        /* Lukker forbindelsen */
        reader.close();
        printWriter.close();
        socket.close();
      } catch (IOException ignored) {

      }
    }
  }



