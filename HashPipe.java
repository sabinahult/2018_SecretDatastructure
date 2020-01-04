import edu.princeton.cs.algs4.*;
import java.util.*;

public class HashPipe {
  private Pipe root;
  private Pipe tail;
  private int n;
  private int maxIndex;

  private class Pipe {
    private String key;
    private Integer val;
    private Object[] pointers;
    private int maxIndex;

    public Pipe(String key, Integer val, int height) {
      this.key = key;
      this.val = val;
      pointers = new Object[height];
      maxIndex = height-1;
    }

    public String getKey() {
      return key;
    }

    public Integer getVal() {
      return val;
    }

    public void updateVal(Integer val) {
      this.val = val;
    }

    public Pipe getPipeAtHeight(int i) {
      if(i > maxIndex || i < 0) return null;
      return (Pipe) pointers[i];
    }

    public void addPipeAtHeight(Pipe newPipe, int i) {
      if(i > maxIndex || i < 0) return;
      pointers[i] = newPipe;
    }

    public int getMaxPipeIndex() {
      return maxIndex;
    }

    @Override
    public String toString() {
      return "" + key;
    }
  }

  // create an empty symbol table
  public HashPipe() {
    root = new Pipe("", 0, 32);
    tail = root;
    n = 0;
    maxIndex = 0;
  }

  // returns true if n == 0
  public boolean isEmpty() {
    return n == 0;
  }

  // return the number of elements
  public int size() {
    return n;
  }

  // to keep track of the highest not null index in root
  private void updateMaxIndex(int h) {
    if(h-1 > maxIndex) maxIndex = h-1;
  }

  // put key-value pair into the table
  public void put(String key, Integer val) {
    if(key.equals("")) return; // no empty keys allowed in this HashPipe!

    int height = Integer.numberOfTrailingZeros(key.hashCode()) + 1;
    Pipe newPipe = new Pipe(key, val, height);
    Pipe floor = floorPipe(key);

    if(floor.getKey().equals(key)) {
      floor.updateVal(val);
      return;
    }

    if(floor.getPipeAtHeight(0) == null) {
      floor.addPipeAtHeight(newPipe, 0);
      tail = newPipe;
    } else {
      Pipe next = floor.getPipeAtHeight(0);
      floor.addPipeAtHeight(newPipe, 0);
      newPipe.addPipeAtHeight(next, 0);
    }

    n++;
    updateMaxIndex(height);
    if(height > 1) updatePointers(newPipe); // level 0 is updated at insertion
  }

  // updating pointers when a new node is inserted
  private void updatePointers(Pipe newPipe) {
    Pipe current = root;
    int i = maxIndex;
    int h = newPipe.getMaxPipeIndex();

    while(i > h) { // find the place to start (pipe with floor key and h >= h of new pipe)
      Pipe next = current.getPipeAtHeight(i);
      if(next == null) i--;
      else if(next.getKey().compareTo(newPipe.getKey()) > 0) i--;
      else current = next;
    }

    while(i > 0) { // now update pointers
      Pipe next = current.getPipeAtHeight(i);
      if(next == null) {
        current.addPipeAtHeight(newPipe, i);
        i--;
      } else if(next.getKey().compareTo(newPipe.getKey()) > 0) {
        current.addPipeAtHeight(newPipe, i);
        newPipe.addPipeAtHeight(next, i);
        i--;
      } else current = next;
    }
  }

  // return value associated with key if key is in the HashPipe
  public Integer get(String key) {
    if(isEmpty()) return null;
    if(key.equals("")) return null; // don't want to return the root

    Pipe found = floorPipe(key);
    if(!found.getKey().equals(key)) return null; // search miss
    return found.getVal(); // search hit
  }

  // returns the largest key less than or equal to key
  public String floor(String key) {
    Pipe floor = floorPipe(key);
    return floor.getKey();
  }

  // returns the pipe with the largest key <= key
  private Pipe floorPipe(String key) {
    if(isEmpty()) return root;
    if(key.compareTo(tail.getKey()) > 0) return tail;

    Pipe current = root;
    int i = maxIndex;

    while(i >= 0) {
      Pipe next = current.getPipeAtHeight(i);
      if(next == null) i--;
      else if(next.getKey().compareTo(key) > 0) i--;
      else current = next;
    }
    return current;
  }

  // helper method for checking content of pipes
  public String control(String key, int h) {
    Pipe found = floorPipe(key);
    if(!found.getKey().equals(key)) return null;
    if(found.getPipeAtHeight(h) == null) return null;
    return found.getPipeAtHeight(h).getKey();
  }

  // returns a collection of strings from lo to hi
  public Iterable<String> keys(String lo, String hi) {
    List<String> collection = new ArrayList<>();
    if(isEmpty()) return collection;
    if(lo.compareTo(hi) >= 0) return collection;

    Pipe start = null;
    Pipe stop = null;

    if(lo.equals("")) start = root.getPipeAtHeight(0);
    if(hi.equals("")) stop = tail;

    if(start == null) start = floorPipe(lo);
    if(stop == null) stop = floorPipe(hi);

    for(Pipe x = start; x != stop; x = x.getPipeAtHeight(0)) {
      collection.add(x.getKey());
    }
    return collection;
  }

  @Override
  public String toString() {
    String string = "";
    for(Pipe x = root.getPipeAtHeight(0); x != null; x = x.getPipeAtHeight(0)) {
      string += x.getKey() + ":" + x.getVal() + " ";
    }
    return string;
  }

  // either supply a command line argument consisting of one integer
  // representing n, or pipe a text-file into system.in
  public static void main(String[] args) {
    if(args.length != 0) {
      HashPipe hp = new HashPipe();
      int n = Integer.parseInt(args[0]);

      // best case instance
      Stopwatch best = new Stopwatch();
      for(int i = 0; i < n; i++) {
        hp.put("" + i, i);
      }
      StdOut.println("Best: " + best.elapsedTime());

      // worst case instance
      hp = new HashPipe();
      String[] input = new String[n];
      for(int j = 0; j < n; j++) {
        String a = String.format("%dA",j);
        if(a.hashCode() % 2 == 0) {
          char[] c = a.toCharArray();
          c[c.length-1]++;
          a = new String(c);
        }
        input[j] = a;
      }

      Stopwatch worst = new Stopwatch();
      for(int k = 0; k < n; k++) {
        hp.put(input[k], k);
      }
      StdOut.println("Worst: " + worst.elapsedTime());

    } else {

      if(!StdIn.isEmpty()) {
        HashPipe hp = new HashPipe();
        for(int i = 0; !StdIn.isEmpty(); i++) {
          String key = StdIn.readString();
          hp.put(key, i);
        }

        StdOut.println(hp.toString());
      } else {
        StdOut.println("Please supply an integer representing N, or a textfile for standard in, thanks :).");
      }
    }
  }
}
