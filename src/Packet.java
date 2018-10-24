

import java.util.Arrays;

public class Packet implements Cloneable {

    public int generation;
    public int[] header;
    public int[] body;

    public Packet(int generation, int[] header, int[] body) {
        this.generation = generation;
        this.header = header;
        this.body = body;
    }
    public String toString() {
        return Arrays.toString(header) + Arrays.toString(body);
    }

    public Packet clone() {
        int[] header = Arrays.copyOf(this.header, this.header.length);
        int[] body = Arrays.copyOf(this.body, this.body.length);
        return new Packet(generation, header, body);
    }
}
