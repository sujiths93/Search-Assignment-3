import java.io.File;
import java.io.IOException;
import java.lang.Object;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.nio.file.Path;
import org.apache.commons.collections15.FactoryUtils;
import org.apache.commons.collections15.Transformer;

import edu.uci.ics.jung.algorithms.scoring.PageRank;
import edu.uci.ics.jung.algorithms.scoring.PageRankWithPriors;
import edu.uci.ics.jung.graph.*;
import edu.uci.ics.jung.graph.util.Pair;
import edu.uci.ics.jung.io.PajekNetReader;
class ranks{
	Double d;
	Integer i;
	public ranks(Double d,Integer i){
		this.d=d;
		this.i=i;
	}
}
class CustomComparator implements Comparator<ranks> {
    @Override
    public int compare(ranks a, ranks b) {
        if(a.d<b.d)
        	return 1;
        else
        	if(a.d>b.d)
        		return -1;
        return 0;
    }
}
public class AuthorRank {
	//C:\Users\sujit\Documents\GitHub\Search-Assignment-3\Graphfile
	@SuppressWarnings("unchecked")
	public static void main(String args[]) throws IOException{
		Path currentRelativePath = Paths.get("");
		String s = currentRelativePath.toAbsolutePath().toString();
		String filePath=new File(s+"/Graphfile/author.net").getAbsolutePath();

		PajekNetReader pnr = new PajekNetReader(FactoryUtils.instantiateFactory(Object.class));
        Graph graph=new UndirectedSparseGraph<Integer,Integer>();
        pnr.load(filePath, graph);
        System.out.println(graph.inDegree(1024));
        PageRank<Integer,Integer> rank=new PageRank<Integer,Integer>(graph,0.85);
        rank.setMaxIterations(30);
        rank.evaluate();
        Collection<Integer> c=new ArrayList<Integer>();
        c=graph.getVertices();
        int k=0;
        List<ranks> result=new ArrayList<ranks>();
        for(int i:c){
        	result.add(new ranks(rank.getVertexScore(i),i));
        }
        System.out.println("NODEID"+"		"+"SCORE");
        Collections.sort(result,new CustomComparator());
        for(int i=result.size()-1;i>=result.size()-11;i--)
        	System.out.println(result.get(i).i+"		"+result.get(i).d);
		return;
	}
}
