package dk.cs.dwebtek;

import java.io.IOException;
import java.io.InputStream;
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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;

@Path("shop")
public class ShopService {
    /**
     * Our Servlet session. We will need this for the shopping basket
     */
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
    
    @GET
    @Path("createCustomer")
    public String makeCustomer(){
//    	step 0 parse the incoming JSON to get the user name and password
//    	step 1 use JDOM to create the createCustomer XML and print it to console
//    	step 2 send the XML to the server and print out the reply
//    	step 3 parse the reply from the server and translate it into JSON
//    	step 4 return the JSON
    	return "ok";
    }
}


