import java.util.List;

public class SenderNodeProduction extends SenderNode {
    SenderNodeProduction(List<Node> nodesToForward, FiniteField_F_2_n finiteField, String filename){
        super(nodesToForward, finiteField, filename);
        super.PACKET_LENGTH = 100;
        super.readData(filename);
    }
}
