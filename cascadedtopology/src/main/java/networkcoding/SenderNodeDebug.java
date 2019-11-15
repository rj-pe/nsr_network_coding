package networkcoding;
import java.io.Serializable;
import java.util.List;


public class SenderNodeDebug extends SenderNode implements Serializable {

    SenderNodeDebug(List<Node> nodesToForward, FiniteField_F_2_n finiteField, String filename, int packet_length, int network_min_cut) {
        super(nodesToForward, finiteField, filename, network_min_cut);
        super.PACKET_LENGTH = packet_length;
        super.readData(filename);
    }
}


