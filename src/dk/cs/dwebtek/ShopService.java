package dk.cs.dwebtek;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.XMLOutputter;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

@Path("shop")
public class ShopService {
    /**
     * Our Servlet session. We will need this for the shopping basket
     */
	private static final String shopKey = "C0CF3F21E1B721284D08A9AE";
	
    HttpSession session;
    
    ArrayList<Item> items;
    
	private static final Namespace ns = Namespace
			.getNamespace("http://www.cs.au.dk/dWebTek/2014");

    public ShopService(@Context HttpServletRequest servletRequest) {
        session = servletRequest.getSession();
    }

    /**
     * Make the price increase per request (for the sake of example)
     */
    private static int priceChange = 0;

    @GET
    @Path("items")
    public String getItems() {
        //You should get the items from the cloud server.
        //In the template we just construct some simple data as an array of objects
        JSONArray array = new JSONArray();
        
        updateList();
        
        for(Item item : items){
            JSONObject jsonObject1 = new JSONObject();
            jsonObject1.put("name", item.getName());
            jsonObject1.put("price", item.getPrice());
            jsonObject1.put("description",item.getItemDescriptionAsHTML());
            jsonObject1.put("stock", item.getStock());
            jsonObject1.put("id", item.getId());

            array.put(jsonObject1);
        }
        
//        JSONObject jsonObject1 = new JSONObject();
//        jsonObject1.put("id", 1);
//        jsonObject1.put("name", "Stetson hat");
//        jsonObject1.put("price", 200 + priceChange);
//        array.put(jsonObject1);
//
//        JSONObject jsonObject2 = new JSONObject();
//        jsonObject2.put("id", 2);
//        jsonObject2.put("name", "Rifle");
//        jsonObject2.put("price", 500 + priceChange);
//        array.put(jsonObject2);
        
        priceChange++;

        //You can create a MessageBodyWriter so you don't have to call toString() every time
        return array.toString();
    }
    
    public void updateList() {
    	items = new ArrayList<Item>();
    	try {
    		XMLOutputter xmlOutPutter = new XMLOutputter();
    		SAXBuilder builder = new SAXBuilder();
    		HttpURLConnection con = (HttpURLConnection) new URL(
    				"http://services.brics.dk/java4/cloud/listItems?shopID=34")
    				.openConnection();
    		InputStream in = con.getInputStream();
    		Document responseDoc = builder.build(in);
    		List<Element> children = responseDoc.getRootElement().getChildren();
    		for (Element e : children) {
    			Item item = new Item();
    			item.setId(Integer.parseInt(e.getChild("itemID",
    					e.getNamespace()).getValue()));
    			item.setName(e.getChild("itemName", e.getNamespace())
    					.getValue());
    			item.setUrl(e.getChild("itemURL", e.getNamespace()).getValue());
    			item.setPrice(Integer.parseInt(e.getChild("itemPrice",
    					e.getNamespace()).getValue()));
    			item.setStock(Integer.parseInt(e.getChild("itemStock",
    					e.getNamespace()).getValue()));
    			item.setDescription(xmlOutPutter.outputString(e.getChild(
    					"itemDescription", e.getNamespace()).getChild(
    					"document", ns)));
    			items.add(item);
    		}
    	} catch (JDOMException e1) {
    		e1.printStackTrace();
    	} catch (MalformedURLException e1) {
    		e1.printStackTrace();
    	} catch (IOException e1) {
    		e1.printStackTrace();
    	}
    }
    
    @POST
    @Path("/login")
    public String login(@FormParam("json") String JSON){
    	System.out.println(JSON);
    	JSONObject customer = new JSONObject(JSON);
    	String username = customer.getString("username");
    	String password = customer.getString("password");
    	Document doc = generateLogin(username, password);
    	
    	HttpURLConnection con;
		try {
			con = (HttpURLConnection) new URL(
					"http://services.brics.dk/java4/cloud/login")
					.openConnection();
		
			con.setDoOutput(true);
			BufferedOutputStream out = new BufferedOutputStream(
				con.getOutputStream());
			PrintStream pout = new PrintStream(out);
			XMLOutputter xmlOutPutter = new XMLOutputter();
			pout.append(xmlOutPutter.outputString(doc));
			pout.flush();    	
			System.out.println(xmlOutPutter.outputString(doc));
			int response = con.getResponseCode();

			if (response != 200) {
				System.out
					.println("Something went wrong while logging in: "
							+ response);
				return "Error";
			} else {
				System.out.println("The customer was succesfully logged in");
				
					
			SAXBuilder builder = new SAXBuilder();
			InputStream in = con.getInputStream();
			//Making XML
			Document responseDoc = builder.build(in);
			String responseDocString = xmlOutPutter.outputString(responseDoc);
			System.out.println(responseDocString);
			return responseDoc.getRootElement().getChild("customerID",ns).getValue();
			}
		}catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JDOMException e) {
			e.printStackTrace();
		}
		
	
//    	/step 0 parse the incoming JSON to get the user name and password
//    	/step 1 use JDOM to create the createCustomer XML and print it to console
//    	/step 2 send the XML to the server and print out the reply
//    	/step 3 parse the reply from the server and translate it into JSON
//    /	step 4 return the JSON
    	return "Error";
    }
    
    @POST
    @Path("/createCustomer")
    public String makeCustomer(@FormParam("json") String JSON){
    	System.out.println(JSON);
    	JSONObject customer = new JSONObject(JSON);
    	String username = customer.getString("username");
    	String password = customer.getString("password");
    	Document doc = generateCreateCustomer(username, password);
    	
    	HttpURLConnection con;
		try {
			con = (HttpURLConnection) new URL(
					"http://services.brics.dk/java4/cloud/createCustomer")
					.openConnection();
		
			con.setDoOutput(true);
			BufferedOutputStream out = new BufferedOutputStream(
				con.getOutputStream());
			PrintStream pout = new PrintStream(out);
			XMLOutputter xmlOutPutter = new XMLOutputter();
			pout.append(xmlOutPutter.outputString(doc));
			pout.flush();    	
			System.out.println(xmlOutPutter.outputString(doc));
			int response = con.getResponseCode();

			if (response != 200) {
				System.out
					.println("Something went wrong while creating the customer: "
							+ response);
				return "Something went wrong while creating the customer :(";
			} else {
				System.out.println("The customer was succesfully made");
				return "The customer was succesfully made! :D";
			}
//			SAXBuilder builder = new SAXBuilder();
//			InputStream in = con.getInputStream();
//			//Making XML
//			Document responseDoc = builder.build(in);
//			String responseDocString = xmlOutPutter.outputString(responseDoc);
//			System.out.println(responseDocString);
//			JSONObject JSONResponse = XML.toJSONObject(responseDocString);
//			return JSONResponse.toString();
		
		}catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}// catch (JDOMException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
	
//    	/step 0 parse the incoming JSON to get the user name and password
//    	/step 1 use JDOM to create the createCustomer XML and print it to console
//    	/step 2 send the XML to the server and print out the reply
//    	/step 3 parse the reply from the server and translate it into JSON
//    /	step 4 return the JSON
    	return "error";
    }
    
    @POST
    @Path("/sellItems")
    public String sell(@FormParam("json") String JSON){
    	System.out.println(JSON);
    	JSONObject JSONOb = new JSONObject(JSON);
    	JSONArray JSONItems = JSONOb.getJSONArray("items");
    	String customerID = JSONOb.getInt("customerID")+"";
    	
    	for(int i = 0; i < JSONItems.length(); i++){

    		int itemID = JSONItems.getInt(i);
    		
    		Document doc = generateSellItem(itemID, customerID);

    		HttpURLConnection con;
    		try {
    			con = (HttpURLConnection) new URL(
    					"http://services.brics.dk/java4/cloud/sellItems")
    			.openConnection();

    			con.setDoOutput(true);
    			BufferedOutputStream out = new BufferedOutputStream(
    					con.getOutputStream());
    			PrintStream pout = new PrintStream(out);
    			XMLOutputter xmlOutPutter = new XMLOutputter();
    			pout.append(xmlOutPutter.outputString(doc));
    			pout.flush();    	
    			System.out.println(xmlOutPutter.outputString(doc));
    			int response = con.getResponseCode();

    			if (response != 200) {
    				System.out
    				.println("Something went wrong while buying the items: "
    						+ response);
    				return "Error";
    			} else {
    				System.out.println("The items were bought");
    				return "Success";

    				
    			}
    		}catch (MalformedURLException e) {
    			e.printStackTrace();
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
    	}
    	return "Error";
    }
    
    public Document generateSellItem(int itemID, String customerID) {
		// Creating a new xml program to hold the createCustomer data
		Document doc = new Document();

		// Adds a new element called sellItem and sets it as RootElement
		Element sellItems = new Element("sellItems", ns);
		doc.setRootElement(sellItems);

		// Copies the ItemIDEl from the source document
		Element itemIDEl = new Element("itemID", ns).setText(itemID+"");
		
		// Copies the customerIDEl from the source document
		Element customerIDEl = new Element("customerID", ns).setText(customerID);
		
		// Copies the saleAmount from the source document
		Element saleAmount = new Element("saleAmount", ns).setText("1");
		
		// Copies the shopkey from the source document
		Element elshopKey = new Element("shopKey", ns).setText(shopKey);
		
		//adds shopKey, customerName and customerPass as children																
		sellItems.addContent(itemIDEl);
		sellItems.addContent(customerIDEl);
		sellItems.addContent(elshopKey);
		sellItems.addContent(saleAmount);	
		return doc;
	}
    
    public Document generateLogin(String username, String password){
    	// Creating a new xml program to hold the createCustomer data
		Document doc = new Document();
		
		// Adds a new element called createCustomer and sets it as RootElement
		Element login = new Element("login", ns);
		doc.setRootElement(login);

		// Copies the customerName from the source document
		Element customerName = new Element("customerName", ns).setText(username);
		
		// Copies the customerPass from the source document
		Element customerPass = new Element("customerPass", ns).setText(password);
			
		//adds shopKey, customerName and customerPass as children																
		login.addContent(customerName);
		login.addContent(customerPass);
		
		return doc;
    }
    
    public Document generateCreateCustomer(String username, String password) {
		// Creating a new xml program to hold the createCustomer data
		Document doc = new Document();

		// Adds a new element called createCustomer and sets it as RootElement
		Element createCustomer = new Element("createCustomer", ns);
		doc.setRootElement(createCustomer);

		// Copies the customerName from the source document
		Element customerName = new Element("customerName", ns).setText(username);
		
		// Copies the customerPass from the source document
		Element customerPass = new Element("customerPass", ns).setText(password);
		
		// Copies the shopkey from the source document
		Element elshopKey = new Element("shopKey", ns).setText(shopKey);
		
		//adds shopKey, customerName and customerPass as children																
		createCustomer.addContent(customerName);
		createCustomer.addContent(customerPass);
		createCustomer.addContent(elshopKey);
		
		return doc;
	}
}


