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
	private int cellsBuffer[][], generations;
	
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
				cellsBuffer = new int[n.l.cols()][n.l.rows()]; 
				
				while(generations <= n.g){
					next();
				}
				
				//send result back to server
				out.writeObject(new Note("R", n.l, life.toString()));
				System.out.println(life.toString());
				out.flush();
				
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
				System.exit(1);
			} 
		}
		System.exit(0); 
	}
		
	/**
	 * Client running lifeform generations
	 */
	public synchronized void next() {
		int x;
		int y;

		generations++;
		// clear the buffer
		for( x=0; x<life.cols(); x++ ) {
			for( y=0; y<life.rows(); y++ ) {
				cellsBuffer[x][y] = 0;
			}
		}

		// count neighbors of off-edge cells
		for( x=1; x<life.cols()-1; x++ ) {
			for( y=1; y<life.rows()-1; y++ ) {
				if ( life.check(x, y) ) {
					cellsBuffer[x-1][y-1]++;
					cellsBuffer[x][y-1]++;
					cellsBuffer[x+1][y-1]++;
					cellsBuffer[x-1][y]++;
					cellsBuffer[x+1][y]++;
					cellsBuffer[x-1][y+1]++;
					cellsBuffer[x][y+1]++;
					cellsBuffer[x+1][y+1]++;
				}
			}
		}

		// count neighbors of edge cells
		x=1; // start at (1,0)
		y=0;
		int dx=1;
		int dy=0;
		while( true ) {
			if ( life.check(x, y) ) {
				if ( x > 0 ) {
					if ( y > 0 )
						cellsBuffer[x-1][y-1]++;
					if ( y < life.rows()-1 )
						cellsBuffer[x-1][y+1]++;
					cellsBuffer[x-1][y]++;
				}
				if ( x < life.cols()-1 ) {
					if ( y < life.rows()-1 )
						cellsBuffer[x+1][y+1]++;
					if ( y > 0 )
						cellsBuffer[x+1][y-1]++;
					cellsBuffer[x+1][y]++;
				}
				if ( y > 0 )
					cellsBuffer[x][y-1]++;
				if ( y < life.rows()-1 )
					cellsBuffer[x][y+1]++;
			}

			// turn clockwise at collision with edge
			if ( x==life.cols()-1 && y==0 ) {
				dx = 0;
				dy = 1;
			} else if ( x==life.cols()-1 && y==life.rows()-1 ) {
				dx = -1;
				dy = 0;
			} else if ( x==0 && y==life.rows()-1 ) {
				dx = 0;
				dy = -1;
			} else if ( x==0 && y==0 ) {
				// all edge cells done
				break;
			}
			x += dx;
			y += dy;
		}

		// here is the life algorithm
		// simple, isn't it?
		for( x=0; x<life.cols(); x++ ) {
			for( y=0; y<life.rows(); y++ ) {
				switch( cellsBuffer[x][y] ) {
				case 2:
					// no change
					break;
				case 3:
					life.setCell(x, y, true);
					break;
				default:
					life.setCell(x, y, false);
					break;
				}
			}
		}

	}
}