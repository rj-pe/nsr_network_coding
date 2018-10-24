

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.Collections;



public class SinkNode extends Node {

    private String name;

    private int currentGeneration = 1;

    public SinkNode(FiniteField_F_2_n finiteField, String name) {
        super(Collections.EMPTY_LIST, finiteField);
        this.name = name;
    }

    public void handle() {
        if (getReceivedPackets().get(currentGeneration).size() >= getNetworkMinCut()) {
            performGaussianElimination();
        }
    }

    private void performGaussianElimination() {

        System.out.println("Performing gaussian elimination on header vectors: " + getReceivedPackets(currentGeneration));
        System.out.println("Applying the same operations to body vectors to obtain original data");

        Matrix matrix = new Matrix(getReceivedPackets(currentGeneration), getFiniteField());
        matrix.performGaussianElimination();

        flushReceivedData(matrix);
        currentGeneration++;
    }

    private void flushReceivedData(Matrix matrix) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(name + "-output.txt", true))) {
            int i = 0;
            for (Packet packet : matrix.getOriginalPackets()) {
                for (int data : packet.body) {
                    bw.write(Integer.toHexString(data));
                    if (i % 2 == 1) {
                        bw.write("|");
                    }
                    i++;
                }
                bw.write("\n");
            }
            bw.flush();
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onPacketReceived(Packet packet) {
        super.onPacketReceived(packet);
        int packetGeneration = packet.generation;
        if (packetGeneration == currentGeneration) {
            handle();
        }
    }
}
