package networkcoding;

import java.io.Serializable;
import java.util.Arrays;

public class Packet implements Serializable {

    int generation;
    Integer[] header;
    Integer[] body;

    Packet(int generation, Integer[] header, Integer[] body) {
        this.generation = generation;
        this.header = header;
        this.body = body;
    }
    public String toString() {
        return
                /*Arrays.toString(
                        header).replace("[","").replace("]","")
                        + */Arrays.toString(
                                body).replace("[","").replace("]","");
    }
}
