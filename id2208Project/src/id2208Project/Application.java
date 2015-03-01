package id2208Project;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.ow2.easywsdl.wsdl.WSDLFactory;
import org.ow2.easywsdl.wsdl.api.Binding;
import org.ow2.easywsdl.wsdl.api.BindingOperation;
import org.ow2.easywsdl.wsdl.api.Description;
import org.ow2.easywsdl.wsdl.api.WSDLReader;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
public class Application {

	public final String filePath = 
			"file:/home/stefan/programs/gitLocal/id2208project/id2208Project/resources/WSDLs/ACHWorksAPIProfile.wsdl";
	
	public static void main(String args []) {
		new Application();
	}
	
	Application(){
		

		//SchemaReader r = org.ow2.easywsdl.schema.SchemaFactory.newInstance().newSchemaReader();
		//r.read(new URI("").toURL()).getTypes()
		try{
			WSDLReader reader = WSDLFactory.newInstance().newWSDLReader();
			Description d = reader.read(new URI(filePath).toURL());
			//String str = d.getBindings().get(0).getBindingOperations().get(0).getOperation().getQName().getLocalPart();
			for(Binding b : d.getBindings()){
				for(BindingOperation o : b.getBindingOperations()) {
					String str = o.getQName().getLocalPart();
					System.out.println(str);
				}
			}
//			Types t = d.getTypes();
//			d.getTypes().getOtherElements().get(0).getNodeName();
			
			
			Document doc= DocumentBuilderFactory
					.newInstance().newDocumentBuilder().parse(filePath);
			String query = "/definitions/portType/operation/@name";
			//query = "//operation/@name";
			XPath xPath = XPathFactory.newInstance().newXPath();
			NodeList nodeList = (NodeList) xPath.compile(query).evaluate(doc, XPathConstants.NODESET);
			
			for(int i = 0; i<nodeList.getLength(); i++) {
			
				String name = nodeList.item(i).getNodeValue();
				
			}
		} catch(ParserConfigurationException e) {
			e.printStackTrace();
		} catch(IOException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
