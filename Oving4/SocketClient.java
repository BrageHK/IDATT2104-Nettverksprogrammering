package asyncsocket;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Scanner;

class SocketClient {
  private static byte[] buffer = new byte[256];
  private static InetAddress address;
  private static DatagramSocket socket;
  private static int PORTNR = 4445;


  public static void main(String[] args) throws IOException {
    socket = new DatagramSocket();
    address = InetAddress.getByName("localhost");

    sendMessage("                                                                                  ");
    System.out.println(receiveMessage());

    Scanner sc = new Scanner(System.in);

    String enLinje = "brage";
    while (!enLinje.equals("")) {

      enLinje = sc.nextLine();
      sendMessage(enLinje);
      System.out.println(receiveMessage());
    }

    /* Lukker forbindelsen */
    socket.close();
  }

  private static void sendMessage(String msg) throws IOException {
    buffer = msg.getBytes();
    DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, PORTNR);
    socket.send(packet);
  }

  private static String receiveMessage() throws IOException {
    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
    socket.receive(packet);
    String received = new String(packet.getData(), 0, packet.getLength());
    return received;
  }
}

