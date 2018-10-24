
import java.util.Arrays;
import java.util.List;
import java.util.Random;



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
            Integer[] coefficients = new Integer[getNetworkMinCut()];
            FiniteField_F_2_n ff = getFiniteField();
            Packet packetToSend = getReceivedPackets(currentGeneration).get(0).clone();
            int coefficient = random.nextInt(ff.getElementsCount());
            coefficients[0] = coefficient;
            ff.multiplyPacketBy(packetToSend, coefficient);
            for (int i = 1; i < getNetworkMinCut(); i++) {
                coefficient = random.nextInt(ff.getElementsCount());
                coefficients[i] = coefficient;
                Packet packet = getReceivedPackets(currentGeneration).get(i).clone();
                ff.multiplyPacketBy(packet, coefficient);
                ff.addPackets(packetToSend, packet);
            }
            System.out.println("applying random coefficients: " + Arrays.asList(coefficients));
            for (Node node: getNodesToForward()) {
                node.onPacketReceived(packetToSend);
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
