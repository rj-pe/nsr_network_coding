import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class IntermediateNode extends Node {

    private Random random = new Random();
    private String name;

    private int currentGeneration = 1;
    public IntermediateNode(List<Node> nodesToForward, FiniteField_F_2_n finiteField, String name) {
        super(nodesToForward, finiteField);
        this.name = name;
    }

    @Override
    public void handle() {
        if (getReceivedPackets(currentGeneration).size() >= getNetworkMinCut()) {
            Logger logger = Logger.getLogger("log");
            Integer[] coefficients = new Integer[getNetworkMinCut()];
            FiniteField_F_2_n ff = getFiniteField();
            Packet packetToSend = getReceivedPackets(currentGeneration).get(0).clone();
            int coefficient = random.nextInt(ff.getElementsCount());
            logger.log(Level.INFO, String.format("%s %d", this.name, coefficient));

            coefficients[0] = coefficient;
            ff.multiplyPacketBy(packetToSend, coefficient);
            for (int i = 1; i < getNetworkMinCut(); i++) {
                coefficient = random.nextInt(ff.getElementsCount());
                logger.log(Level.INFO, String.format("%s %d", this.name ,coefficient));
                coefficients[i] = coefficient;
                Packet packet = getReceivedPackets(currentGeneration).get(i).clone();
                ff.multiplyPacketBy(packet, coefficient);
                ff.addPackets(packetToSend, packet);
            }
            for (Node node: getNodesToForward()) {
                Packet clone_for_sink_node = packetToSend.clone();
                node.onPacketReceived(clone_for_sink_node);
            }
            currentGeneration++;
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
