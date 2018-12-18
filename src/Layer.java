import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Layer is a class that creates a layer within a cascaded network topology.
 */
class Layer {
    /**
     * Collection of the destination nodes to which members of this layer should forward their encoded message to.
     */
    private List<Node> nodes_to_forward;
    /**
     * Collection of the nodes that are part of this layer.
     */
    List<Node> nodes_in_layer;
    /**
     * A string that represents the name of this layer, the name of each node includes the corresponding layer name.
     */
    private String layer_name;
    /**
     * The number of nodes in this layer.
     */
    private int number_of_nodes_in_layer;
    /**
     * The finite field over which arithmetic operations will occur.
     */
    private FiniteField_F_2_n field;
    /**
     * Uninitialized constructor for the Layer object, acts like a default constructor.
     * TODO: Possibly redundant as a Java generated default constructor will accomplish the same and no other constructors are present.
     */
    Layer(){
        this.nodes_in_layer = null;
        this.nodes_to_forward = null;
        this.layer_name = null;
        this.number_of_nodes_in_layer = 0;
        this.field = null;
    }

    /**
     * Creates, and names the required nodes for the layer. Stores created nodes in nodes_in_layer.
     * @return returns the number of nodes created.
     */
    private int instantiate_nodes(int min_cut){
        Logger logger = Logger.getLogger("log");
        int j = 0;
        for(; j < number_of_nodes_in_layer; j++) {
            String node_name = String.format("(" + layer_name + ", %d)", j);
            logger.log(Level.INFO, node_name);
            Node node = new IntermediateNodeProduction(nodes_to_forward, field, node_name, min_cut);
            nodes_in_layer.add( node);
        }
        return j;
    }
    /**
     * Set the fields of the layer.
     * This is not handled by the constructor b/c Layer objects are created before they can be fully defined.
     * @param nodes             the number of nodes that the layer contains.
     * @param nodes_to_forward  a collection of the destination nodes to which the members of this layer should forward.
     * @param layer_name        the name of the layer.
     * @param field             the finite field over which arithmetic operations will occur.
     */
    void fill_fields(int nodes, List<Node> nodes_to_forward, String layer_name, FiniteField_F_2_n field, int min_network_cut){
        this.nodes_in_layer = new ArrayList<>();
        this.nodes_to_forward = nodes_to_forward;
        this.layer_name = layer_name;
        this.number_of_nodes_in_layer = nodes;
        this.field = field;
        instantiate_nodes(min_network_cut);
    }
}
