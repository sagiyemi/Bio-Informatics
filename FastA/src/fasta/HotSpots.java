/**
 * 
 */
package fasta;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import com.eaio.stringsearch.BoyerMooreHorspoolRaita;
import com.eaio.stringsearch.StringSearch;

/**
 * @author Rogo
 *
 */
public class HotSpots {
	
	protected ArrayList<Integer>[] hotSpots;
	protected ArrayList < HashMap< String, ArrayList< Integer > > > HashList;
	protected HashMap<String, ArrayList<Integer>> queryTups;
	protected int lastQuery;

	
	public HotSpots(){
		hotSpots=null;
		HashList =null;
		queryTups = null;
		lastQuery = -1;
	};
	
	
	
	/**
	 * 
	 * @param queryInd - index in static queries list
	 * @param targetInd index in static targetList
	 * @param DbHash - true if you want to use a hashed 
	 * @return ArrayList array that in position x of the array we store diagonal x. The stored value is the query index
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<Integer>[] discover(int queryInd, int targetInd, boolean DBHash){
		
		String query = FastaStarter.queries.get(queryInd);
		String target = FastaStarter.targets.get(targetInd);
		hotSpots = new ArrayList[query.length()+target.length()-1];
		HashMap<String, ArrayList<Integer>> targetTups;
		
		// find all ktups in query string. Need to do only once
		if (queryInd!=lastQuery || queryTups == null){
			queryTups = getkTupples(query);
			lastQuery = queryInd;
		}
		
		if (DBHash){
			if (HashList == null)
				hashDB();
			targetTups = HashList.get(targetInd);
		}
		else
		//look for the ktup queries on target string
			targetTups = findTupsOnTarget(queryTups, target);
		
		for (String tup : queryTups.keySet()) {
			ArrayList<Integer> queryInds = queryTups.get(tup);
			ArrayList<Integer> targetInds = targetTups.get(tup);
			
		  if (queryInds!=null && targetInds!=null)
			for (Integer t : targetInds) {
				for (Integer q : queryInds) {
					/*We have t+q-1 diagonals
					 * the bottom left diagonal is 1
					 * the top right diagonal is t+q-1
					 */
					if (hotSpots[t-q+query.length()-1] == null)
						hotSpots[t-q+query.length()-1] = new ArrayList<>();
					hotSpots[t-q+query.length()-1].add(q);
				}
			}
		}
		    
	        for (int i = 0; i < hotSpots.length; i++) {
				if (hotSpots[i]!=null)
					Collections.sort(hotSpots[i]);
			} 
		return hotSpots;
	}
	
	/**
	 * Hash the database for all ktups.
	 * @param DBFile
	 */
	public void hashDB(){
	 //arraylist ( map (ktup - > index) )
	ArrayList< HashMap< String, ArrayList< Integer > > > dbHash = new ArrayList<>();
	 
	//start Hashing
	for (int i = 0; i < FastaStarter.targets.size(); i++) 
		dbHash.add(getkTupples(FastaStarter.targets.get(i)) );	
		
	 HashList = dbHash;
	}

	
	
	
	private HashMap<String, ArrayList<Integer>> findTupsOnTarget(
			HashMap<String, ArrayList<Integer>> queryTups, String target) {
		
		HashMap<String, ArrayList<Integer>> tups = new HashMap<>();
		
		for  (String tup : queryTups.keySet() ){
			ArrayList<Integer> indices = findAllOccurrences(tup, target);
			tups.put(tup, indices);
		}
		return tups;
	}



	private ArrayList<Integer> findAllOccurrences(String tup, String target) {
		StringSearch BMsearch = new BoyerMooreHorspoolRaita();
		ArrayList<Integer> Indices = new ArrayList<>();
//		String targetArr = target.toCharArray();
		Object proccessed = BMsearch.processString(tup);
		int index = -1;
	      while ( (index = BMsearch.searchString(target, index+1, tup, proccessed) ) >=0 ){
	    	  Indices.add(index);
		}
		return Indices;
	}
	/**
	 * Runs BM on <code> query </code> to search for all kTupples.
	 * @param query
	 * @return map kTupple->indexes on <code> query </code>
	 */
	private HashMap<String, ArrayList<Integer>> getkTupples(String query) {
		StringSearch BMsearcher = new BoyerMooreHorspoolRaita();
		int ind;
		String pattern;
		ArrayList<Integer> indices;
		HashMap<String, ArrayList<Integer> > hashQ = new HashMap<>();
//		String queryArr = query.toCharArray();
		
		for (int i = 0; i < query.length()-FastaStarter.ktup; i++) {
			pattern = query.substring(i, i+FastaStarter.ktup-1);
//			ind = BMsearcher.searchString(query, i+1, pattern);
			if ((indices = hashQ.get(pattern)) == null){
				indices = new ArrayList<Integer>();
				hashQ.put(pattern.toString(), indices);
			}
			indices.add(i);
//			if (ind>=0)
//				indices.add(ind);
		}
		return hashQ;
	}

	
	public ArrayList<Integer>[] getHotSpots() {
		return hotSpots;
	}
}
