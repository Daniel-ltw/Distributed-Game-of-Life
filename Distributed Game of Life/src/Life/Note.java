package Life;

import java.io.Serializable;

/*
    Implementation of a Linda Tuple Space.
    Author: Moti Ben-Ari, 2004.
    
    A Note consists of a String and an array of zero or more Objects.
    There are separate constructors for 0-3 integer elements.
    Method get(int) returns the i'th integer element.
*/  

public class Note implements Serializable{

	public String id, r;
    public Lifeform l;
    public int g;
    private int nid;
    
    public Note (String id, int gens, Lifeform life, int nid) {
        if (id == null || gens <= 0 || nid < 0) System.exit(1);
        this.id = id;
        this.g = gens; 
        this.nid = nid;
        if (life != null) this.l = life;
    }
    
    public Note (String id, Lifeform l, String result) {
        if (id == null) System.exit(1);
        this.id = id;
        if (l != null) this.l = l;
        if (result != null) this.r = result;
    }
    
    public int getNID(){
    	return nid;
    }

    public String toString() {
        if (l == null) return id;
        String s = "\n" + id + "\n" + l;
        return s;
    }
}
