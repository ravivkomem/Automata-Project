package Entities;

import java.util.*;

/**
 * This implementation is taken from GeeksForGeeks
 * https://www.geeksforgeeks.org/implementing-generic-graph-in-java/
 */
class Graph<T> {

	// We use Hashmap to store the edges in the graph
	private Map<T, List<T>> map = new HashMap<>();

	// This function adds a new vertex to the graph
	public void addVertex(T s) {
		map.put(s, new LinkedList<T>());
	}

	// This function adds the edge
	// between source to destination
	public void addEdge(T source, T destination) {

		if (!map.containsKey(source))
			addVertex(source);

		if (!map.containsKey(destination))
			addVertex(destination);

		map.get(source).add(destination);
	}

	// This function gives the count of vertices
	public void getVertexCount() {
		System.out.println("The graph has " + map.keySet().size() + " vertex");
	}

	// This function gives the count of edges
	public void getEdgesCount() {
		int count = 0;
		for (T v : map.keySet()) {
			count += map.get(v).size();
		}
		System.out.println("The graph has " + count + " edges.");
	}

	// This function gives whether
	// a vertex is present or not.
	public void hasVertex(T s) {
		if (map.containsKey(s)) {
			System.out.println("The graph contains " + s + " as a vertex.");
		} else {
			System.out.println("The graph does not contain " + s + " as a vertex.");
		}
	}

	// This function gives whether an edge is present or not.
	public void hasEdge(T s, T d) {
		if (map.get(s).contains(d)) {
			System.out.println("The graph has an edge between " + s + " and " + d + ".");
		} else {
			System.out.println("The graph has no edge between " + s + " and " + d + ".");
		}
	}

	public Collection<T> getAdjacentCollection(T s)
	{
		if (map.keySet().contains(s))
		{
			return map.get(s);
		}
		else
		{
			return new ArrayList<T>();
		}
	}
	
	// Prints the adjacency list of each vertex.
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();

		for (T v : map.keySet()) {
			builder.append(v.toString() + ": ");
			for (T w : map.get(v)) {
				builder.append(w.toString() + " ");
			}
			builder.append("\n");
		}

		return (builder.toString());
	}
}