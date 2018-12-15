import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.*;


public class App {
    /// When DEV_MODE is set, app will run in debug mode allowing local encoding vectors & packet length field to be set manually.
    static final boolean DEV_MODE = false;


    public static void main(String args[]) throws IOException {

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

        FiniteField_F_2_n ff24 = FiniteField_F_2_n.getInstance();


        List<Node> intermediateNodes = new ArrayList<>();

        if(DEV_MODE) {
            ///TODO: implement network topology functionality in DEV_MODE.
            /// @debug if hardcoding local encoding vectors, specify them below.
            List<Node> sinkNodes = new ArrayList<>();
            sinkNodes.add(new SinkNode(ff24, "t1", 3));
            sinkNodes.add(new SinkNode(ff24, "t2", 3));
            int[] l1 = {14, 5, 7};
            int[] l2 = {6, 4, 13};
            int[] l3 = {0, 1, 0};
            intermediateNodes.add(new IntermediateNodeDebug(sinkNodes, ff24, "i1", l1, 3));
            intermediateNodes.add(new IntermediateNodeDebug(sinkNodes, ff24, "i2", l2, 3));
            intermediateNodes.add(new IntermediateNodeDebug(sinkNodes, ff24, "i3", l3, 3));
            SenderNodeDebug sender = new SenderNodeDebug(intermediateNodes, ff24, args[0], 100, 3);
            sender.handle();
            /// end debug
        } else {
            // Create the network over which the packets will be sent.
            // Network parameters should be specified when creating the network object.
            Topology network = new Topology(3, 5, 4, ff24);
            log.log(Level.INFO,
                    String.format("\nlayers: %d\nnpl: %d\nsinks: %d\n",
                            network.get_num_layers(), network.get_nodes_per_layer(), network.get_num_sink_nodes()));
            // Create the sender node.
            SenderNodeProduction sender = new SenderNodeProduction(network.get_source_list(), ff24, args[0], network.get_min_cut());
            // 'Send' the packet.
            sender.handle();
        }
    }
}
