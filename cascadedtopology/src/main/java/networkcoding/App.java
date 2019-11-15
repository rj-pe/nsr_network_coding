package networkcoding;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.*;



public class App {
    /// When DEV_MODE is set, app will run in debug mode allowing local encoding vectors & packet length field to
    //  be set manually.
    private static final boolean DEV_MODE = false;

    public static void main(String[] args) throws IOException {

        // Logger saves (to file, fh) local encoding vectors at intermediate nodes & packets recv'd by sink nodes.
        String packet_name = args[0];
        String[] tokens = packet_name.split("/");
        LogManager.getLogManager().readConfiguration(new FileInputStream("logging.properties"));
        FileHandler fh = new FileHandler(tokens[tokens.length-1] + ".log");
        Logger log = Logger.getLogger("log");
        log.addHandler(fh);
        fh.setFormatter(new SimpleFormatter());
        log.setLevel(Level.INFO);
        log.log(Level.INFO, String.format("testing: %s", args[0]));
        // end Logger

        FiniteField_F_2_n ff24 = new FiniteField_F_2_n(4);


        List<Node> intermediateNodes = new ArrayList<>();

        if(DEV_MODE) {
            ///TODO: implement network topology functionality in DEV_MODE.
            /// @debug if hardcoding local encoding vectors, specify them below.
            List<Node> sinkNodes = new ArrayList<>();
            sinkNodes.add(new SinkNode(ff24, "t1", 3));
            sinkNodes.add(new SinkNode(ff24, "t2", 3));
            int[] l1 = {3, 7, 2};
            int[] l2 = {1, 2, 4};
            int[] l3 = {4, 5, 3};
            intermediateNodes.add(new IntermediateNodeDebug(sinkNodes, ff24, "i1", l1, 3));
            intermediateNodes.add(new IntermediateNodeDebug(sinkNodes, ff24, "i2", l2, 3));
            intermediateNodes.add(new IntermediateNodeDebug(sinkNodes, ff24, "i3", l3, 3));
            SenderNodeDebug sender =
                    new SenderNodeDebug(intermediateNodes, ff24, args[0], 5, 3);
            sender.handle();
            /// end debug
        } else { // Production Mode
            // Create the network over which the packets will be sent.
            // Network parameters should be specified when creating the network object.
            int numberLayers, nodesPerLayer, numberSinkNodes;
            numberLayers = 2;
            nodesPerLayer = 2;
            numberSinkNodes = 2;

            log.log(Level.INFO,
                    String.format("\n%d; %d; %d\n",
                            numberLayers, nodesPerLayer, numberSinkNodes));
            Topology network = new Topology(numberLayers, nodesPerLayer, numberSinkNodes, ff24);

            // Create the sender node.
            SenderNodeProduction sender =
                    new SenderNodeProduction(network.get_source_list(), ff24, args[0], network.get_min_cut());
            // 'Send' the packet.
            sender.handle();
        }
    }
}
