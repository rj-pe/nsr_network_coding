import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class IntermediateNodeDebug extends Node {
        /// @debug
        private int[] local_encoding_vector;
        /// end debug

        private String name;

        private int currentGeneration = 1;

    IntermediateNodeDebug(List<Node> nodesToForward, FiniteField_F_2_n finiteField, String name, int local_encoding_vector[], int network_min_cut) {
        super(nodesToForward, finiteField, network_min_cut);
        this.name = name;
        this.local_encoding_vector = local_encoding_vector;
    }

        @Override
        public void handle() {
            if (getReceivedPackets(currentGeneration).size() >= getNetworkMinCut()) {
                int coefficient;
                Logger logger = Logger.getLogger("log");
                Integer[] coefficients = new Integer[getNetworkMinCut()];
                FiniteField_F_2_n ff = getFiniteField();
                Packet packetToSend = getReceivedPackets(currentGeneration).get(0).clone();
                /// @debug
                coefficient = local_encoding_vector[0];
                /// end @debug

                logger.log(Level.INFO, String.format("%s %d", this.name, coefficient));

                coefficients[0] = coefficient;
                ff.multiplyPacketBy(packetToSend, coefficient);
                for (int i = 1; i < getNetworkMinCut(); i++) {
                    /// @debug
                    coefficient = local_encoding_vector[i];
                    /// end @debug
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

