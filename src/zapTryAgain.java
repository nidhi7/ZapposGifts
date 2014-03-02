import java.io.BufferedReader ;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.NavigableMap;
import java.util.PriorityQueue;
import java.util.Properties;
import java.util.TreeMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class zapTryAgain {
	public static NavigableMap<Double,ArrayList<ArrayList<Product>>> m=new TreeMap<Double,ArrayList<ArrayList<Product>>>();
	public static ArrayList<Product> data=new ArrayList<Product>();
	static int n=0;
	static double money=0;
	static int pageCount=1; 
	static ArrayList<Double> prices=new ArrayList<Double>();
	static ArrayList<Integer> pids=new ArrayList<Integer>();
	static ArrayList<Integer> sids=new ArrayList<Integer>();
	static ArrayList<String> urls=new ArrayList<String>();
	static ArrayList<String> pnames=new ArrayList<String>();
	
	static Properties prop = new Properties();
	static InputStream input = null;
	public static void main(String[] inargs) throws IOException {


		
		input = new FileInputStream("config.txt");
		prop.load(input);
	
// check if any arguments are given
if (inargs.length !=2) {
	printHelp();
	System.exit(0);
} else {
	n=Integer.parseInt(inargs[0]);
	money=Double.parseDouble(inargs[1]);
}
//System.out.println("Retrieving prices");

boolean continueRetrieving = retrievePrices();
while(continueRetrieving){
	continueRetrieving=retrievePrices();
}

processAllRecords();

prices.removeAll(prices);
//System.out.println(prices.size());
urls.removeAll(urls);
sids.removeAll(sids);
pnames.removeAll(pnames);
pids.removeAll(pids);
System.gc();
//System.out.println(data.size());
/*for(Product p: data){
	System.out.println(p.styleId+" "+p.ProductId+" "+p.Price+" "+p.prodName+" "+p.purl);
}*/
System.gc();
if(data.size()>=n){
	
	int[] indices;
	CombinationGenerator x = new CombinationGenerator (data.size(), n);
	//StringBuffer combination;  
	//ArrayList<ArrayList<Product>> combinationsAll=new ArrayList<ArrayList<Product>>();
	while (x.hasMore ()) {
    
	ArrayList<Product> combination=new ArrayList<Product>();
	Double sum=0.0; int count=0;
	indices = x.getNext ();
 
	for (int i = 0; i < indices.length; i++) {
	  sum+=data.get(indices[i]).Price;
	 // if(sum<=money){
		  count++;
	  combination.add(data.get(indices[i]));
		  
	//  }    
	}
	if(count==n) {
	  if(m.containsKey(sum))
	  m.get(sum).add(combination);
	  else{
		  ArrayList<ArrayList<Product>> a=new ArrayList<ArrayList<Product>>();
		  a.add(combination);
		  m.put(sum,a);
	  }
	  
  }
    //combinationsAll.add(combination);

}
	
//free memory of data arraylist
	data.removeAll(data);
	System.gc();
if(m.containsKey(money)){
	//while(m.get(money)!=null){
		for(ArrayList<Product> ap: m.get(money)){
			for(Product p:ap){
				System.out.println(p.styleId+" "+p.ProductId+" "+p.Price+"||"+p.prodName+"||"+p.purl);
			}
			System.out.println();
			System.out.println("---------------------------------------------------");
		}
	//}
}
else{
		Double higherKey = m.higherKey(money);
		Double lowerKey = m.lowerKey(money);
		
		if(higherKey!=null&&lowerKey!=null&&higherKey-money==money-lowerKey){
			
				for(ArrayList<Product> ap: m.get(higherKey)){
					for(Product p:ap){
						System.out.println(p.styleId+" "+p.ProductId+" "+p.Price+"||"+p.prodName+"||"+p.purl);
					}
					System.out.println();
					System.out.println("---------------------------------------------------");
			}
			
				for(ArrayList<Product> ap: m.get(lowerKey)){
					for(Product p:ap){
						System.out.println(p.styleId+" "+p.ProductId+" "+p.Price+"||"+p.prodName+"||"+p.purl);
					}
					System.out.println();
					System.out.println("---------------------------------------------------");
				}
			
		}
		else if(higherKey!=null&& lowerKey==null){
			
				for(ArrayList<Product> ap: m.get(higherKey)){
					for(Product p:ap){
						System.out.println(p.styleId+" "+p.ProductId+" "+p.Price+"||"+p.prodName+"||"+p.purl);
					}
					System.out.println();
					System.out.println("---------------------------------------------------");
				}
			
		}
		else if(higherKey!=null&&lowerKey!=null){
			if(higherKey-money<money-lowerKey){
				//while(m.get(higherKey)!=null){
					for(ArrayList<Product> ap: m.get(higherKey)){
						for(Product p:ap){
							System.out.println(p.styleId+" "+p.ProductId+" "+p.Price+"||"+p.prodName+"||"+p.purl);
						}
						System.out.println();
						System.out.println("---------------------------------------------------");
					}
				//}
			}
			else if(higherKey-money>money-lowerKey){
				
					for(ArrayList<Product> ap: m.get(lowerKey)){
						for(Product p:ap){
							System.out.println(p.styleId+" "+p.ProductId+" "+p.Price+"||"+p.prodName+"||"+p.purl);
						}
						System.out.println();
						System.out.println("---------------------------------------------------");
					}
				
			
			}
		}
		else if(lowerKey!=null&&higherKey==null){
			
				for(ArrayList<Product> ap: m.get(lowerKey)){
					for(Product p:ap){
						System.out.println(p.styleId+" "+p.ProductId+" "+p.Price+"||"+p.prodName+"||"+p.purl);
					}
					System.out.println();
					System.out.println("---------------------------------------------------");
				}
			
		}
		
		else{
			System.out.println("No combinations! Sorry :(");
		}
	}
	}
else{
	System.out.println("Sorry, no such combinations found!");
}

	
	}


private static void processAllRecords() {
	for (int i = 0; i < prices.size(); i++){
		data.add(new Product(pids.get(i),prices.get(i),sids.get(i),urls.get(i),pnames.get(i)));
	}
		
	}


public static boolean retrievePrices() {

	
// open the URL connection to the REST api
	
try {
	//System.out.println(pageCount);

	URL url = new URL(
		//	"http://api.zappos.com/Search?key=67d92579a32ecef2694b74abfc00e0f26b10d623");
			"http://api.zappos.com/Search?sort={%22price%22:%22asc%22}&limit=100&page="+pageCount+"&key=67d92579a32ecef2694b74abfc00e0f26b10d623");
	pageCount++;
	HttpURLConnection conn = (HttpURLConnection) url.openConnection();
	conn.setDoOutput(true);
	conn.setAllowUserInteraction(false);

	// the URL is not right
	if (conn.getResponseCode() != 200) {
		System.out.println("Exiting.. No connection");
		System.exit(0);
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
	if(JSONstring.isEmpty()){
		return false;
	}
	
	boolean continueIfPriceOnPageLess=priceJSONParse(JSONstring);
	
	if((!continueIfPriceOnPageLess && "".equals(prop.getProperty("maxNumberOfPages"))) || 
			(!"".equals(prop.getProperty("maxNumberOfPages")) && pageCount>Integer.parseInt(prop.getProperty("maxNumberOfPages")))){
		return false;
		}
	
	return true;
//	return prices;
	
} catch (MalformedURLException e) {
	e.printStackTrace();
	System.out.println("MalformedURLException");
	return false;
} catch (IOException e) {
	e.printStackTrace();
	System.out.println("IO exception");
	return false;
}


//return null;
}

/**
* Given a JSON object of the response after searching a product, return a list with the 
* default images of the products in the response.
* @param ImageJSON
* @return
*/
public static boolean priceJSONParse(String ImageJSON) {
JSONObject JSONresponse;
try {
	JSONresponse = new JSONObject(ImageJSON);
	JSONArray JSONprices = JSONresponse.getJSONArray("results");
	//Double[] prices = new Double[JSONprices.length()];
	//Integer[] pids=new Integer[JSONprices.length()];
//	Integer[] sids=new Integer[JSONprices.length()];
	
	String tPrice;
	Double tempPrice = 0.0;
	Integer tempPId,tempSId;

	
	for (int i = 0; i < JSONprices.length(); i++) {
		//prices[i] = ((JSONObject) JSONprices.get(i)).getString("price");
		tPrice=((JSONObject) JSONprices.get(i)).getString("price");
		prices.add(Double.parseDouble(tPrice.substring(1)));
		pids.add(Integer.parseInt(((JSONObject) JSONprices.get(i)).getString("productId")));
		sids.add(Integer.parseInt(((JSONObject) JSONprices.get(i)).getString("styleId")));
		 urls.add(((JSONObject) JSONprices.get(i)).getString("productUrl"));
		 pnames.add(((JSONObject) JSONprices.get(i)).getString("productName"));
	
	//	data.add(new Product(tempPId,tempPrice,tempSId));
	}
	Double compareLastOnPage = prices.get((JSONprices.length()*(pageCount-1))-1);
	if(compareLastOnPage > money){
		return false;
	}
	
	
	System.out.println();
	
	
} catch (JSONException e) {
	// this might mean that the API sent us a malformed JSON object and that is bad!
	e.printStackTrace();
	return false;
}

return true;
}



/**
 * Print help for the CLI
 */
public static void printHelp() {
	System.out.println(" Usage: zapTryAgain [number of gifts] [approx amount in dollars you want to spend");
	System.out.println(" Note: Please do not append $ with the amount");
	
}

}

class Product{
	int ProductId;
	Double Price;
	int styleId;
	String prodName;
	String purl;
	Product(int pid, Double pr,int sid,String url,String pname){
		this.ProductId=pid;
		this.Price=pr;
		this.styleId=sid;
		this.purl=url;
		this.prodName=pname;
	}
	
}

class CombinationGenerator {

	  private int[] a;
	  private int n;
	  private int r;
	  private BigInteger numLeft;
	  private BigInteger total;

	  //------------
	  // Constructor
	  //------------

	  public CombinationGenerator (int n, int r) {
	    if (r > n) {
	      throw new IllegalArgumentException ();
	    }
	    if (n < 1) {
	      throw new IllegalArgumentException ();
	    }
	    this.n = n;
	    this.r = r;
	    a = new int[r];
	    BigInteger nFact = getFactorial (n);
	    BigInteger rFact = getFactorial (r);
	    BigInteger nminusrFact = getFactorial (n - r);
	    total = nFact.divide (rFact.multiply (nminusrFact));
	    reset ();
	  }

	  //------
	  // Reset
	  //------

	  public void reset () {
	    for (int i = 0; i < a.length; i++) {
	      a[i] = i;
	    }
	    numLeft = new BigInteger (total.toString ());
	  }

	  //------------------------------------------------
	  // Return number of combinations not yet generated
	  //------------------------------------------------

	  public BigInteger getNumLeft () {
	    return numLeft;
	  }

	  //-----------------------------
	  // Are there more combinations?
	  //-----------------------------

	  public boolean hasMore () {
	    return numLeft.compareTo (BigInteger.ZERO) == 1;
	  }

	  //------------------------------------
	  // Return total number of combinations
	  //------------------------------------

	  public BigInteger getTotal () {
	    return total;
	  }

	  //------------------
	  // Compute factorial
	  //------------------

	  private static BigInteger getFactorial (int n) {
	    BigInteger fact = BigInteger.ONE;
	    for (int i = n; i > 1; i--) {
	      fact = fact.multiply (new BigInteger (Integer.toString (i)));
	    }
	    return fact;
	  }

	  //--------------------------------------------------------
	  // Generate next combination (algorithm from Rosen p. 286)
	  //--------------------------------------------------------

	  public int[] getNext () {

	    if (numLeft.equals (total)) {
	      numLeft = numLeft.subtract (BigInteger.ONE);
	      return a;
	    }

	    int i = r - 1;
	    while (a[i] == n - r + i) {
	      i--;
	    }
	    a[i] = a[i] + 1;
	    for (int j = i + 1; j < r; j++) {
	      a[j] = a[i] + j - i;
	    }

	    numLeft = numLeft.subtract (BigInteger.ONE);
	    return a;

	  }
	}
