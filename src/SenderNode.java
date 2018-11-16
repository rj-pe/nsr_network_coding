import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class SenderNode extends Node {

    private List<int[]> data = new ArrayList<>();

    private int currentGeneration = 1;

    private static final int PACKET_LENGTH = 100;

    public SenderNode(List<Node> nodesToForward, FiniteField_F_2_n finiteField, String filename) {
        super(nodesToForward, finiteField);
        readData(filename);
    }
/// This function only stores one packet per input file. App will need modification if multiple packets need to be
/// transmitted through the network.
    private void readData(String filename) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line = null;
            while((line = br.readLine()) != null) {
                // this test is modified to work with Linux packet copying format
                // if (line.startsWith("|0   |")) {
                lines.add(line);
                    // The following break ends readData after first packet is added to lines. If multiple packets
                    // per file are to be transmitted some modifications will be necessary.
                    // break;
                //}
            }
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        // Note: convertToBytes method is modified to support linux libpcap library packet format
        for (String line : lines) {
            data.add(convertToBytes(line));
        }

        for (int[] raw_data : data) {
            // pad data with zeros to fit the packet length
            int numberOfGenerations = (int)Math.ceil((double)raw_data.length / (PACKET_LENGTH * getNetworkMinCut()));
            int length = numberOfGenerations * PACKET_LENGTH * getNetworkMinCut();
            int[] padded = Arrays.copyOf(raw_data, length);
            int generationNumber = 1;
            for (int i = 1; i <= numberOfGenerations; i++) {
                for (int j = 1; j <= getNetworkMinCut(); j++) {
                    int rangeStartIndex = (i-1) * getNetworkMinCut() * PACKET_LENGTH + (j-1) * PACKET_LENGTH;
                    int[] body =  Arrays.copyOfRange(padded, rangeStartIndex, rangeStartIndex + PACKET_LENGTH);
                    // changed the first parameter of Packet method call from generationNumber to i
                    Packet packet = new Packet(i, getUnitVector(j, getNetworkMinCut()), body);

                    /// store the packet into corresponding class field-- receivedPackets
                    onPacketReceived(packet);
                }
            }
        }
    }

    private int[] getUnitVector(int x, int length) {
        int[] vector = new int[length];
        vector[x-1] = 1;
        return vector;
    }

    private int[] convertToBytes(String line) {
        /// This method modified from original for use with a packet copied as a hexstream.
        String[] bytes = new String[line.length()/2];
        for(int i = 0, j = 0; i < line.length() - 1; i = i + 2, j++){
            bytes[j] =  line.substring(i, i + 2);
        }
        int[] data = new int[bytes.length * 2];
        int i = 0;
        for (String bite : bytes) {
            if (bite.length() == 2) {
                data[i++] = Integer.decode("0x" + bite.substring(0, 1));
                data[i++] = Integer.decode("0x" + bite.substring(1, 2));
            }
        }
        return data;
    }

    public void handle() {
        while(getReceivedPackets(currentGeneration) != null) {
            for (Node nodeToForward: getNodesToForward()) {
                for (Packet packet : getReceivedPackets(currentGeneration)) {
                    /// send packets to nodes listed in class field-- nodesToForward
                    nodeToForward.onPacketReceived(packet);
                }
            }
            // by the time generation increases to two, all the nodes in nodes to forward have been gone through data in
            // generation 2,3,.. is lost
            currentGeneration++;
        }
    }

    public static void main(String args[]) {
        new SenderNode(null, null, "input.txt");
    }
}
