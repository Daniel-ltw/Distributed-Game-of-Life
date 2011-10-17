package Life;

import java.util.ArrayList;
import java.util.HashMap;

public class Lifeform {
	private boolean cells[][];
	private int cellRows;
	private int cellCols;
	
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
	public Lifeform(int cellCols, int cellRows){
		this.cellCols = cellCols;
		this.cellRows = cellRows;
		cells = new boolean[cellCols][cellRows];
		clear();
	}
	
	/**
	 * set the respective cell with the specified boolean
	 * @param cellX
	 * @param celly
	 * @param b
	 * @return 
	 */
	public void setCell(int cellX, int cellY, boolean b){
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
	

    public String toString() {
        if (cells == null) return "Empty";
        HashMap<Integer, Integer> a = new HashMap<Integer, Integer>(cellRows);
        String s = "____________________", state = "d";
        for(int x = 0; x < cellCols; x++){
        	s += "\n";
        	for(int y = 0; y < cellRows; y++){
        		if(cells[x][y] == true){
        			s += "|1";
        			a.put(y, 1);
        		} else {
        			s += "|0";
        			a.put(y, 1);
        		}
        	}
        	if(!a.containsValue(0)){
        		state = "s"; 
        	}
        	int count = 0;
        	for(int k:a.values()){
        		if(k == 1) count++;
        	}
        	if(count >= 2 && count < 4){
        		state = "l";
        	}
        	s += "|, " + state;
        }
        return s;
    }
}
