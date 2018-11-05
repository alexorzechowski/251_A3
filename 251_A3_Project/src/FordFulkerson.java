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
		Stack.add(destination);	//Add source as 1st on the path
		ArrayList<Integer> pi = new ArrayList<Integer>(adjList.size());
		for(int i=0; i<adjList.size(); i++)
			pi.add(-1);
		return DFS(source,destination, graph,allNodes,adjList,color,numDone, Stack,pi);
	}
	public static ArrayList<Integer> DFS(Integer source, Integer destination, WGraph graph, ArrayList<Integer> allNodes,ArrayList<ArrayList<Integer>> adjList, int[] color, int numDone,ArrayList<Integer> Stack,ArrayList<Integer> pi ){		
		color[source]=1;	//Mark Grey	
		if(source!=destination){
			for(int v: adjList.get(source) ){			
				if(color[v]==0){
					color[v]=1; //Mark Grey - discovered
					System.out.println("AdjList of node "+source+" is "+Arrays.toString(adjList.get(source).toArray()));
					System.out.println("Visiting node "+v);
					pi.set(v, source);
					DFS(v,destination,graph,allNodes,adjList,color, numDone,Stack,pi);
				}				
			}
		}
		color[source]=2;	//Mark Black
		System.out.println("Marked "+source+ " Black");
		numDone++;
		//Construct the path from Source to Dest
		for(int i=0; i<pi.size(); i++){
			int pred=pi.get(destination);
			if(pred==-1){
				System.out.println("No Path!");
				return new ArrayList<Integer>(0);
			}
			Stack.add(pred);
		}
		Collections.reverse(Stack);
		return Stack;
		
	}
	
	
	
	public static void fordfulkerson(Integer source, Integer destination, WGraph graph, String filePath){
		String answer="";
		String myMcGillID = "260610696"; //Please initialize this variable with your McGill ID
		int maxFlow = 0;
		
			// YOUR CODE GOES HERE
		WGraph resG = new WGraph();
		ArrayList<Edge> gEdges = graph.getEdges();
		//ArrayList<Edge> gEdgesSorted = graph.listOfEdgesSorted();
		int n=graph.getNbNodes();
		int[][] gFlows = new int[n][n];
		ArrayList<Integer> resGCaps = new ArrayList<Integer>(gEdges.size());
		int[][] resGFlows = new int[n][n];
		
		//Build Residual Graph
		int[][] resG_edge_directions = new int[n][n];
		for(int i=0; i<gEdges.size(); i++){
			Edge e=gEdges.get(i);
			if(gFlows[e.nodes[0]][e.nodes[1]] <e.weight){
				int c_res=e.weight- gFlows[e.nodes[0]][e.nodes[1]];
				resG.addEdge(new Edge(e.nodes[0],e.nodes[1],c_res));
				resG_edge_directions[e.nodes[0]][e.nodes[1]]=1;	//1=Forward
			}
			if(gFlows[e.nodes[0]][e.nodes[1]] >0){
				resG.addEdge(new Edge(e.nodes[1],e.nodes[0],gFlows[e.nodes[0]][e.nodes[1]] ));
				resG_edge_directions[e.nodes[0]][e.nodes[1]] =2;	//2=backward
				System.out.println("Added backward edge from "+e.nodes[0]+" to "+e.nodes[1]);
			}				
		}
		
		while(!pathDFS(source,destination,resG).isEmpty()){
			//Augment Path
			ArrayList<Integer> path = pathDFS(source,destination,resG);
			System.out.println("Path in resG is "+Arrays.toString(path.toArray()));
			System.out.println("path size is " +path.size());
			ArrayList<Edge> edgesSorted = resG.listOfEdgesSorted();
			int B=edgesSorted.get(edgesSorted.size()-1).weight;	//Start with max possible B =max capacity, then see if we can decrease
			System.out.println("Starting B is " +B);
			for(int i=0; i<path.size()-1; i++){	//Find B for the path found
				Edge e=resG.getEdge(path.get(i), path.get(i+1));
				System.out.println("i is "+i);
				System.out.println("resG is "+resG);
				System.out.println("Edge capacity is " +e.weight);
				if(e.weight - gFlows[e.nodes[0]][e.nodes[1]] <B)					
					B=e.weight - gFlows[e.nodes[0]][e.nodes[1]] ;
			}
			System.out.println("Finishing B is " +B);
			for(int i=0; i<path.size()-1; i++){
				Edge e=resG.getEdge(path.get(i), path.get(i+1));
				int dir=resG_edge_directions[e.nodes[0]][e.nodes[1]] ;
				if(dir==1){	//If forward directed edge, add B to the flow in resG along the path
					gFlows[e.nodes[0]][e.nodes[1]]= 	gFlows[e.nodes[0]][e.nodes[1]]+B;
					System.out.println(Arrays.deepToString(gFlows));
				}
				else
					gFlows[e.nodes[0]][e.nodes[1]]= 	gFlows[e.nodes[0]][e.nodes[1]]-B;
			}
			//ReBuild the Residual Graph! ie Update resG with the new flows
			resG=new WGraph();			
			resG_edge_directions = new int[n][n];
			for(int i=0; i<gEdges.size(); i++){
				Edge e=gEdges.get(i);
				if(gFlows[e.nodes[0]][e.nodes[1]] <e.weight){
					int c_res=e.weight- gFlows[e.nodes[0]][e.nodes[1]];
					resG.addEdge(new Edge(e.nodes[0],e.nodes[1],c_res));
					resG_edge_directions[e.nodes[0]][e.nodes[1]]=1;	//1=Forward
				}
				if(gFlows[e.nodes[0]][e.nodes[1]] >0){
					resG.addEdge(new Edge(e.nodes[1],e.nodes[0],gFlows[e.nodes[0]][e.nodes[1]] ));
					resG_edge_directions[e.nodes[0]][e.nodes[1]] =2;	//2=backward
				}				
			}
		}
		//Calc max flow
		int src = resG.getSource();
		for(int i=0; i<n-1; i++){
			maxFlow = maxFlow+gFlows[src][i];
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
		 //System.out.println(Arrays.toString(pathDFS(g.getSource(),g.getDestination(),g).toArray()));
		 fordfulkerson(g.getSource(),g.getDestination(),g,f.getAbsolutePath().replace(".txt",""));
	 }
}
