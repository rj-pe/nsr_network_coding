package networkcoding;
import java.util.List;

public class SenderNodeProduction extends SenderNode {
    SenderNodeProduction(List<Node> nodesToForward, FiniteField_F_2_n finiteField, String filename, int network_min_cut){
        super(nodesToForward, finiteField, filename, network_min_cut);
        super.PACKET_LENGTH = 100;
        super.readData(filename);
    }
}
