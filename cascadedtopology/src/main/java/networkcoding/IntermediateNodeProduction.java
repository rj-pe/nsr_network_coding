package networkcoding;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class IntermediateNodeProduction extends IntermediateNode {

    private Random random = new Random();

    public IntermediateNodeProduction(
            List<Node> nodesToForward, FiniteField_F_2_n finiteField, String name, int network_min_cut) {
        super(nodesToForward, finiteField, name, network_min_cut);
    }
    IntermediateNodeProduction(
            List<Node> nodesToForward, FiniteField_F_2_n finiteField, String name, int network_min_cut, boolean log_flag)
            throws IOException {
        super(nodesToForward, finiteField, name, network_min_cut);
        if(log_flag){
            received_packet_log = new FileHandler(name + ".txt");
        }
    }

    @Override
    public void handle() {
        if (getReceivedPackets(currentGeneration).size() >= getNetworkMinCut()) {
            instantiate_handle_objects();
            coefficient = random.nextInt(ff.getElementsCount());
            // logger.log(Level.INFO, String.format("%s ; %d", this.name, coefficient));
            coefficients[0] = coefficient;
            ff.multiplyPacketBy(packetToSend, coefficient);

            for (int i = 1; i < getNetworkMinCut(); i++) {
                coefficient = random.nextInt(ff.getElementsCount());
                Packet packet = get_next_packet(coefficient, i);
                network_coding(packet, coefficient);
            }

            // log intermediate node's random encoding vector.
            log_encoding_vector();
            // send copy of encoded packet to any receiver nodes
            forward_packet();

        }
    }

    @Override
    public void onPacketReceived(Packet packet) {
        super.onPacketReceived(packet);
        if (packet.generation == currentGeneration) {
            Logger log = Logger.getLogger(this.name + ".txt");
            log.addHandler(received_packet_log);
            received_packet_log.setFormatter(new SimpleFormatter());
            log.setLevel(Level.INFO);
            log.log(Level.INFO, packet.toString());
            handle();
        }
    }

}
