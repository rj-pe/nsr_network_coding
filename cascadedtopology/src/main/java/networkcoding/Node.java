package networkcoding;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public abstract class Node implements Serializable {

    private int networkMinCut;

    private Map<Integer, List<Packet>> receivedPackets = new HashMap<>();

    private List<Node> nodesToForward;

    private FiniteField_F_2_n finiteField;

    public Node(List<Node> nodesToForward, FiniteField_F_2_n finiteField, int network_min_cut) {
        this.nodesToForward = nodesToForward;
        this.finiteField = finiteField;
        this.networkMinCut = network_min_cut;
    }

    int getNetworkMinCut() {
        return networkMinCut;
    }

    List<Node> getNodesToForward() {
        return nodesToForward;
    }

    Map<Integer, List<Packet>> getReceivedPackets() {
        return receivedPackets;
    }

    List<Packet> getReceivedPackets(int generation) {
        return receivedPackets.get(generation);
    }

    FiniteField_F_2_n getFiniteField() {
        return finiteField;
    }

    public void onPacketReceived(Packet packet) {
        if (receivedPackets.get(packet.generation) == null) {
            receivedPackets.put(packet.generation, new ArrayList<Packet>());
        }
        receivedPackets.get(packet.generation).add(packet);
    }

    public abstract void handle();
}
