package Life;
/*
    Implementation of a Linda Tuple Space.
    Author: Moti Ben-Ari, 2004.
    
    A Note consists of a String and an array of zero or more Objects.
    There are separate constructors for 0-3 integer elements.
    Method get(int) returns the i'th integer element.
*/  

public class Note {
    public String id;
    public Lifeform l;
    
    public Note (String id, Lifeform life) {
        if (id == null) System.exit(1);
        this.id = id;
        if (life != null) this.l = life;
    }

    public String toString() {
        if (l == null) return id;
        String s = "\n" + id + "\n" + l;
        return s;
    }
}
