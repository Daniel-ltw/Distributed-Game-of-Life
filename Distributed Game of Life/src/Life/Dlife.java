package Life;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;
import java.util.HashMap;

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
		g.start();

		ServerSocket m_ServerSocket = new ServerSocket();
		m_ServerSocket.bind(m_ServerSocket.getLocalSocketAddress());
		System.out.println("Server running on port: " + m_ServerSocket.getLocalPort());
		int id = 0;
		boolean running = true;

		Timer t = new Timer();

		while(running){
			if(g.counts.get(g.notes.size() - 1) != null && 
					g.counts.get(g.notes.size() - 1) >= 3) {
				// the jobs has already been done
				running = false; 
			} else {
				ClientService cliThread = new ClientService(
						m_ServerSocket.accept()
						, id++, g);
				cliThread.start();
				if(!t.isAlive()){
					t.start(); 
				}
			}
		} 

		t.running = false;

		// Generate report
		File f = new File("LifeGen Report Server - " +
				g.getGen() + " - " +
				g.getRows() + " - " + 
				g.getCols() + " - " + ".txt");

		if(f.exists()){
			f.delete(); 
		} else {
			f.createNewFile();
		}

		try {
			BufferedWriter file = new BufferedWriter(
					new FileWriter(f));

			file.write(t.time + "mins to complete all jobs"); 
			file.close();
		} catch (IOException e) {
		}
	}
}

class Timer extends Thread{

	public boolean running;
	public int time;

	public Timer(){
		this.time = 0;
		this.running = true;
	}

	@Override
	public void run() {
		while(running){
			try {
				Thread.sleep(1000);
				time++;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}

class Generator extends Thread{

	private Space space = new Space();
	private int gen, rows, cols;
	private static int nid = -1, id = -1;
	public HashMap<Integer, Note> notes = new HashMap<Integer, Note>();
	public HashMap<Integer, Integer> counts = new HashMap<Integer, Integer>();

	public Generator(int gen, int rows, int cols) {
		this.gen = gen;
		this.rows = rows;
		this.cols = cols;
	}

	@Override
	public void run() {
		for(int val = 0; val < Math.pow(2, cols*rows); val++){
			id++;
			Lifeform life = new Lifeform(id, cols, rows);
			String bi = Integer.toBinaryString(val);
			while(bi.length() < (cols*rows)){
				String temp = "".concat("0").concat(bi);
				bi = temp;
			}
			int i = 0;
			for(int xA = 0; xA < cols; xA++){
				for(int yA = 0; yA < rows; yA++){
					if(Character.getNumericValue(bi.charAt(i)) == 1){
						life.setCell(xA, yA, true);
					}
					i++;
				}
			}
			nid++; 
			Note n = new Note("T", gen, life, nid);
			notes.put(n.getNID(), n);
			while(space.Size() >= 20){
				try {
					this.sleep(100);
				} catch (InterruptedException e) {
				} 
			}
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

	public ClientService(Socket s, int i, Generator g) {
		clientSocket = s;
		clientID = i;

		this.g = g;
	}

	public void run() {
		Timer t = new Timer();
		t.start();
		Space space = g.getSpace();
		ObjectInputStream in;
		ObjectOutputStream out; 
		HashMap<Integer, String> result = new HashMap<Integer, String>();

		System.out.println("Accepted Client : ID - " + clientID + " : Address - "
				+ clientSocket.getInetAddress().getHostName());
		try {
			out = new ObjectOutputStream(clientSocket.getOutputStream());
			in = new ObjectInputStream(clientSocket.getInputStream());
			if(g.counts.get(g.notes.size() - 1) != null && 
					g.counts.get(g.notes.size() - 1) >= 3) {
				// do nothing as the jobs has already been done
			} else {

				for(int i = 0; i < g.notes.size(); i++) {
					Note n = g.notes.get(i); 
					int lID = n.l.getID();
					if(g.counts.get(lID) != null && 
							g.counts.get(lID) >= 3){
						i++;
					} else {
						if(g.counts.get(lID) != null && 
								g.counts.get(lID) <= 3){
							if(g.counts.get(lID) == 2){
								n = space.removenote(n);
							} else {
								n = space.readnote(n);
							}
						} else {
							n = space.readnote(n);
						}
						out.writeObject(n);
						Thread.sleep(100);
						n = (Note) in.readObject();
						int count;
						if(g.counts.get(lID) != null){
							count = g.counts.get(lID);
							count++;
						} else {
							count = 1;
						}
						g.counts.put(lID, count);
						space.postnote(n);
						result.put(lID, n.r); 
					}
				}
				out.writeObject(null);
				in.close(); 
				out.close(); 
				t.running = false;

				// nothing is done to remedy if the client fail before this stage. 

				System.out.println("Report Size = " + result.size());
				
				Date d = new Date();
				String folder = "LifeGen Report - " +
				g.getGen() + " - " +
				g.getRows() + " - " + 
				g.getCols() + " - " + 
				clientSocket.getInetAddress().getHostName()
				+ " - " + d.toString();
				
				File f = new File(folder);
				
				f.mkdir();

				if(f.exists()){
					f.delete(); 
				} else {
					f.createNewFile();
				}

				try {
					String s ="";
					for(int x:result.keySet()){
						// Generate report
						f = new File(folder +
						".txt");
					BufferedWriter file = new BufferedWriter(
							new FileWriter(f));
						s += "\n____________________\n\t" + x + "\n";
						s += result.get(x); 
					file.write(s); 
					file.close();
					}
					s += "\n\n Running time = " + t.time + "mins";
				} catch (IOException e) {
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.exit(0); 
	}
}