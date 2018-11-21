import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.*;


public class App {
    /// When set, app will run in debug mode allowing local encoding vectors & packet length field to be set manually.
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

        List<Node> sinkNodes = new ArrayList<>();
        sinkNodes.add(new SinkNode(ff24, "t1"));
        sinkNodes.add(new SinkNode(ff24, "t2"));

        List<Node> intermediateNodes = new ArrayList<>();

        if(DEV_MODE) {
            /// @debug if hardcoding local encoding vectors, specify them below.
            int[] l1 = {3, 7, 2};
            int[] l2 = {1, 2, 4};
            int[] l3 = {4, 5, 3};
            intermediateNodes.add(new IntermediateNodeDebug(sinkNodes, ff24, "i1", l1));
            intermediateNodes.add(new IntermediateNodeDebug(sinkNodes, ff24, "i2", l2));
            intermediateNodes.add(new IntermediateNodeDebug(sinkNodes, ff24, "i3", l3));
            SenderNodeDebug sender = new SenderNodeDebug(intermediateNodes, ff24, args[0], 10);
            sender.handle();
            /// end debug
        } else {
            intermediateNodes.add(new IntermediateNode(sinkNodes, ff24, "i1"));
            intermediateNodes.add(new IntermediateNode(sinkNodes, ff24, "i2"));
            intermediateNodes.add(new IntermediateNode(sinkNodes, ff24, "i3"));
            SenderNodeProduction sender = new SenderNodeProduction(intermediateNodes, ff24, args[0]);
            sender.handle();
        }

    }
}
