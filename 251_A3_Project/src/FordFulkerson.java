import java.io.*;
import java.util.*;




public class FordFulkerson {

	
	public static ArrayList<Integer> pathDFS(Integer source, Integer destination, WGraph graph){
		ArrayList<Integer> Stack = new ArrayList<Integer>();
		// YOUR CODE GOES HERE
		//Start at source, continue until you hit dest
		int[] color = new int[graph.getNbNodes()];
		int[] d = new int[graph.getNbNodes()];
		int time=0;
		int numDone=0;
		for(int i=0; i<color.length; i++)
			color[i]=0;	//Init all nodes to white
		ArrayList<Integer> allNodes = new ArrayList<Integer>();
		for(Edge e: graph.getEdges()){
			if(allNodes.contains(e.nodes[0])==false )
				allNodes.add(e.nodes[0]);
			if(allNodes.contains(e.nodes[1])==false )
				allNodes.add(e.nodes[1]);
		}
		
		ArrayList<ArrayList<Integer>> adjList = new ArrayList<ArrayList<Integer>>(graph.getNbNodes());
		for(int i=0; i<graph.getNbNodes(); i++)
			adjList.add(new ArrayList<Integer>());
		
		for(int i=0; i<graph.getEdges().size(); i++){
			Edge edge=graph.getEdges().get(i);
			int n0=edge.nodes[0];
			int n1=edge.nodes[1];
			adjList.get(n0).add(n1);	//Add n1 to n0's adj list
		}
		
		//DFS(source,destination, graph,allNodes,adjList,color,numDone,Stack);
		return DFS(source,destination, graph,allNodes,adjList,color,numDone, Stack);
	}
	public static ArrayList<Integer> DFS(Integer source, Integer destination, WGraph graph, ArrayList<Integer> allNodes,ArrayList<ArrayList<Integer>> adjList, int[] color, int numDone,ArrayList<Integer> Stack ){
		color[source]=1;	//Mark Grey
		if(source==destination){
			Stack.add(destination);
			return Stack;
		}
		for(int v: adjList.get(source) ){
			if(color[v]==0){
				Stack.add(source);
				DFS(v,destination,graph,allNodes,adjList,color, numDone,Stack);
			}				
		}
		color[source]=2;	//Mark Black
		numDone++;
		if(numDone==graph.getNbNodes() && source!=destination){
			System.out.println("No Path!");
			return new ArrayList<Integer>(0);
		}
		return Stack;
		
	}
	
	
	
	public static void fordfulkerson(Integer source, Integer destination, WGraph graph, String filePath){
		String answer="";
		String myMcGillID = "260610696"; //Please initialize this variable with your McGill ID
		int maxFlow = 0;
		
			// YOUR CODE GOES HERE
		WGraph resG = new WGraph();
		ArrayList<Edge> gEdges = graph.getEdges();
		int[] gFlows = new int[gEdges.size()];
		ArrayList<Integer> resGCaps = new ArrayList<Integer>(gEdges.size());
		ArrayList<Integer> resGFlows = new ArrayList<Integer>(gEdges.size());
		for(int i=0; i<resGFlows.size(); i++)
			resGFlows.set(i, 0);
		//Build Residual Graph
		ArrayList<Integer> resG_edge_directions = new ArrayList<Integer>(gEdges.size());
		for(int i=0; i<gEdges.size(); i++){
			Edge e=gEdges.get(i);
			if(gFlows[i]<e.weight){
				int c_res=e.weight-gFlows[i];
				resG.addEdge(new Edge(e.nodes[0],e.nodes[1],c_res));
				resGCaps.add(c_res);
				resG_edge_directions.add(1);	//1=Forward
			}
			if(gFlows[i]>0){
				resG.addEdge(new Edge(e.nodes[1],e.nodes[0],gFlows[i]));
				resGCaps.add(gFlows[i]);
				resG_edge_directions.add(2);	//2=backward
			}				
		}
		while(!pathDFS(source,destination,resG).isEmpty()){
			//Augment Path
			ArrayList<Integer> path = pathDFS(source,destination,graph);
			int B=Collections.max(resGCaps);	//Start with maximum possible bottleneck
			for(int i=0; i<path.size()-1; i++){	//Find B
				Edge e=resG.getEdge(i, i+1);
				if(e.weight - resGFlows.get(i)<B)
					B=e.weight - resGFlows.get(i);
			}
			for(int i=0; i<path.size()-1; i++){
				int dir=resG_edge_directions.get(i);
				if(dir==1){
					resGFlows.set(i, resGFlows.get(i)+B);
				}
				else
					resGFlows.set(i, resGFlows.get(i)-B);				
			}
			//Update resG with the new flows
			for(int i=0; i<path.size(); i++){
				Edge e=resG.getEdge(i, i+1);
				resG.setEdge(e.nodes[0], e.nodes[1], path.get(i));
			}
		}
		//Calc max flow
		resG.getSource();
		ArrayList<Edge> resG_edges = resG.getEdges();
		for(Edge e: resG_edges){
			if(e.nodes[0]==0)
				maxFlow=maxFlow+e.weight;
		}
		
			//End Your code
		answer += maxFlow + "\n" + graph.toString();	
		writeAnswer(filePath+myMcGillID+".txt",answer);
		System.out.println(answer);
	}
	
	
	public static void writeAnswer(String path, String line){
		BufferedReader br = null;
		File file = new File(path);
		// if file doesnt exists, then create it
		
		try {
		if (!file.exists()) {
			file.createNewFile();
		}
		FileWriter fw = new FileWriter(file.getAbsoluteFile(),true);
		BufferedWriter bw = new BufferedWriter(fw);
		bw.write(line+"\n");	
		bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (br != null)br.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
	
	 public static void main(String[] args){
		 String file = args[0];
		 File f = new File(file);
		 WGraph g = new WGraph(file);	
		 System.out.println(g);
		 System.out.println(Arrays.toString(pathDFS(g.getSource(),g.getDestination(),g).toArray()));
		 //fordfulkerson(g.getSource(),g.getDestination(),g,f.getAbsolutePath().replace(".txt",""));
	 }
}
