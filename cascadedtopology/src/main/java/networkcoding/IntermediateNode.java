package networkcoding;
import java.io.Serializable;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;

abstract class IntermediateNode extends Node implements Serializable {
    FiniteField_F_2_n ff;
    Packet packetToSend;
    FileHandler received_packet_log;
    String name;
    private Logger logger;
    int currentGeneration = 1;
    int coefficient;
    Integer[] coefficients;

    IntermediateNode(List<Node> nodesToForward, FiniteField_F_2_n finiteField, String name, int network_min_cut) {
        super(nodesToForward, finiteField, network_min_cut);
        this.name = name;
    }

    void instantiate_handle_objects(){
        logger = Logger.getLogger("log");
        coefficients = new Integer[getNetworkMinCut()];
        ff = getFiniteField();
        packetToSend = (Packet) UnoptimizedDeepCopy.copy(getReceivedPackets(currentGeneration).get(0));
    }

    Packet get_next_packet(int coefficient, int next){
        coefficients[next] = coefficient;
        return (Packet) UnoptimizedDeepCopy.copy(getReceivedPackets(currentGeneration).get(next));
    }

    void network_coding(Packet packet, int coefficient){
        ff.multiplyPacketBy(packet, coefficient);
        ff.addPackets(packetToSend, packet);
    }

    void log_encoding_vector(){
        StringBuilder random_encoding_vector = new StringBuilder();
        for (int member: coefficients) {
            random_encoding_vector.append(member);
            random_encoding_vector.append(" ; ");
        }
        logger.log(Level.INFO, String.format("%s ; %s", name, random_encoding_vector.toString()));
    }

    void forward_packet(){
        for (Node node: getNodesToForward()) {
            Packet clone_for_next_node = (Packet) UnoptimizedDeepCopy.copy(packetToSend);
            node.onPacketReceived(clone_for_next_node);
        }
        currentGeneration++;
    }
}
