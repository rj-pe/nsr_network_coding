
import java.util.ArrayList;
import java.util.List;



/**
 * create network
 * Read file
 * split into packages
 * merge them on sink nodes.
 *
 */
public class App {

    public static void main(String args[]) {

        FiniteField_F_2_n ff24 = FiniteField_F_2_n.getInstance();

        List<Node> sinkNodes = new ArrayList<>();
        sinkNodes.add(new SinkNode(ff24, "t1"));
        sinkNodes.add(new SinkNode(ff24, "t2"));

        List<Node> intermediateNodes = new ArrayList<>();
        intermediateNodes.add(new IntermediateNode(sinkNodes, ff24, "i1"));
        intermediateNodes.add(new IntermediateNode(sinkNodes, ff24, "i2"));
        intermediateNodes.add(new IntermediateNode(sinkNodes, ff24, "i3"));

        SenderNode sender = new SenderNode(intermediateNodes, ff24, "input.txt");
        sender.handle();

        //TODO: build a method that verifies contents of the bodies of the sent packet and the rec'vd and decoded packet match.
        // Should return an array of the indices of non-matches.

        // verifies that what is received and decoded by sink nodes matches the original packet contents
        // original packet contents stored in : sender.data[]
        // received and decoded packet stored in : sinkNode.data[]
        // sinkNodes.get(0).;
    }
}
