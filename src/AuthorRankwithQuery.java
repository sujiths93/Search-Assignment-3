import java.io.File;
import java.io.IOException;
import java.lang.Object;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.nio.file.Path;
import org.apache.commons.collections15.FactoryUtils;
import org.apache.commons.collections15.Transformer;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;

import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.BM25Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import edu.uci.ics.jung.algorithms.scoring.PageRank;
import edu.uci.ics.jung.algorithms.scoring.PageRankWithPriors;
import edu.uci.ics.jung.algorithms.util.SettableTransformer;
import edu.uci.ics.jung.graph.*;
import edu.uci.ics.jung.io.PajekNetReader;


public class AuthorRankwithQuery {
           @SuppressWarnings("unchecked")
           public static void main(String args[]) throws IOException, ParseException{
                      Path currentRelativePath = Paths.get("");
                      String s = currentRelativePath.toAbsolutePath().toString();
                      String filePath=new File(s+"/Graphfile/author.net").getAbsolutePath();
                      PajekNetReader pnr = new PajekNetReader(FactoryUtils.instantiateFactory(Object.class));
                      Graph graph=new UndirectedSparseGraph<Integer,Integer>();
                      pnr.load(filePath, graph);
                      SettableTransformer<Integer, String> vertex_label= pnr.getVertexLabeller();
                      HashMap<String, Integer> hh = new HashMap<String, Integer>();
                      for(int i=0;i<2000;i++){
                      	hh.put(vertex_label.transform(i), i);
                      }
                    
                    double[] prior = new double[2000];
                    int mm=0,ii=0;
                    HashMap<Integer, Double> h = new HashMap<Integer, Double>();  
                    String queryString = "data mining";  
          			String index = s+"/Graphfile/author_index";
        	       IndexReader indexReader = DirectoryReader.open(FSDirectory.open(new File(index)));
        	       IndexSearcher indexSearcher = new IndexSearcher(indexReader);
        	       Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_40);
        	       indexSearcher.setSimilarity(new BM25Similarity());
        	       QueryParser queryParser = new QueryParser("content", analyzer);
					Query query = queryParser.parse(QueryParser.escape(queryString));
					TopDocs topDocs = indexSearcher.search(query, 300);
        	       
        	       ScoreDoc[] hits = topDocs.scoreDocs;
        	       
        	       for(int i=0;i<hits.length;i++){
        	       	Document doc = indexSearcher.doc(hits[i].doc);
        	       	String at = doc.get("authorid");
        	       	double d = 0;
        	       	if(h.containsKey(at)){
        	       		d = h.get(hh.get(at));
        	       		h.put(hh.get(at), new Double(d+hits[i].score));
        	       	}
        	       	else{
        	       		h.put(hh.get(at), new Double(hits[i].score));
        	       	}
        	       }
        	      
        System.out.println("  ");
        
        double total=0.0;
        for (Map.Entry<Integer, Double> entry : h.entrySet()) {
            total+=h.get(entry.getKey());
        }

        for (Map.Entry<Integer, Double> entry1 : h.entrySet()) {
            prior[entry1.getKey()] = entry1.getValue()/total;
        }
        
        Transformer<Integer, Double> vertex_prior = 
                new Transformer<Integer, Double>()
                {            
             @Override
                     public Double transform(Integer v) 
                     {                        
                         return (double) prior[v];            
                     }           
                };
        
        PageRankWithPriors<Integer, String> prp = new PageRankWithPriors<Integer, String>(graph, vertex_prior, 0.85);
        prp.setMaxIterations(30);
        prp.evaluate();
        
        Collection<Integer> c=new ArrayList<Integer>();
        c=graph.getVertices();
        
        HashMap<Integer, Double> h1 = new HashMap<Integer, Double>();
        
        for(Integer i:c){
        	if(!h1.containsKey(i)){
        		h1.put(i, prp.getVertexScore(i));
        	}
         }
        
        
        HashMap m = sortByValues(h1);
		Set set2 = m.entrySet();
	     Iterator iterator2 = set2.iterator();
	     while(iterator2.hasNext() && ii<10) {
	          Map.Entry me2 = (Map.Entry)iterator2.next();
	          mm = (int) me2.getKey();
	          System.out.println((mm+1) + " -- " + "\"" + vertex_label.transform(mm) + "\"" + " -- " + me2.getValue());
	          ii++;
	     }
    }
       	private static HashMap sortByValues(HashMap map) { 
  	      List list = new LinkedList(map.entrySet());
  	      Collections.sort(list, new Comparator() {
  	    	  public int compare(Object o1, Object o2) {
  	        	   return ((Comparable) ((Map.Entry) (o2)).getValue()).compareTo(((Map.Entry) (o1)).getValue());
  	              }
  	           });
  	      HashMap sortedHashMap = new LinkedHashMap();
  	      for (Iterator it = list.iterator(); it.hasNext();) {
  	             Map.Entry entry = (Map.Entry) it.next();
  	             sortedHashMap.put(entry.getKey(), entry.getValue());
  	      } 
  	      return sortedHashMap;
       	}
}

