package Life;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.TreeMap;

public class Dlife {
	
	private static String command = "The master program should be run with\n" +
			"java Dlife gen rows cols\n" +
			"gen\t-\tThe number of generations you would be running\n" +
			"rows\t-\tThe number of rows\n" +
			"cols\t-\tThe number of columns\n\n\n";
	
	public static void main(String[] args) throws Exception {
		
		if(args[0] != "Dlife" || !args[1].matches("[0-9]*") ||
				!args[2].matches("[0-9]*") ||
				!args[3].matches("[0-9]*")) {
			System.out.println(command);
			System.exit(1);
		}
		
		Generator g = new Generator(Integer.parseInt(args[1]), 
				Integer.parseInt(args[2]), Integer.parseInt(args[3]));
		
		ServerSocket m_ServerSocket = new ServerSocket(58411);
		int id = 0;
		while (true) {
			Socket clientSocket = m_ServerSocket.accept();
			ClientService cliThread = new ClientService(clientSocket, id++, g);
			cliThread.start();
		}
	}
}

class Generator {
	private Space space = new Space();
	private int gen, rows, cols;
	
	public Generator(Integer gen, Integer rows, Integer cols) {
		this.gen = gen;
		this.rows = rows;
		this.cols = cols;

		space.postnote("T", gen, cols, rows);
	}
	
	public synchronized Space getSpace(){
		return space;
	}
}

class ClientService extends Thread {
	private Socket clientSocket;
	private int clientID = -1;
	private boolean running = true;
	private Generator generator;

	public ClientService(Socket s, int i, Generator g) {
		clientSocket = s;
		clientID = i;
		
		generator = g;
	}

	public void run() {
		Space space;
		TreeMap<Lifeform, String> result = new TreeMap<Lifeform, String>();
		
		System.out.println("Accepted Client : ID - " + clientID + " : Address - "
				+ clientSocket.getInetAddress().getHostName());
		try {
			BufferedReader   in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
			PrintWriter   out = new PrintWriter(new OutputStreamWriter(clientSocket.getOutputStream()));
			ObjectOutputStream ostream = new ObjectOutputStream(clientSocket.getOutputStream());
			while (running) {
				space = generator.getSpace();
				ostream.writeObject(space);
				
				Note n = space.removenote("R");
				result.put(n.l, n.r);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			
		}
	}
}