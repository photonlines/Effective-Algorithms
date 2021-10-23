import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by User on 4/9/2016.
 */
public class AdjacencyListGraph<VertexT, EdgeT extends Comparable<EdgeT>> {

    private Map<VertexT, Set<Edge<VertexT, EdgeT>>> edgeMap;

    private final EdgeT NO_EDGE_WEIGHT = null;

    private enum GraphType {
        DIRECTED, UNDIRECTED
    }

    private final GraphType graphType;

    private static final GraphType DEFAULT_GRAPH_TYPE = GraphType.DIRECTED;

    private class Edge<VertexT, EdgeT extends Comparable<EdgeT>> {

        private final VertexT vertex;
        private final EdgeT weight;

        public Edge(VertexT vertex, EdgeT weight) {
            this.vertex = vertex;
            this.weight = weight;
        }

        private VertexT getDestinationVertex() {
            return vertex;
        }

        public EdgeT getEdgeWeight() {
            return weight;
        }
    }

    private AdjacencyListGraph(GraphType graphType) {
        this.graphType = graphType;
        this.edgeMap = new HashMap<VertexT, Set<Edge<VertexT, EdgeT>>>();
    }

    public static <VertexT,  EdgeT extends Comparable<EdgeT>> AdjacencyListGraph<VertexT, EdgeT> newInstance() {
        return new AdjacencyListGraph<VertexT, EdgeT>(DEFAULT_GRAPH_TYPE);
    }

    public static <VertexT,  EdgeT extends Comparable<EdgeT>> AdjacencyListGraph<VertexT, EdgeT> newInstance(GraphType graphType) {
        return new AdjacencyListGraph<VertexT, EdgeT>(graphType);
    }

    public GraphType getGraphType() { return this.graphType; }

    private void addEdge(VertexT vertex, Set<Edge<VertexT, EdgeT>> edge) {
        this.edgeMap.put(vertex, edge);
    }

    public Set<Edge<VertexT, EdgeT>> getNeighbouringEdges(VertexT vertex) {
        return edgeMap.containsKey(vertex) ? edgeMap.get(vertex) : new HashSet<>();
    }

    public boolean addVertex(VertexT vertex) {
        if (vertex == null || edgeMap.containsKey(vertex)) return false;
        edgeMap.put(vertex, new HashSet());
        return true;
    }

    public boolean setEdgeWeight(VertexT sourceVertex, VertexT destinationVertex, EdgeT weight) {
        return setEdge(sourceVertex, destinationVertex, weight, this.getGraphType() == GraphType.UNDIRECTED ? true: false);
    }

    public boolean addEdge(VertexT sourceVertex, VertexT destinationVertex, EdgeT weight) {
        return setEdge(sourceVertex, destinationVertex, weight);
    }

    public boolean setEdge(VertexT sourceVertex, VertexT destinationVertex, EdgeT weight) {
        return setEdge(sourceVertex, destinationVertex, weight, this.getGraphType() == GraphType.UNDIRECTED ? true: false);
    }

    private boolean setEdge(VertexT sourceVertex, VertexT destinationVertex, EdgeT weight, boolean addDestinationEdge) {
        if (sourceVertex == null || destinationVertex == null || weight == null ) throw new IllegalArgumentException();
        Set neighbouringEdges = getNeighbouringEdges(sourceVertex);
        neighbouringEdges.add(new Edge(destinationVertex, weight));
        addEdge(sourceVertex, neighbouringEdges);
        if (addDestinationEdge) setEdge(destinationVertex, sourceVertex, weight, false);
        return true;
    }

    public boolean hasVertex(VertexT vertex) {
        if (vertex == null) return false;
        if (edgeMap.containsKey(vertex)) return true;
        return edgeMap.entrySet().stream()
                .anyMatch(mapEntry -> mapEntry.getValue().stream()
                        .anyMatch(edge -> vertex.equals(edge.getDestinationVertex())));
    }

    public boolean hasEdge(VertexT sourceVertex, VertexT destinationVertex) {
        if (sourceVertex == null || destinationVertex == null) return false;
        Set<Edge<VertexT, EdgeT>> edgeSet = getNeighbouringEdges(sourceVertex);
        return edgeSet.stream()
                .anyMatch(x -> destinationVertex.equals(x.getDestinationVertex()));

    }

    public EdgeT getEdgeWeight(VertexT sourceVertex, VertexT destinationVertex) {
        if (sourceVertex == null || destinationVertex == null) return NO_EDGE_WEIGHT;
        Set<Edge<VertexT, EdgeT>> edgeSet = getNeighbouringEdges(sourceVertex);
        return edgeSet.stream()
                .filter(edge -> destinationVertex.equals(edge.getDestinationVertex()))
                .findFirst()
                .orElse(new Edge<VertexT, EdgeT>(destinationVertex, NO_EDGE_WEIGHT))
                .getEdgeWeight();
    }


    public boolean removeEdge(VertexT sourceVertex, VertexT destinationVertex) {
        return removeEdge( sourceVertex, destinationVertex, this.getGraphType() == GraphType.UNDIRECTED ? true : false);
    }

    private boolean removeEdge(VertexT sourceVertex, VertexT destinationVertex, boolean removeDestinationEdge) {
        if (sourceVertex == null || destinationVertex == null) return false;
        Set<Edge<VertexT, EdgeT>> neighbourEdges = getNeighbouringEdges(sourceVertex);
        int initialSize = neighbourEdges.size();
        Set<Edge<VertexT, EdgeT>> newNeighboursCollection = neighbourEdges.stream()
                .filter(edge -> !destinationVertex.equals(edge.getDestinationVertex()))
                .collect(Collectors.toSet());
        int newSize = newNeighboursCollection.size();

        edgeMap.put(sourceVertex, newNeighboursCollection);

        if (removeDestinationEdge)
            if (removeEdge(sourceVertex, destinationVertex, false))
                return true;

        return newSize < initialSize;
    }

    public boolean removeVertex(VertexT removeVertex) {
        if (removeVertex == null) return false;
        if (edgeMap.containsKey(removeVertex))
            edgeMap.remove(removeVertex);
        Set<VertexT> vertexesWithInboundEdges = edgeMap.entrySet().stream()
                .filter(mapEntry -> mapEntry.getValue().stream()
                        .anyMatch(edge -> removeVertex.equals(edge.getDestinationVertex())))
                .map(Map.Entry::getKey)
                .collect(Collectors.toSet());

        for (VertexT vertex : vertexesWithInboundEdges) {
            removeEdge(vertex, removeVertex);
        }

        return true;
    }

    public Iterator<VertexT> getBreadthFirstIterator(VertexT startVertex) {

        if (startVertex == null) return null;

        List <VertexT> resultList = new ArrayList<>();
        Deque<VertexT> transverseQueue = new ArrayDeque<>();
        Map<VertexT, Boolean> exploredMap = new HashMap<>();

        transverseQueue.push(startVertex);
        resultList.add(startVertex);
        exploredMap.put(startVertex, Boolean.TRUE);

        while (!transverseQueue.isEmpty()) {

            VertexT currentVertex =transverseQueue.poll();

            Set<VertexT> unvisitedNeighbours = getNeighbouringEdges(currentVertex).stream()
                    .filter(edge -> exploredMap.get(edge.getDestinationVertex()) != Boolean.TRUE)
                    .map(Edge::getDestinationVertex)
                    .collect(Collectors.toSet());

            for (VertexT neightbourVertex: unvisitedNeighbours) {
                transverseQueue.push(neightbourVertex);
                exploredMap.put(neightbourVertex, Boolean.TRUE);
                resultList.add(neightbourVertex);
            }

        }

        return resultList.iterator();

    }

    public Iterator<VertexT> getDepthFirstIterator(VertexT startVertex) {

        if (startVertex == null) return null;

        List<VertexT> resultList = new ArrayList<>();
        Map<VertexT, Boolean> exploredMap = new HashMap<>();

        getDepthFirstIterator(startVertex, resultList, exploredMap);

        return resultList.iterator();

    }

    private void getDepthFirstIterator (VertexT currentVertex, List<VertexT> resultList, Map<VertexT, Boolean> exploredMap) {

        resultList.add(currentVertex);
        exploredMap.put(currentVertex, Boolean.TRUE);

        Set<VertexT> unvisitedNeighbours = getNeighbouringEdges(currentVertex).stream()
                .filter(edge -> exploredMap.get(edge.getDestinationVertex()) != Boolean.TRUE)
                .map(Edge::getDestinationVertex)
                .collect(Collectors.toSet());

        for (VertexT neightbourVertex: unvisitedNeighbours) {
            if (exploredMap.get(neightbourVertex) != Boolean.TRUE) getDepthFirstIterator(neightbourVertex, resultList, exploredMap);
        }

    }

    public static void main(String[] args) {

        AdjacencyListGraph<Integer, String> g = AdjacencyListGraph.newInstance(GraphType.DIRECTED);

        g.setEdge(8, 5, "String Edge 1");
        g.setEdge(8, 20, "String edge 2");
        g.addEdge(15, 27, "2");
        g.setEdge(8, 5, "String edge 2");
        g.setEdge(3, 20, "String edge 2");
        g.setEdge(1, 20, "String edge 2");
        g.setEdge(20, 5, "String edge 2");
        g.setEdge(8, 2, "String edge 2");
        g.setEdge(5, 15, "String edge 2");
        g.setEdge(3, 1, "String edge 2");
        g.setEdge(5, 8, "String edge 2");
        g.setEdge(5, 8, "String edge 2");
        g.setEdge(2, 22, "String edge 2");
        g.setEdge(22, 18, "String edge 2");
        g.setEdge(18, 2, "String edge 2");

        //g.removeVertex(8);


        //System.out.println(g.getEdgeWeight(3, 1));

        g.setEdgeWeight(3, 1, "Some other string");

        //System.out.println(g.getEdgeWeight(3, 1));
        //System.out.println(g.getEdgeWeight(1, 3));

        Iterator<Integer> iter = g.getBreadthFirstIterator(8);
        while(iter.hasNext()) System.out.println(iter.next());




    }
}
