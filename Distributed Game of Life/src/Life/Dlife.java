package Life;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeMap;
import java.util.Vector;

public class Dlife {

	private static String command = "The master program should be run with\n" +
	"java Dlife gen rows cols\n" +
	"gen\t-\tThe number of generations you would be running\n" +
	"rows\t-\tThe number of rows\n" +
	"cols\t-\tThe number of columns\n\n\n";

	public static void main(String[] args) throws Exception {

		if(!args[0].matches("[0-9]*") ||
				!args[1].matches("[0-9]*") ||
				!args[2].matches("[0-9]*")) {
			System.out.println(command);
			System.exit(1);
		}

		Generator g = new Generator(Integer.parseInt(args[0]), 
				Integer.parseInt(args[1]), Integer.parseInt(args[2]));

		ServerSocket m_ServerSocket = new ServerSocket();
		m_ServerSocket.bind(m_ServerSocket.getLocalSocketAddress());
		System.out.println("Server running on port: " + m_ServerSocket.getLocalPort());
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
	private static int nid = -1, id = -1;
	public HashMap<Integer, Note> notes = new HashMap<Integer, Note>();
	public HashMap<Integer, Integer> counts = new HashMap<Integer, Integer>();

	public Generator(int gen, int rows, int cols) {

		this.gen = gen;
		this.rows = rows;
		this.cols = cols;

		for(int val = 0; val < Math.pow(2, cols*rows); val++){
			id++;
			Lifeform life = new Lifeform(id, cols, rows);
			String bi = Integer.toBinaryString(val);
			while(bi.length() < (cols*rows)){
				String temp = "".concat("0").concat(bi);
				bi = temp;
			}
			System.out.println("binary = " + bi);
			int i = 0;
			for(int xA = 0; xA < cols; xA++){
				for(int yA = 0; yA < rows; yA++){
					if(Character.getNumericValue(bi.charAt(i)) == 1){
						life.setCell(xA, yA, true);
					}
					i++;
				}
			}
			System.out.println(life.toString());
			nid++; 
			Note n = new Note("T", gen, life, nid);
			notes.put(n.getNID(), n);
			space.postnote(n); 
		}
		System.out.println("Notes Size = " + notes.size());
	}

	public synchronized Space getSpace(){
		return space;
	}

	public int getRows(){
		return rows;
	}

	public int getCols(){
		return cols;
	}

	public int getGen(){
		return gen;
	}
}

class ClientService extends Thread {
	private Socket clientSocket;
	private int clientID = -1;
	private Generator g;
	private boolean running = true; 

	public ClientService(Socket s, int i, Generator g) {
		clientSocket = s;
		clientID = i;

		this.g = g;
	}

	public void run() {
		Space space = g.getSpace();
		ObjectInputStream in;
		ObjectOutputStream out; 
		HashMap<Integer, String> result = new HashMap<Integer, String>();

		System.out.println("Accepted Client : ID - " + clientID + " : Address - "
				+ clientSocket.getInetAddress().getHostName());
		try {
			out = new ObjectOutputStream(clientSocket.getOutputStream());
			in = new ObjectInputStream(clientSocket.getInputStream());
			//while(g.counts.get(g.notes.size() - 1) == null){
				if(g.counts.get(g.notes.size() - 1) != null && 
						g.counts.get(g.notes.size() - 1) >= 3) {
					return;
				}
					
				for(int i = 0; i < g.notes.size(); i++) {
					Note n = g.notes.get(i); 
					int lID = n.l.getID();
					if(g.counts.get(lID) != null){
						while(g.counts.get(lID) >= 3){
							return;
						}

						if(g.counts.get(lID) == 2){
							n = space.removenote(n);
						} else {
							n = space.readnote(n);
						}
					} else {
						n = space.readnote(n);
					}
					out.writeObject(n);
					Thread.sleep(1000);
					n = (Note) in.readObject();
					int count;
					if(g.counts.get(lID) != null){
						count = g.counts.get(lID);
						count++;
						g.counts.put(lID, count);
					} else {
						count = 1;
					}
					space.postnote(n); 
					result.put(lID, n.r); 
				}
			//}
			in.close(); 
			out.close(); 

			System.out.println("Report Size = " + result.size());

			// Generate report
			File f = new File("LifeGen Report - " +
					g.getGen() + " - " +
					g.getRows() + " - " + 
					g.getCols() + ".txt");

			if(f.exists()){
				f.delete(); 
			} else {
				f.createNewFile();
			}
			
			try {
				String s ="";
				BufferedWriter file = new BufferedWriter(
						new FileWriter(f));
				for(int x:result.keySet()){
					s += "\n____________________\n\t" + x + "\n";
					s += result.get(x); 
				}
				file.write(s); 
				file.close();
			} catch (IOException e) {
			}
			System.exit(0);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}