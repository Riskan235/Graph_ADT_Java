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
	 * Finds and returns the shortest path between two nodes in a graph. If there are multiple shortest path, the one
	 * which is lexicographically least will be returned.
	 * @param graph where the path is looked for
	 * @param start the start node of the path
	 * @param end the ending node of the path
	 * @requires <b>start</b> and <b>end</b> are present in the graph.
	 * @return a List if there is a valid path between two nodes. Which <b>start</b> is stored in
	 *         index 0 and <b>end</b> is stored in the last index. All the even indices stores node and
	 *         odd indices <i>i</i> stores the edge which connects from node <i>i - 1</i> to node <i>i + 1</i>.
	 *         Returns empty list if <b>start</b> and <b>end</b> are identical and represent in the MarvelPath.
	 *         Returns <b>null</b> if no path between <b>start</b> or <b>end</b> nodes.
	 * @throws <b>IllegalArgumentException</b> if either <b>start</b> or <b>end</b> is not present in MarvelPath.
	 */
	public static List<String> shortestPath(Graph<String, String> graph, String start, String end) {
		Queue<String> nodesToVisit = new LinkedList<String>();
		Map<String, String> nodesToPaths = new HashMap<String, String>();
		Map<String, String> nodesToParent = new HashMap<String, String>();
		Set<String> seen = new HashSet<String>();
		
		nodesToVisit.add(start);
		seen.add(start);
		while (!nodesToVisit.isEmpty()) {
			String currentNode = nodesToVisit.remove();
			seen.add(currentNode);
			if (currentNode.equals(end)) {
				List<String> path = new ArrayList<String>();
				path.add(currentNode);
				while (!currentNode.equals(start)) {
					path.add(0, nodesToPaths.get(currentNode));
					currentNode = nodesToParent.get(currentNode);
					path.add(0, currentNode);
				}
				return path;
			} else {
				Map<String, Set<String>> childrenWithEdge = graph.getChildren(currentNode);
				List<String> children = new ArrayList<String>(childrenWithEdge.keySet());
				Collections.sort(children);
				for (int i = 0; i < children.size(); i ++) {
					if (!seen.contains(children.get(i)) && !nodesToVisit.contains(children.get(i))) {
						Set<String> edges = childrenWithEdge.get(children.get(i));
						List<String> edgeList = new ArrayList<String>(edges);
						Collections.sort(edgeList);
						nodesToPaths.put(children.get(i), edgeList.get(0));
						nodesToVisit.add(children.get(i));
						nodesToParent.put(children.get(i), currentNode);
					}
				}
			}
		}
		return null;
	}

	/**
	 * Find and return the least weight path between two nodes <b>start</b> and <b>end</b> in <b>graph</b> with
	 * 	Dijkstra's Algorithm.
	 * @param graph which the path would be searched from.
	 * @param start the starting point of the path we would like to find.
	 * @param end the end point of the path we would like to find.
	 * @requires no parameter is null; <b>start</b> and <b>end</b> are present in <b>graph</b>.
	 * @return a list formatted in special ways that represents the path between <b>start</b> and <b>end</b>. The first
	 * 	element in returned list would always be <b>start</b> and last would always be <b>end</b>. 
	 * 		* if list = {start, node1, node2,..., end}, it means that the path starts from <b>start</b>, then node1, then
	 * 		  node2,..., until <b>end</b>.
	 * 		* if <b>start</b>.equals(<b>end</b>), then the list would only contain one element, which is <b>start</b>/<b>end</b>
	 * 		   returns null if there is no valid path between <b>start</b> and <b>end</b> in <b>graph</b>.
	 */
	public static <N> List<N> leastWeightedPath(Graph<N, Double> graph, N start, N end) {
		
		Map<N, Double> distanceFromStart = new HashMap<N, Double>();
		Map<N, N> nodeToPrevious = new HashMap<N, N>();
		Set<N> done = new HashSet<N>();
		
		// Use a comparator to sort active list, which nodes with least total distance from front would
		// be placed to the front of active list.
		Comparator<N> comparator = (x, y) -> distanceFromStart.get(x).compareTo(distanceFromStart.get(y));
		Queue<N> active = new PriorityQueue<N>(comparator);
		
		// Set up
		active.add(start);
		distanceFromStart.put(start, 0.0);
		nodeToPrevious.put(start, start);
		
		while (!active.isEmpty()) {
			// Removes the node with least total weight from start, now this node is done.
			N current = active.remove();
			done.add(current);
			
			if (current.equals(end)) {
				List<N> path = new ArrayList<N>();
				N currentNode = end;
				path.add(end);
				while (!currentNode.equals(start)) {
					currentNode = nodeToPrevious.get(currentNode);
					path.add(0, currentNode);
				}
				return path;
			} else {
				Map<N, Set<Double>> children = graph.getChildren(current);
				for (N child: children.keySet()) {
					if (!done.contains(child)) { // Add unexplored nodes to active to be explored
						double currentDistance = distanceFromStart.get(current) + minWeight(children.get(child)).get(0);
						if (active.contains(child)) {
							// If this node is already in the active list, we update its priority if necessary.
							double previousDistance = distanceFromStart.get(child);
							if (previousDistance > currentDistance) {
								// Update priority when the old distance is larger than new distance.
								nodeToPrevious.put(child, current);
								distanceFromStart.put(child, currentDistance);
								active.remove(child);
								active.add(child);
							}
						} else {
							nodeToPrevious.put(child, current);
							distanceFromStart.put(child, currentDistance);
							active.add(child);
						}
					}
				}
			}
		}
		return null;
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
