package Life;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class DLifeWorker {

	private static String command = "The master program should be run with\n" +
	"java DLifeWorker host_name port_num\n" +
	"host_name\t-\tThe server's host name\n" +
	"port_num\t-\tThe server's port number\n\n\n";

	public static void main(String[] args) throws Exception {

		if(!args[1].matches("[0-9]*")) {
			System.out.println(command);
			System.exit(1);
		}

		ClientSocket client = new ClientSocket(args[0], Integer.parseInt(args[1]));
		System.out.println("Connected to server. \n" +
		"Commencing generations of Life. \n");
		client.start();
	}
}

class ClientSocket extends Thread{

	private Socket socket = null;
	private ObjectInputStream  in = null;
	private ObjectOutputStream  out = null;
	private Lifeform life;

	public ClientSocket(String hostname, int port){

		//Create socket connection
		try{
			socket = new Socket(hostname, port);
			socket.setKeepAlive(true); 
			in = new ObjectInputStream(socket.getInputStream());
			out = new ObjectOutputStream(socket.getOutputStream());
		} catch (UnknownHostException e) {
			System.out.println("Unknown host: ????");
			System.exit(1);
		} catch  (IOException e) {
			System.out.println("No I/O");
			System.exit(1);
		}
	}

	@Override
	public void run() {
		Note n; 
		while(!socket.isClosed()){
			try {
				//retrieve the space as a medium
				n = (Note) in.readObject();

				//read and remove note as according
				//then generate and post result
				life = n.l;
				System.out.println(life.toString());

				while(life.gens() <= n.g){
					life.next();
				}

				//send result back to server
				out.writeObject(new Note("R", n.l, life.toString()));
				//System.out.println(life.toString());
				out.flush();

			} /*catch(EOFException e) {
				System.out.println("Job is Done. Quiting......");
				System.exit(0); 
			}*/catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				System.exit(1);
			}
		}
		System.exit(0); 
	}
}