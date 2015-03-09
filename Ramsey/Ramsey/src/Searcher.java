import java.math.BigInteger;


public class Searcher {

	//node count of the graph
	public int graphSize;
	
	//maximum clique size in a valid graph
	public int maxCliqueSizeAllowed;

	//set of relations of each node on the circulant graph.
	//where bit 0 represents a connection to 1;
	//bit 1 represents a connection to 2;
	//to bit n-1
	
	
	public static void main(String args[]){
		if(args == null){
			Searcher c = new Searcher(17,3);
			c.search();
		}
		
		
		
	}
	
	public Searcher(int graphSize, int maxCliqueSize){
		this.graphSize = graphSize;
		this.maxCliqueSizeAllowed = maxCliqueSize;
	}
	
	public void search(){
		BigInteger relationSet = BigInteger.ZERO;
		int DEBUGcount=0;
		
		//only need to analyze half the graphs, complement will be done in analysis.
		//accidentally generates one extra one after it finds the last one
		//no worries though
		while(relationSet.longValue() < Math.pow(2, ((graphSize-1)-1)/2)){
			relationSet = getNextRelationSet(relationSet);
			//go ahead and get a symmetrized version
			BigInteger symSet = symmetrizeSet(relationSet);
			analyzeRelationSet(symSet);
		//	System.out.println("Analyzed for R " + maxCliqueSizeAllowed + " " + relationSet.toString());
			DEBUGcount++;
			System.out.println(DEBUGcount);
			
		}
		
	}
	
	private void analyzeRelationSet(BigInteger relationSet) {
		//smoke weed every day
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
		
		if(oneSum != zeroSum)
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
	
}
