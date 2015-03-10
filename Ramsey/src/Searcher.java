import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.math.BigInteger;
import com.wolfram.jlink.*;

public class Searcher {

	//node count of the graph
	public int graphSize;
	
	//maximum clique size in a valid graph
	public int maxCliqueSizeAllowed;

	public final int totalGraphCount = 352717;
	public int successfulGraphs = 0;
	//set of relations of each node on the circulant graph.
	//where bit 0 represents a connection to 1;
	//bit 1 represents a connection to 2;
	//to bit n-1
		
	
	public Searcher(int graphSize, int maxCliqueSize){
		this.graphSize = graphSize;
		this.maxCliqueSizeAllowed = maxCliqueSize;
	}
	
//c:\program files\wolfram research\mathematica\10.0\mathkernel.exe
	public static void main(String[] argv) {

		KernelLink ml = null;

	        try {
	            ml = MathLinkFactory.createKernelLink(argv);
	        } catch (MathLinkException e) {
	            System.out.println("Fatal error opening link: " + e.getMessage());
	            return;
	        }

	        try {
	            // Get rid of the initial InputNamePacket the kernel will send
	            // when it is launched.
	            ml.discardAnswer();
	            
	            //rip r(5,5) > 46
	            //try for r(5,5) >=44
	            Searcher s = new Searcher(17,3);
	        
	            s.search(ml);
	        
	            
	        } catch (MathLinkException e) {
	            System.out.println("MathLinkException occurred: " + e.getMessage());
	        } finally {
	            ml.close();
	        }
	    }
		
	
	
	public void search(KernelLink ml){
		
		//set up the wolfram environment
		
		
		BigInteger relationSet = BigInteger.ZERO;
		int DEBUGcount=0;
		
		//only need to analyze half the graphs, complement will be done in analysis.
		//accidentally generates one extra one after it finds the last one
		//no worries though
		while(relationSet.longValue() < Math.pow(2, ((graphSize-1)-1)/2)){
			relationSet = getNextRelationSet(relationSet);
			//go ahead and get a symmetrized version
			BigInteger symSet = symmetrizeSet(relationSet);
			String command1 = getMathematicaCliqueCommand(symSet,false);
			String command2 = getMathematicaCliqueCommand(symSet,true);
			

            String result1 = ml.evaluateToOutputForm(command1, 0);
            System.out.println(result1);
            
            String result2 = ml.evaluateToOutputForm(command2, 0);
            System.out.println(result2);
            
            int cliques1 = 1 + result1.length() - result1.replace(",", "").length();
            int cliques2 = 1 + result2.length() - result2.replace(",", "").length();
            if(cliques1 <= maxCliqueSizeAllowed && cliques2 <= maxCliqueSizeAllowed){
            	System.out.println("Winner!");
            	System.out.println(command1);
            	System.out.println(command2);
            	return;
            
            }
            
			
		
			
			//run the commands, count the cliques. If both less than or equal to max clique size allowed, count++ and print the command to a file.
			
			
			DEBUGcount++;
			System.out.println("\n" + (DEBUGcount*100)/totalGraphCount + "%");
			System.out.println("S: " + successfulGraphs);
			
		}
		
	}
	

	   
	

	public BigInteger getNextRelationSet(BigInteger relationSet){
	
		//increment the old integer by one
		relationSet = relationSet.add(BigInteger.ONE);
		
		//find a balanced realtion set.
		while (!relationSetBalanced(relationSet)){
			relationSet = relationSet.add(BigInteger.ONE);
		}
		
		return relationSet;
	}
	
	
	//returns true if each edges in the relation set
	//has the same number of edges in the complement 
	public boolean relationSetBalanced(BigInteger relationSet){
		int zeroSum = 0;
		int oneSum = 0;
		// because we only care about the lower half of the relationSet due to symmetry
		// only search n-1 / 2 nodes
		for(int i = 0; i != (graphSize-1)/2; i++){
			
			if(relationSet.testBit(i))
				oneSum++;
			else
				zeroSum++;
		}
		
		//if(oneSum != zeroSum)
		if(Math.abs(oneSum - zeroSum) > 1)
			return false;
		else
			return true;
		
	}
	
	//takes a set and expands it to all nodes
	public BigInteger symmetrizeSet(BigInteger relationSet){
	
		for(int i = 0; i !=(graphSize-1)/2; i++){
			
			if(relationSet.testBit(i))
				relationSet = relationSet.setBit((graphSize-1)-1-i);		
		}
	
		return relationSet;
	}	
	
	
	public String getMathematicaCliqueCommand(BigInteger relationSet,boolean complement){
		StringBuilder writer = new StringBuilder();
		writer.append("FindClique[");
		
		if(complement)
			writer.append("GraphComplement[");
		
		writer.append("AdjacencyGraph[{");
		
		for(int i = 0; i != graphSize; i++){
			writer.append("{");

				int j = 0;
				while (j != graphSize){
					
					
					if(j < i){
						writer.append(relationSet.testBit((graphSize-1)-(i-j))? "1" : "0");
					}
					if(i == j){
						writer.append("0");
					}
					if(j > i){
						writer.append(relationSet.testBit(j-(i+1))? "1" : "0");
					}
					
					j++;
					
					if(j != graphSize)
						writer.append(",");
				}
				
				writer.append("}");
				
				if(i != graphSize-1)
					writer.append(",");
			}
		

			writer.append("}]]");
			
			if(complement)
				writer.append("]");
				
		return writer.toString();
	}
}
