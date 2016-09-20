import java.util.*;
import java.io.*;

// This class implements a google-like search engine
public class searchEngine {

    public HashMap<String, LinkedList<String> > wordIndex;                  // this will contain a set of pairs (String, LinkedList of Strings)	
    public directedGraph internet;             // this is our internet graph
    
    
    
    // Constructor initializes everything to empty data structures
    // It also sets the location of the internet files
    searchEngine() {
	// Below is the directory that contains all the internet files
	htmlParsing.internetFilesLocation = "internetFiles";
	wordIndex = new HashMap<String, LinkedList<String> > ();		
	internet = new directedGraph();				
    } // end of constructor2014
    
    
    // Returns a String description of a searchEngine
    public String toString () {
	return "wordIndex:\n" + wordIndex + "\ninternet:\n" + internet;
    }
    
    
    // This does a graph traversal of the internet, starting at the given url.
    // For each new vertex seen, it updates the wordIndex, the internet graph,
    // and the set of visited vertices.
    
    void traverseInternet(String url) throws Exception {
    	//implementing a depth first traversal recursively
    	if (url==null||internet.getVisited(url)) return; //base case trying to avoid infinite loop and visiting the same node twice
    	internet.addVertex(url);
    	//setting up a while loop that adds all the links to the current url to our directed graph.
    	LinkedList<String>linksToWebpage=htmlParsing.getLinks(url);
    	Iterator<String> itr=linksToWebpage.iterator();
    	while(itr.hasNext()){
    		internet.addEdge(url,itr.next());
    	}
    	//creating the dictionary of words.
    	//gets a linked list of strings from the webpage and iterates through each string.
    	itr=htmlParsing.getContent(url).iterator();
    	while(itr.hasNext()){
    		String tempString=(String)itr.next();
    		//checks to see if the word is already contained. If not we create a new linked list to store the list of urls associated with that word.
    		if(!wordIndex.containsKey(tempString)){
    			LinkedList<String> freshWordList=new LinkedList<String>();
    			freshWordList.add(url);
    			wordIndex.put(tempString,freshWordList);
    		}
    		//if the old list has already been created, we just simply copy the old list and add the new url to that list, putting the value back into the hashmap.
    		else{
    			LinkedList<String> oldWordList=wordIndex.get(tempString);
    			oldWordList.add(url);
    			wordIndex.put(tempString, oldWordList);
    		}
    	}
    	//setting the value as visited.
    	internet.setVisited(url, true);
    	//gets all of the links to the current value.
    	itr=linksToWebpage.iterator();
    	//goes through all the links and if they have not already been visited,recursively calls the method so that those urls are visited.
    	while(itr.hasNext()){
    		String tempVar=(String)itr.next();
    		if(!internet.getVisited(tempVar)){
    			traverseInternet(tempVar);
    		}
    	}
    } // end of traverseInternet
    void computePageRanks() {
    	//getting the list of vertices and setting my intitial constant value for page ranks equal to one.
    	LinkedList<String> vertices=internet.getVertices();
    	Iterator<String> itrV=vertices.iterator();
    	double constantPageRank=1.0;
    	//setting all the intial page ranks to one.
    	while(itrV.hasNext()){
    		internet.setPageRank(itrV.next(),constantPageRank);
    	}
    	//number of times page rank is run.
    	int numberOfRepetitions=100;
    	for(int i=0;i<numberOfRepetitions;i++){
    		//reset the iterator
    		itrV=vertices.iterator();
    		while(itrV.hasNext()){
    			String tempUrl=itrV.next();
    			LinkedList<String> edgesInto=internet.getEdgesInto(tempUrl);
    			double sumOfPageRank=0;
    			Iterator<String>edgesIntoItr=edgesInto.iterator();
    			while(edgesIntoItr.hasNext()){
    			String tempEdge=edgesIntoItr.next();
    			sumOfPageRank+=(internet.getPageRank(tempEdge)/(double)internet.getOutDegree(tempEdge));
    			}
    			internet.setPageRank(tempUrl, 0.5+0.5*sumOfPageRank);
    			}
    		}
    	}// end of computePageRanks
    
	
    /* Returns the URL of the page with the high page-rank containing the query word
       Returns the String "" if no web site contains the query.
       This method can only be called after the computePageRanks method has been executed.
       Start by obtaining the list of URLs containing the query word. Then return the URL 
       with the highest pageRank.
       This method should take about 25 lines of code.
    */
    String getBestURL(String query) {
	/* WRITE YOUR CODE HERE */
    	LinkedList<String> theUrls =wordIndex.get(query);
    	if(theUrls==null){
    		return "We are sorry but no website matched your query";
    	}
    	String bestLink=null;
    	double highestScore=0;
    	Iterator<String>parseUrls=theUrls.iterator();
    	while(parseUrls.hasNext()){
    		String currentUrlVisiting=parseUrls.next();
    		if((internet.getPageRank(currentUrlVisiting)>highestScore)){
    			highestScore=internet.getPageRank(currentUrlVisiting);
    			bestLink=currentUrlVisiting;
    		}
    	}
    	return bestLink; // remove this
    } // end of getBestURL
    
    
	
    public static void main(String args[]) throws Exception{		
	searchEngine mySearchEngine = new searchEngine();
	// to debug your program, start with.
	mySearchEngine.traverseInternet("http://www.cs.mcgill.ca/~blanchem/250/a.html");
    
	//mySearchEngine.traverseInternet("http://www.cs.mcgill.ca");
	
	// this is just for debugging purposes. REMOVE THIS BEFORE SUBMITTING
	mySearchEngine.computePageRanks();
	BufferedReader stndin = new BufferedReader(new InputStreamReader(System.in));
	String query;
	do {
	    System.out.print("Enter query: ");
	    query = stndin.readLine();
	    if ( query != null && query.length() > 0 ) {
		System.out.println("Best site = " + mySearchEngine.getBestURL(query));
	    }
	} while (query!=null && query.length()>0);				
    } // end of main
}