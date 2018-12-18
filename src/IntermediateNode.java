import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

abstract class IntermediateNode extends Node {
    FiniteField_F_2_n ff;
    Packet packetToSend;
    String name;
    Logger logger;
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
        packetToSend = getReceivedPackets(currentGeneration).get(0).clone();
    }

    Packet get_next_packet(int coefficient, int next){
        coefficients[next] = coefficient;
        return getReceivedPackets(currentGeneration).get(next).clone();
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
            Packet clone_for_next_node = packetToSend.clone();
            node.onPacketReceived(clone_for_next_node);
        }
        currentGeneration++;
    }
}
