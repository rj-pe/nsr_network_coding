import java.util.List;


public class SenderNodeDebug extends SenderNode {

    SenderNodeDebug(List<Node> nodesToForward, FiniteField_F_2_n finiteField, String filename, int packet_length) {
        super(nodesToForward, finiteField, filename);
        super.PACKET_LENGTH = packet_length;
        super.readData(filename);
    }
}


