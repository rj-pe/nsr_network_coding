import java.util.List;
import java.util.Random;
import java.util.logging.Level;

public class IntermediateNodeProduction extends IntermediateNode {

    private Random random = new Random();

    public IntermediateNodeProduction(List<Node> nodesToForward, FiniteField_F_2_n finiteField, String name, int network_min_cut) {
        super(nodesToForward, finiteField, name, network_min_cut);
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
            handle();
        }
    }

}
