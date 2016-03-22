package pt.mashashi.javaroles.test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class MavenCleaner {
	public static void main(String[] args) throws FileNotFoundException, IOException, ParserConfigurationException, SAXException {
		
		//mvn exec:java -Dexec.mainClass="pt.mashashi.javaroles.test.MavenCleaner"
		String p = "JavaRoles"+File.separatorChar+"target"+File.separatorChar+"site"+File.separatorChar+"cobertura"+File.separatorChar+"coverage.xml";
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.parse(new File(p));
		NodeList pkgs = document.getElementsByTagName("package");
		end: while(pkgs.getLength()!=0){
			inner:{ 
				for(int i=0;i<pkgs.getLength();i++){
					String pkgName = pkgs.item(i).getAttributes().getNamedItem("name").getNodeValue();
					if(
							pkgName.contains(".test")
					  ){
						System.out.println("Removed pkg:"+pkgName);
						pkgs.item(i).getParentNode().removeChild(pkgs.item(i));
						break inner;
					}
				}
				break end;
			}
			pkgs = document.getElementsByTagName("package");
		}
			
		
		/*
		NodeList elems = document.getElementsByTagName("class");
		for(int i=0;i<elems.getLength();i++){
			String className = elems.item(i).getAttributes().getNamedItem("name").getNodeValue();
			if(className.contains(".test.")){
				System.out.println("Removed class:"+className);
				elems.item(i).getParentNode().removeChild(elems.item(i));
			}
		}
		*/
		
		FileWriter f = new FileWriter(p);
        f.write(getStringFromDoc(document));
        f.close();
	}
	
	public static String getStringFromDoc(org.w3c.dom.Document doc) {
		 try
	        {
	           DOMSource domSource = new DOMSource(doc);
	           StringWriter writer = new StringWriter();
	           StreamResult result = new StreamResult(writer);
	           TransformerFactory tf = TransformerFactory.newInstance();
	           Transformer transformer = tf.newTransformer();
	           transformer.transform(domSource, result);
	           writer.flush();
	           return writer.toString();
	        }
	        catch(TransformerException ex)
	        {
	           ex.printStackTrace();
	           return null;
	        } 
	}
	
}
