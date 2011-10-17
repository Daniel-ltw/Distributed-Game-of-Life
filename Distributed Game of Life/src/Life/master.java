package Life;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class master {
	public static void main(String[] args) throws Exception {
		ServerSocket m_ServerSocket = new ServerSocket(12111);
		int id = 0;
		while (true) {
			Socket clientSocket = m_ServerSocket.accept();
			ClientService cliThread = new ClientService(clientSocket, id++);
			cliThread.start();
		}
	}
}

class ClientService extends Thread {
	Socket clientSocket;
	int clientID = -1;
	boolean running = true;

	public ClientService(Socket s, int i) {
		clientSocket = s;
		clientID = i;
	}

	public void run() {
		System.out.println("Accepted Client : ID - " + clientID + " : Address - "
				+ clientSocket.getInetAddress().getHostName());
		try {
			BufferedReader   in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			PrintWriter   out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
			ObjectOutputStream ostream = new ObjectOutputStream(clientSocket.getOutputStream());
			while (running) {
				//ostream.writeObject(obj);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}