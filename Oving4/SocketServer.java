package asyncsocket;


import java.io.IOException;
import java.net.*;

class SocketServer {
    private static byte[] buffer = new byte[256];
    private static DatagramSocket serverSocket;
    private static int PORTNR = 4445;
    private static InetAddress address;

  public static void main(String[] args) throws IOException {


    serverSocket = new DatagramSocket(PORTNR);
    System.out.println("Server is running");

    while (true){
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);  // venter inntil en klient kobler til
        serverSocket.receive(packet);
        address = packet.getAddress();
        int port = packet.getPort();

        String received = receiveMessage(port);
        if (received.trim().equals("")){
            sendMessage("Welcome to the Calculator server please enter an equation", port);
        } else {
            String result = Double.toString(calculate(received));
            sendMessage(result, port);
        }
    }
  }

    private static double calculate(String equation){
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

    private static void sendMessage(String msg, int port) throws IOException {
        serverSocket.send(new DatagramPacket(msg.getBytes(), msg.getBytes().length, address, port));
    }

    private static String receiveMessage(int port) throws IOException {
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length, address, port);
        return new String(packet.getData(), 0, packet.getLength());
    }
}





