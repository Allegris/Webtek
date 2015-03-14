package dk.cs.dwebtek;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringReader;
import java.util.List;

import org.jdom2.Content;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

public class Item implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private int id;

	private String name;

	private int price;

	private String url;

	private String description;

	private int stock;

	public String xmlToHTML(Element e) {
		String valueToReturn = "";
		List<Content> docContent = e.getContent();
		for (Content c : docContent) {
			switch (c.getCType()) {
			case Text:
				valueToReturn += c.getValue();
				break;
			case Element:
				Element element = (Element) c;
				switch (element.getName()) {
				case "bold":
					valueToReturn += "<b>" + xmlToHTML(element) + "</b>";
					break;
				case "italics":
					valueToReturn += "<i>" + xmlToHTML(element) + "</i>";
					break;
				case "list":
					valueToReturn += "<ul>" + xmlToHTML(element) + "</ul>";
					break;
				case "item":
					valueToReturn += "<li>" + xmlToHTML(element) + "</li>";
					break;
				default:
					valueToReturn += "element";
					break;
				}
				break;
			default:
				valueToReturn += "";
				break;
			}
		}
		return valueToReturn;
	}

	public String getItemDescriptionAsHTML() {
		// do something to get the parameter we sent in below
		final StringReader reader = new StringReader(description);
		final SAXBuilder builder = new SAXBuilder();
		String descriptionAsHTML = "Error parsing XML";
		try {
			Document d = builder.build(reader);
			final Element documentElement = d.getRootElement();
			descriptionAsHTML = xmlToHTML(documentElement);
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return descriptionAsHTML;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getPrice() {
		return price;
	}

	public void setPrice(int price) {
		this.price = price;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getStock() {
		return stock;
	}

	public void setStock(int stock) {
		this.stock = stock;
	}
}
