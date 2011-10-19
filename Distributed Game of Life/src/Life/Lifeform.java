package Life;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class Lifeform implements Serializable{

	private boolean cells[][];
	private int cellRows, cellsBuffer[][], cellCols, id, generations;
	private boolean toString;

	/**
	 * Creates a define shape from the given arraylist of integer x and y
	 * @param cellCols
	 * @param cellRows
	 * @param listx
	 * @param listy
	 */
	public Lifeform(int cellCols, int cellRows, ArrayList<Integer> listx, ArrayList<Integer> listy){
		this.cellCols = cellCols;
		this.cellRows = cellRows;
		cells = new boolean[cellCols][cellRows];
		cellsBuffer= new int[cellCols][cellRows];
		clear();
		for(int x = 0; x < listx.size(); x++){
			cells[listx.get(x)][listy.get(x)] = true;
		}
	}

	/**
	 * Creates a empty lifeform
	 * @param cellCols
	 * @param cellRows
	 */
	public Lifeform(int id, int cellCols, int cellRows){
		this.id = id;
		this.cellCols = cellCols;
		this.cellRows = cellRows;
		cells = new boolean[cellCols][cellRows];
		cellsBuffer= new int[cellCols][cellRows];
		clear();
	}

	public int getID(){
		return id;
	}

	/**
	 * set the respective cell with the specified boolean
	 * @param cellX
	 * @param celly
	 * @param b
	 * @return 
	 */
	public synchronized void setCell(int cellX, int cellY, boolean b){
		cells[cellX][cellY] = b;
	}

	public boolean check(int x, int y){
		return cells[x][y];
	}

	public int size(){
		int result = 0; 
		for(int x = 0; x < cellCols; x++){
			for(int y = 0; y < cellRows; y++){
				if(cells[x][y] = true) result++;
			}
		}
		return result;
	}

	// clears canvas
	public synchronized void clear() {
		for( int x=0; x<cellCols; x++ ) {
			for( int y=0; y<cellRows; y++ ) {
				cells[x][y] = false;
			}
		}
	}


	public String toString(){
		toString = true;
		int y;
		if (cells == null) return "Empty";

		// should re-implement this to incorporate next to verify the liveness
		String s = "____________________", state = "d";
		HashMap<Integer, String> states = new HashMap<Integer, String>(cellRows);
		for(int x = 0; x < cellCols; x++){
			s += "\n";
			for(y = 0; y < cellRows; y++){
				if(cells[x][y] == true){
					s += "|1";
				} else {
					s += "|0";
				}
				switch( cellsBuffer[x][y] ) {
				case 2:
					if(states.containsKey(y)){
						if(states.get(y) != "l"){
							state = "s";
							states.put(y, state);
						}
					}
					break;
				case 3:
					state = "l";
					break;
				default:
					if(states.containsKey(y)){
						if(states.get(y) != "l" || 
								states.get(y) != "s"){
							state = "d";
							states.put(y, state);
						}
					} else {
						state = "d";
						states.put(y, state);
					}
					break;
				}
			}
			s += "|, " + states.get(y);
		}
		toString = false;
		return s;
	}

	/**
	 * 
	 * @return cellCols
	 */
	public int cols(){
		return cellCols;
	}

	/**
	 * 
	 * @return cellRows
	 */
	public int rows(){
		return cellRows;
	}

	/**
	 * 
	 * @return generations
	 */
	public int gens(){
		return generations;
	}



	/**
	 * Client running lifeform generations
	 */
	public synchronized void next() {
		int x;
		int y;

		generations++;
		// clear the buffer
		for( x=0; x<cellCols; x++ ) {
			for( y=0; y<cellRows; y++ ) {
				cellsBuffer[x][y] = 0;
			}
		}

		// count neighbors of off-edge cells
		for( x=1; x<cellCols-1; x++ ) {
			for( y=1; y<cellRows-1; y++ ) {
				if ( this.check(x, y) ) {
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
			if ( this.check(x, y) ) {
				if ( x > 0 ) {
					if ( y > 0 )
						cellsBuffer[x-1][y-1]++;
					if ( y < cellRows-1 )
						cellsBuffer[x-1][y+1]++;
					cellsBuffer[x-1][y]++;
				}
				if ( x < cellCols-1 ) {
					if ( y < cellRows-1 )
						cellsBuffer[x+1][y+1]++;
					if ( y > 0 )
						cellsBuffer[x+1][y-1]++;
					cellsBuffer[x+1][y]++;
				}
				if ( y > 0 )
					cellsBuffer[x][y-1]++;
				if ( y < cellRows-1 )
					cellsBuffer[x][y+1]++;
			}

			// turn clockwise at collision with edge
			if ( x==cellCols-1 && y==0 ) {
				dx = 0;
				dy = 1;
			} else if ( x==cellCols-1 && y==cellRows-1 ) {
				dx = -1;
				dy = 0;
			} else if ( x==0 && y==cellRows-1 ) {
				dx = 0;
				dy = -1;
			} else if ( x==0 && y==0 ) {
				// all edge cells done
				break;
			}
			x += dx;
			y += dy;
		}

		if(!toString){
			transform();
		}

	}

	private void transform() {
		// here is the life algorithm
		// simple, isn't it?
		for( int x = 0; x<cellCols; x++ ) {
			for( int y = 0; y<cellRows; y++ ) {
				switch( cellsBuffer[x][y] ) {
				case 2:
					// no change
					break;
				case 3:
					this.setCell(x, y, true);
					break;
				default:
					this.setCell(x, y, false);
					break;
				}
			}
		}
	}
}
