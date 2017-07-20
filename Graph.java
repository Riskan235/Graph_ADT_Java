import java.util.*;

public class Graph<N, E> {

/** <b>Graph</b> represents a mutable direct labeled multi-graph, which consists of a collection of
 * nodes and edges. Each edge connects a pair of nodes in one direction only. And edge from node1 to
 * node2 indicates that node2 is directly reachable from node1. Any number of edges is allowed between
 * a pair of nodes. Every edge has a label containing information of some sort. In this Graph class,
 * edges with same label, starting node and ending node are not allowed (i.e. there can exist at most
 * one edge between one starting node and one end node, with one specific label).
 */

	 // Abstract Function:
	 // 	 This class represents a graph. All the node that the graph contains is stored in
	 //  nodeToChildren.keySet(). For each node n stored in the graph, nodeToChildren.get(n).keySet()
	 //  stores all of n's child nodes. And for each of n's child c, nodeToChildren.get(n).get(c) contains
	 //  all the edges that connects n and c directly, and from n to c.
	 // 
	 // Representation Invariant for every Graph g:
	 //  * nodeToChildren is never null;
	 //  * For every key in nodeToChildren, the corresponding Map<String, Set<String>> is never null.
	 //  * For every key in nodeToChildren.get(key), the corresponding Set<String> is never null.
	
	private Map<N, Map<N, Set<E>>> nodeToChildren;
	private boolean disableCheckRep;
	
	/**
	 * Constructs an empty Graph object.
	 */
	public Graph () {
		nodeToChildren = new HashMap<N, Map<N, Set<E>>>();
		disableCheckRep = false;
		checkRep();
	}
	
	/**
	 * Constructs an empty Graph with the option of whether or not to disable representation invarient check.
	 * @param checkRep: flag to disable/not disable representation invarient check.
	 * 		  if checkRep == true, representation invarient check will be disabled.
	 */
	public Graph (boolean disableCheckRep) {
		this();
		this.disableCheckRep = disableCheckRep;
	}
	
	/**
	 * Add a node <b>node</b> to the graph.
	 * @param node: the node being added to the graph
	 * @modifies this
	 * @effects add node to the graph
	 * @throws IllegalStateException if the passes in node is already contained in the graph.
	 */
	public void addNode (N node) {
		if (nodeToChildren.keySet().contains(node)) {
			throw new IllegalStateException();
		} else {
			nodeToChildren.put(node, new HashMap<N, Set<E>>());
		}
		checkRep();
	}
	
	/**
	 * Add an edge <b>edge</b> between parentNode and childNode in the graph.
	 * @param parentNode: start of the being-added edge
	 * @param childNode: finish of the being-added edge
	 * @param edge: edge being added to the graph, connecting parentNode and childNode
	 * @modifies this
	 * @effects connects existing nodes parentNode and childNode with edge, make childNode a child
	 * 			of parentNode
	 * @throws IllegalStateException if the graph does not contain either parentNode or childNode
	 * 		   or the graph already ontains an edge with same lable and goes from the same parent
	 * 		   node to the same child node.
	 */
	public void addEdge (N parentNode, N childNode, E edge) {
		if (!this.containsNode(parentNode) || !this.containsNode(childNode)) {
			throw new IllegalStateException();
		} else {
			Map<N, Set<E>> childrenAndEdges = nodeToChildren.get(parentNode);
			if (childrenAndEdges.containsKey(childNode)) {
				Set<E> edges = childrenAndEdges.get(childNode);
				if (edges.contains(edge)) {
					throw new IllegalStateException();
				} else {
					edges.add(edge);
				}
			} else {
				Set<E> edgeSet = new HashSet<E>();
				edgeSet.add(edge);
				childrenAndEdges.put(childNode, edgeSet);
			}
		}
		checkRep();
	}
	
	/**
	 * Check if a node is present in this graph.
	 * @param node - Node being checked whether it is in the graph already.
	 * @return true if and only if this graph contains the input argument node.
	 */
	public boolean containsNode(N node) {
		return nodeToChildren.keySet().contains(node);
	}
	
	/**
	 * Check if an edge with specified label, beginning and ending is present in this graph.
	 * @param parent: beginning node of the edge which will be looked up in the graph
	 * @param child: end of the edge which will be looked up in the graph
	 * @param edgeName: label of the edge which will be looked up in the graph
	 * @return true if and only if such an edge that goes from parent to child, and with the name edgeName
	 * 		   is in the graph.
	 */
	public boolean containsEdge(N parent, N child, E edgeName) {
		if (!this.containsNode(parent) || !this.containsNode(child) ||
			!this.containsChild(parent, child)) {
			return false;
		} else if (nodeToChildren.get(parent).get(child).contains(edgeName)) {
			return true;
		}
		return false;
	}
	
	/**
	 * Checks if <b>childNode</b> is a child of <b>parentNode</b>
	 * @param parentNode: a node in the graph which is expected to be the parent of childNode
	 * @param childNode: a node in the graph which is expected to be the child of parentNode
	 * @return true if and only if childNode is a child of parentNode
	 * @throws IllegalStateException if either of parentNode is not in the graph.
	 */
	public boolean containsChild(N parentNode, N childNode) {
		if (!this.containsNode(parentNode)) {
			throw new IllegalStateException();
		}
		return nodeToChildren.get(parentNode).containsKey(childNode);
	}
	
	/**
	 * Returns a set of all nodes present in the graph.
	 * @return an unmodifiableSet of all the nodes contained in this graph.
	 */
	public Set<N> getNodes () {
		return Collections.unmodifiableSet(nodeToChildren.keySet());
	}
	
	/**
	 * Returns a set of all child nodes of the given node.
	 * @param parentNode: the parent node of desired child nodes
	 * @return an unModifiableMap, which has its keys representing all of parentNode's children, and
	 * 		   the Set corresponding to each child is the set of all edges that connects parentNode and
	 * 		   that child node.
	 * @throws IllegalStateException if the input argument parentNode is not contained in the graph.
	 */
	public Map<N, Set<E>> getChildren (N node) {
		if (!nodeToChildren.containsKey(node)) {
			throw new IllegalStateException();
		} else {
			return Collections.unmodifiableMap(nodeToChildren.get(node));
		}
	}
	
	/**
	 * Check the representation invariant of this graph.
	 */
	private void checkRep() {
		assert(nodeToChildren != null);
		if (!disableCheckRep) {
			for (N parent: nodeToChildren.keySet()) {
				assert(nodeToChildren.get(parent) != null);
				Map<N, Set<E>> childrenAndEdges = nodeToChildren.get(parent);
				for (N child: childrenAndEdges.keySet()) {
					assert(childrenAndEdges.get(child) != null);
				}
			}
		}
	}
}
