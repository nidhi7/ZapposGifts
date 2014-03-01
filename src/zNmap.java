import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

//import javax.swing.tree.TreeNode;






import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class zNmap {
	
	// static declarations for the api base calls
	static final String ZAPPOS_API_KEY = "52ddafbe3ee659bad97fcce7c53592916a6bfd73";
	static final String ZAPPOS_API_SEARCH = "http://api.zappos.com/Search/";
	static final String ZAPPOS_API_KEY_ID = "?key=";
	static final String ZAPPOS_API_SEPARATOR = "&&";
	
	/**
	 * Command Line Interface program
	 * @param inargs
	 */
	public static void main(String[] inargs) {
		//variables to store number of products and dollars
			int n=0;
			double money=0;
		// check if any arguments are given
		if (inargs.length !=2) {
			printHelp();
			System.exit(0);
		} else {
			n=Integer.parseInt(inargs[0]);
			money=Double.parseDouble(inargs[1]);
		}
		System.out.println("Retrieving prices");
		String[] p=retrievePrices();
		String[] s=new String[p.length];
		for(int j=0;j<p.length;j++){
			
			s[j]=p[j].substring(1);
		}
		System.out.println("Creating BST");
		binST<Double> m=new binST<Double>();
		for(String i:s){
			if(Double.parseDouble(i)<=money)
			m.insert(Double.parseDouble(i));
		}
		System.out.println("Finding paths");
		findSum(m.root,money);
		
		m.inOrder(m.root);
		/*Map<Double, List<Integer>> tm=new TreeMap<Double, List<Integer>>();
		String[] temp=folders.split(" |,");
		for (int j=1;j<temp.length;j++){

		if (!tm.containsKey(temp[j])){
			tm.put(temp[j], new ArrayList<Integer>());
		}
		tm.get(temp[j]).add(j); // This will change the arraylist in the map.*/
		
	/*	NavigableMap<Double,ArrayList<Integer>> original = new TreeMap<Double,ArrayList<Integer>>();
		ArrayList<Integer> al1=new ArrayList<Integer>();
		al1.addAll(Arrays.asList(1,2,3,4,5));
		original.put(121.0,al1);*/
		
		/*original.put(126.0, 1);
		original.put(22.0, 2);
		original.put(119.0,3);
		original.put(121.0, 4);
		original.put(124.0, 5);*/
		
		//get product price greater than given amount
	/*	Object higherKey = original.higherKey(money);
		System.out.println(original.get(higherKey));
		//get product price lesser than given amount
		Object lowerKey = original.lowerKey(money);
		System.out.println(original.get(lowerKey));
		
		System.out.println();
		for(Double i: original.keySet()){
			System.out.println(original.get(i));
			
		}
		*/
		
	}
	
public static String[] retrievePrices() {
		
		// open the URL connection to the REST api
		try {
			URL url = new URL(
					"http://api.zappos.com/Search?key=52ddafbe3ee659bad97fcce7c53592916a6bfd73");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setDoOutput(true);
			conn.setAllowUserInteraction(false);

			// bad news: the URL is not right. We have to play the guessing game!
			if (conn.getResponseCode() != 200) {
				//return fixSKU(skuNumber);
			}
		
			// buffer the response into a string
			BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = rd.readLine()) != null) {
				sb.append(line);
			}
			rd.close();
			conn.disconnect();
			
			// parse response and return URL
			String JSONstring = sb.toString();
			String[] prices = priceJSONParse(JSONstring);
			return prices;
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * Given a JSON object of the response after searching a product, return a list with the 
	 * default images of the products in the response.
	 * @param ImageJSON
	 * @return
	 */
	public static String[] priceJSONParse(String ImageJSON) {
		JSONObject JSONresponse;
		try {
			JSONresponse = new JSONObject(ImageJSON);
			JSONArray JSONprices = JSONresponse.getJSONArray("results");
			String[] prices = new String[JSONprices.length()];
			for (int i = 0; i < prices.length; i++) {
				prices[i] = ((JSONObject) JSONprices.get(i)).getString("price");
			}
			return prices;
			
		} catch (JSONException e) {
			// this might mean that the API sent us a malformed JSON object and that is bad!
			e.printStackTrace();
		}
		return null;
	}
	
	static void findSum(Node node, Double sum, Double[] path, int level){
		if(node==null){
			return;
		}
	
	//insert current node into path
	path[level]=(Double) node.data;
	
	//look for paths with a sum that ends at this node
	int t=0;
	for(int i=level; i>=0;i--){
		t+=path[i];
		if(t==sum){
			print(path,i,level);
		}
	}
	
	//search nodes beaneath this one
	findSum(node.left,sum,path,level+1);
	findSum(node.right,sum,path,level+1);
	
	//Remove current node from path. Not strictly necessary, since
	//we would ignore this value, but it's good practice
	path[level]=Double.MIN_VALUE;
}
	
	public static void findSum(Node node,Double sum){
		int depth=depth(node);
		Double[] path=new Double[depth];
		findSum(node,sum,path,0);
		
	}
	
	public static void print(Double[] path, int start, int end){
		
		for(int i=start; i<=end;i++){
			System.out.print(path[i]+" ");
		}
		
		System.out.println();
	}

	public static int depth(Node node){
		
		if(node==null){
			return 0;
		}
		else{
			return 1+Math.max(depth(node.left),depth(node.right));
		}
		}

	/**
	 * Print help for the CLI
	 */
	public static void printHelp() {
		System.out.println(" Usage: getpic [-v] [FILE]...");
		System.out.println(" Download default image for product SKU numbers contained in FILE.");
		System.out.println(" There must be one product per line");
		System.out.println();
		System.out.println(" -v, --verbose		verbose output");
	}
}

class binST<T extends Comparable<T>> implements Iterable {

	Node<T> root;
	private Comparator<T> comp;
	

	public void inOrder(Node<T> p)
	{
		{
		if(p.left!=null)inOrder(p.left);
		System.out.print(p.data+" ");
		if(p.right!=null)inOrder(p.right);
		
		}
	}
	
	
	public binST(){
		root=null;
		comp=null;
	}
	
	public binST(Comparator<T> c){
		root=null;
		comp=c;
	}
	
	
	
	//count of nodes in BST
	
	int count(Node<T> p){
		if(p!=null) return count(p.left)+1+count(p.right);
		else return 0;
	}
	
	
	
	private int compare(T a, T b)
	   {
	     	if(comp == null) return a.compareTo(b);
	      else
	      return comp.compare(a,b);
	   }
	
	void insert(T data){
		root=insert(root,data);
	}
	Node<T> insert(Node<T> p,T d){
		if(p==null) return new Node<T>(d);
		else if(compare(p.data,d)==0){return p;}
		else if(compare(p.data,d)<0){p.right=insert(p.right,d);}
		else if(compare(p.data,d)>0){p.left=insert(p.left,d);}
			
			return p;
	}
	
	boolean search(T d){
		return search(root,d);
	}

	private boolean search(Node<T> p, T d) {
		if(p==null){return false;}
		else if(compare(p.data,d)==0) return true;
		else if(compare(p.data,d)>0) search(p.left,d);
		else if(compare(p.data,d)<0) search(p.right,d);
		return false;
	}
	
	private void delete(T d){
		root=delete(root,d);
	}

	private Node<T> delete(Node<T> p, T d) {
		if(p==null){ throw new RuntimeException("cannot delete");}
		else if(compare(p.data,d)>0) delete(p.left,d);
		else if(compare(p.data,d)<0) delete(p.right,d);
		else{
			if(p.left==null) return p.right;
			else if(p.right==null) return p.left;
			else {
				p.data=getMaxFromLeft(p.left);
				delete(p.left,p.data);
			}
			
		}
		return p;
	}
	private T getMaxFromLeft(Node<T> root) {
		if(root==null){return null;}
		while(root.right!=null){
			root=root.right;
		}
		return root.data;
	}

	
	@Override
	public Iterator iterator() {
		// TODO Auto-generated method stub
		return null;
	}

}

class Node<T>{
	T data;
	Node<T> left,right;
	
	Node(T d,Node<T> l,Node<T> r){
		this.data=d;
		left=l;
		right=r;
	}
	
	Node(T d)
	{
		this.data=d;
		left=null; right=null;
	}
	
}

