import java.io.*;
import java.net.*;
import java.util.Scanner;

class SocketClient {
  public static void main(String[] args) throws IOException {
    final int PORTNR = 1250;

    Scanner sc = new Scanner(System.in);

    Socket socket = new Socket(InetAddress.getLocalHost().getHostName(), PORTNR);
    System.out.println("Connection established");

    /* Åpner en forbindelse for kommunikasjon med tjenerprogrammet */
    InputStreamReader leseforbindelse
                      = new InputStreamReader(socket.getInputStream());
    BufferedReader bufferedReader = new BufferedReader(leseforbindelse);
    PrintWriter printWriter = new PrintWriter(socket.getOutputStream(), true);

    /* Leser innledning fra tjeneren og skriver den til kommandovinduet */
    String innledning1 = bufferedReader.readLine();
    String innledning2 = bufferedReader.readLine();
    System.out.println(innledning1 + "\n" + innledning2);

    /* Leser tekst fra kommandovinduet (brukeren) */
    String enLinje = sc.nextLine();
    while (!enLinje.equals("")) {
      printWriter.println(enLinje);  // sender teksten til tjeneren
      String respons = bufferedReader.readLine();  // mottar respons fra tjeneren
      System.out.println("Fra tjenerprogrammet: " + respons);
      respons = bufferedReader.readLine();  // mottar respons fra tjeneren
      System.out.println("Fra tjenerprogrammet: " + respons);
      enLinje = sc.nextLine();
    }

    /* Lukker forbindelsen */
    bufferedReader.close();
    printWriter.close();
    socket.close();
  }
}

