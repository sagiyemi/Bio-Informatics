package fasta;


import java.util.ArrayList;
import java.util.Iterator;

import org.jgrapht.DirectedGraph;
import org.jgrapht.Graph;
import org.jgrapht.graph.*;
import org.jgrapht.traverse.DepthFirstIterator;
import org.jgrapht.traverse.TopologicalOrderIterator;


public class DiagonalRunsChainer {

	private DefaultDirectedWeightedGraph<DiagonalRun,DefaultWeightedEdge> dg;
	private ArrayList<DiagonalRun> diagArr;
	private DiagonalRun s = null;
	private DiagonalRun t = null;
	private int from = 0;
	private int to = 0;
	
	
	
	public DiagonalRunsChainer(ArrayList<DiagonalRun> diagArr) {
		this.dg = new DefaultDirectedWeightedGraph<DiagonalRun,DefaultWeightedEdge>(DefaultWeightedEdge.class);
		this.diagArr = diagArr;
	}
	
	private void buildGraph() {
		this.s = new DiagonalRun(0, 0);
		this.t = new DiagonalRun(999999, 999999);
		dg.addVertex(s);
		dg.addVertex(t);

		// Create edges: S->V (score=2*w(v)) and V->T (score=0)
		for (DiagonalRun dr : diagArr) {
			dg.addVertex(dr);
			DefaultWeightedEdge es = dg.addEdge(s, dr);
			DefaultWeightedEdge et = dg.addEdge(dr, t);
			dg.setEdgeWeight(es, 2*dr.getScore());
			dg.setEdgeWeight(et, 0);
		}
		
		int xdiff,ydiff,weight;
		// Create edges between vertexes
		for (DiagonalRun drSource : diagArr) {
			for (DiagonalRun drTarget : diagArr) {
				if ((drSource!=drTarget) && (drTarget!=s) && (drSource!=s) && (drTarget!=t) && (drSource!=t)) {
					xdiff = drTarget.getStartingX() - (drSource.getEndingX());
					ydiff = drTarget.getStartingY() - (drSource.getEndingY());
					if ((xdiff >= 0) && (ydiff >=0)) {
						DefaultWeightedEdge e = dg.addEdge(drSource, drTarget);
						weight = 2*drTarget.getScore() - Math.max(xdiff, ydiff);
						dg.setEdgeWeight(e, weight);
					}		
				}
			}
		}
	}
	
	private void findHeaviestPath() {
		DirectedGraph<DiagonalRun , DefaultWeightedEdge> dgraph = this.dg;
		Iterator<DiagonalRun> iter = new TopologicalOrderIterator<DiagonalRun, DefaultWeightedEdge>(dgraph);
		DiagonalRun drSource = null;
		DiagonalRun drTarget = null;
		this.s.pi = null;
		this.s.d = 0;
		double c;
		while (iter.hasNext()) {
			drSource = iter.next();
			for (DefaultWeightedEdge edge : dgraph.outgoingEdgesOf(drSource)) {		// for every vertex in Adj
//				System.out.println(edge);
				drTarget = dg.getEdgeTarget(edge);
				c = drSource.d + dg.getEdgeWeight(edge);
				if (c > drTarget.d) {	// Relax
//					System.out.println(c + " > " + drTarget.d);
					drTarget.d = c;
					drTarget.pi = drSource;
//					System.out.println(drTarget.pi == drTarget);
				}	
			}
		}
//		System.out.println(this.t.pi);
	}
	
	private void backTrace() {
		DiagonalRun dr = this.t;
		if (dr.pi == null) {
			this.from = 0;
			this.to = 0;
		} else {
			while (this.s != dr.pi) {
//			System.out.println(dr);
				dr = dr.pi;
			}
//		System.out.println(dr);
			this.from = dr.getStartingY()-dr.getStartingX();
			this.to = this.t.pi.getEndingY()-dr.getEndingX();
		}
	}
	
	
	private void printGraph() {
        //Print out the graph to be sure it's really complete
		Graph gra = dg;
        Iterator<Object> iter =
            new DepthFirstIterator<Object, DefaultWeightedEdge>(gra);
        Object vertex;
        while (iter.hasNext()) {
            vertex = iter.next();
            System.out.println(
                "Vertex " + vertex.toString() + " is connected to: "
                + gra.edgesOf(vertex).toString());
        }
		
	}
	
	public int getFrom() { 
		return this.from;
	}
	
	public int getTo() {
		return this.to;
	}
	
	
	/**
	 * 
	 * @return the weight of the last node in the Graph
	 */
	public double connectDiagonals () {
		buildGraph();
		findHeaviestPath();
		backTrace();
		return this.t.d;
	}
	
	public void Test() {
		System.out.println("hello world");
//		System.out.println(diagArr);
		buildGraph();
		findHeaviestPath();
		backTrace();
//		printGraph();
		
//		System.out.println(dg);
		
	}
	
	
	
	
	
	
	
	
	
	
}
