

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;



public class SenderNode extends Node {

    private List<int[]> data = new ArrayList<int[]>();

    private int currentGeneration = 1;

    private static final int PACKET_LENGTH = 100;

    public SenderNode(List<Node> nodesToForward, FiniteField_F_2_n finiteField, String filename) {
        super(nodesToForward, finiteField);
        readData(filename);
    }
// This function is not storing all of the packets in the input file. Only stores the first packet.
    private void readData(String filename) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line = null;
            while((line = br.readLine()) != null) {
                // this test is modified to work with Linux packet copying format
                if (line.startsWith("|0   |")) {
                    lines.add(line.substring("|0   |".length()));
                    // this break ends readData after first packet is added to lines.
                    break;
                }
            }
        } catch(Exception ex) {
            ex.printStackTrace();
        }
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
                    Packet packet = new Packet(generationNumber, getUnitVector(j, getNetworkMinCut()), body);
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
        String[] bytes = line.split("\\|");
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
                    nodeToForward.onPacketReceived(packet);
                }
            }
            currentGeneration++;
        }
    }

    public static void main(String args[]) {
        new SenderNode(null, null, "input.txt");
    }
}
