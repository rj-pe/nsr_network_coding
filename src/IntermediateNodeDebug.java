import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class IntermediateNodeDebug extends IntermediateNode {
        /// @debug
        private int[] local_encoding_vector;
        /// end debug

    IntermediateNodeDebug(List<Node> nodesToForward, FiniteField_F_2_n finiteField, String name, int[] local_encoding_vector, int network_min_cut) {
        super(nodesToForward, finiteField, name, network_min_cut);
        this.local_encoding_vector = local_encoding_vector;
    }

        @Override
        public void handle() {
            if (getReceivedPackets(currentGeneration).size() >= getNetworkMinCut()) {
                instantiate_handle_objects();
                Packet packetToSend = getReceivedPackets(currentGeneration).get(0).clone();
                /// @debug
                coefficient = local_encoding_vector[0];
                /// end @debug

                coefficients[0] = coefficient;
                ff.multiplyPacketBy(packetToSend, coefficient);
                for (int i = 1; i < getNetworkMinCut(); i++) {
                    /// @debug
                    coefficient = local_encoding_vector[i];
                    /// end @debug
                   Packet packet = get_next_packet(coefficient, i);
                    network_coding(packet, coefficient);
                }

                log_encoding_vector();

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

