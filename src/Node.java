import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public abstract class Node {

    private int networkMinCut = 3;

    private Map<Integer, List<Packet>> receivedPackets = new HashMap<>();

    private List<Node> nodesToForward;

    private FiniteField_F_2_n finiteField;

    public Node(List<Node> nodesToForward, FiniteField_F_2_n finiteField) {
        this.nodesToForward = nodesToForward;
        this.finiteField = finiteField;
    }

    public int getNetworkMinCut() {
        return networkMinCut;
    }

    public List<Node> getNodesToForward() {
        return nodesToForward;
    }

    public Map<Integer, List<Packet>> getReceivedPackets() {
        return receivedPackets;
    }

    public List<Packet> getReceivedPackets(int generation) {
        return receivedPackets.get(generation);
    }

    public FiniteField_F_2_n getFiniteField() {
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
