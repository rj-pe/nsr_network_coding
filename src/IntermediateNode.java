

import java.util.List;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/// This class contains debugging code used for testing various local encoding vectors. Three separate sections must be
/// edited to switch between hardcoded and randomly generated local encoding vectors. Each section is marked @debug
public class IntermediateNode extends Node {

    private Random random = new Random();

    private int[] local_encoding_vector;

    private String name;

    private int currentGeneration = 1;
/*
    public IntermediateNode(List<Node> nodesToForward, FiniteField_F_2_n finiteField, String name, int local_encoding_vector[]) {
        super(nodesToForward, finiteField);
        this.name = name;
        this.local_encoding_vector = local_encoding_vector;
    }
*/

    /// @debug: for hardcoding a local encoding vector uncomment constructor  below and comment out constructor above
    public IntermediateNode(List<Node> nodesToForward, FiniteField_F_2_n finiteField, String name, int local_encoding_vector[]) {
        super(nodesToForward, finiteField);
        this.name = name;
        this.local_encoding_vector = local_encoding_vector;
    }
    /// end debug


    @Override
    public void handle() {
        if (getReceivedPackets(currentGeneration).size() >= getNetworkMinCut()) {
            Logger logger = Logger.getLogger("log");
            Integer[] coefficients = new Integer[getNetworkMinCut()];
            FiniteField_F_2_n ff = getFiniteField();
            Packet packetToSend = getReceivedPackets(currentGeneration).get(0).clone();
            //int coefficient = random.nextInt(ff.getElementsCount());
            /// @debug: for hardcoding a local encoding vector uncomment line below and comment line above
            int coefficient = local_encoding_vector[0];
            /// end debug
            logger.log(Level.INFO, String.format("%s %d", this.name, coefficient));

            coefficients[0] = coefficient;
            ff.multiplyPacketBy(packetToSend, coefficient);
            for (int i = 1; i < getNetworkMinCut(); i++) {
                //coefficient = random.nextInt(ff.getElementsCount());
                /// @debug: for hardcoding a local encoding vector uncomment line below and comment line above
                coefficient = local_encoding_vector[i];
                /// end debug
                logger.log(Level.INFO, String.format("%s %d", this.name ,coefficient));
                coefficients[i] = coefficient;
                Packet packet = getReceivedPackets(currentGeneration).get(i).clone();
                ff.multiplyPacketBy(packet, coefficient);
                ff.addPackets(packetToSend, packet);
            }
            // System.out.println("applying random coefficients: " + Arrays.asList(coefficients));
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
