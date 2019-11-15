package networkcoding;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Topology is a class that creates an arbitrarily specified multi-level cascading network topology.
 */
class Topology implements Serializable {
    /**
     * The number of sink nodes in the network topology.
     */
    private int num_sink_nodes;
    /**
     * The number of num_layers of intermediate nodes in the network topology.
     */
    private int num_layers;
    /**
     * Collection of sink nodes in the network topology.
     */
    private List<Node> sink_nodes;
    /**
     * Collection of intermediate num_layers that lie between the source node and the sink nodes.
     */
    private List<Layer> network_layers;
    /**
     * Collection of nodes in the network layer closest to the source node.
     */
    private List<Node> closest_to_source;
    /**
     * The finite field over which arithmetic operations will occur.
     */
    private FiniteField_F_2_n field;
    /**
     * The number of intermediate nodes that each layer contains.
     */
    private int nodes_per_layer;
    /**
     * The network min-cut.
     * Defined as the smallest total weight of the edges which if removed would disconnect the source from the sink.
     */
    private int min_cut;
    /**
     * Default constructor for the network topology object.
     * @param number_layers      the number of num_layers of intermediate nodes in the network.
     * @param nodes_per_layer    the number of intermediate nodes that each layer contains.
     * @param number_sink_nodes  the number of sink nodes that the network contains.
     * @param field              the finite field over which arithmetic operations will occur.
     */
    Topology(int number_layers, int nodes_per_layer, int number_sink_nodes, FiniteField_F_2_n field)
            throws IOException {
        this.num_sink_nodes = number_sink_nodes;
        this.num_layers = number_layers;
        this.nodes_per_layer = nodes_per_layer;
        this.field = field;
        // Ensure that each member of the list is a distinct Layer object.
        this.network_layers = Stream.generate(Layer::new).limit(number_layers).collect(Collectors.toList());
        this.sink_nodes = new ArrayList<>();
        this.min_cut = compute_min_cut(number_layers);
        instantiate_sink_nodes(number_sink_nodes);
        instantiate_network_layers(number_layers);
    }

    /**
     * Creates the required number of sink nodes in the network.
     * @param number_sink_nodes       the number of sink nodes that the network contains.
     */
    private void instantiate_sink_nodes(int number_sink_nodes){
        int count = 0;
        for(int i = 0; i < number_sink_nodes; i++){
            String sink_node_name = String.format("t%d", i);
            sink_nodes.add(new SinkNode(field, sink_node_name, min_cut));
            count ++;
        }
    }

    /**
     * Creates the required num_layers of intermediate nodes in the network.
     * @param number_of_layers      the number of num_layers in the network
     */
    private void instantiate_network_layers(int number_of_layers) throws IOException {
        int count = 0;
        // A container that holds a list of the nodes in the (n+1)th layer.
        // Facilitates the connecting the nth layer of nodes to the (n+1)th layer.
        if(number_of_layers > 1) {
            for (int i = number_of_layers; i > 0; i--) {
                String layer_name = String.format("%d", i);
                // Start with the layer closest to the sink nodes and instantiate its nodes.
                // This layer will send directly to sink nodes.
                if (i == number_of_layers) {
                    // Create the layer object and the nodes that belong to it, connect each node to all sink nodes.
                    instantiate_single_layer( sink_nodes, layer_name, i-1);
                } else { // Connect this layer (n) to the next layer (n+1) (the layer closer to the sink nodes).
                    instantiate_single_layer( network_layers.get(i).nodes_in_layer, layer_name, i-1);
                    // Store the list of nodes in layer closest to the source node in a class field for public access.
                    if( i == 1){
                        closest_to_source = network_layers.get(i-1).nodes_in_layer;
                    }
                }
            }
        }
    }

    /**
     * Creates a single layer object and returns a list of the nodes in the created layer.
     * @param nodes_to_forward      a list of the nodes to which the created layer should forward their data.
     * @param layer_name            the name of the layer.
     */
    private void instantiate_single_layer(List<Node> nodes_to_forward, String layer_name, int layer_number)
            throws IOException {
        List<Node> forward = new ArrayList<>(nodes_to_forward);
        // Modify the Layer object in place (w.r.t. to its location in the list).
        // The fill_fields method of the Layer class instantiates the nodes that make up the layer.
        network_layers.get(layer_number).fill_fields(nodes_per_layer, forward, layer_name, field, min_cut);
    }
    /**
     * "Computes" the network min-cut. In this simplified network topology computing the min-cut is trivial.
     * If a more general network topology needs to be constructed a smarter method would need to be implemented.
     */
    private int compute_min_cut(int number_layers){
        return nodes_per_layer/*number_layers + 1*/;
    }
    /**
     * Provides access to the layer of nodes closest to the source node.
     * @return a list of the nodes in the layer closest to the source node.
     */
    List<Node> get_source_list(){
        return closest_to_source;
    }
    /**
     * Provides access to the network min-cut.
     * @return the network min-cut.
     */
    int get_min_cut(){
        return min_cut;
    }
    /**
     * Provides access to the number of num_layers in the topology.
     * @return the number of num_layers
     */
    int get_num_layers(){ return num_layers;}
    /**
     * Provides access to the number of intermediate nodes per layer in the topology.
     * @return the number of intermediate nodes per layer
     */
    int get_nodes_per_layer(){ return nodes_per_layer;}
    /**
     * Provides access to the number of sink nodes in the topology.
     * @return the number of sink nodes in the network.
     */
    int get_num_sink_nodes(){ return num_sink_nodes;}
}
