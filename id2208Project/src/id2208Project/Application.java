package id2208Project;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import javax.xml.namespace.QName;
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
import org.ow2.easywsdl.wsdl.org.w3.ns.wsdl.TypesType;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.predic8.schema.ComplexType;
import com.predic8.schema.Element;
import com.predic8.schema.Schema;
import com.predic8.schema.SimpleType;
import com.predic8.wsdl.Definitions;
import com.predic8.wsdl.Operation;
import com.predic8.wsdl.PortType;
import com.predic8.wsdl.WSDLParser;
public class Application {

	public final String filePath = 
			"file:/home/stefan/programs/gitLocal/id2208project/id2208Project/resources/WSDLs/ACHWorksAPIProfile.wsdl";

	public static void main(String args []) {
		new Application();

	}

	Application(){
		second();

	}

	private void second(){
		WSDLParser parser = new WSDLParser();

		Definitions defs = parser
				.parse(filePath);

		for (PortType pt : defs.getPortTypes()) {
			//System.out.println(pt.getName());
			for (Operation op : pt.getOperations()) {
				System.out.println("operation: " + op.getName());
				listParams(defs.getElement(op.getInput().
						getMessage().getParts().get(0).getElement().getQname()));
				System.out.println();
				//System.out.println("Next");
				System.out.println();
			}
		}
	}

	private void listParams(Element element){
		ComplexType ct = (ComplexType) element.getEmbeddedType();

		Schema schema = element.getSchema();


		for(Element ee : ct.getSequence().getElements()){
			String type = ee.getType().getLocalPart();	

			if(schema.getType(type) instanceof ComplexType){
				for(Element e : ee.getSchema().getComplexType(type).getSequence().getElements()) {
					System.out.println(e.getName());
				}
			}
			if(schema.getType(type) instanceof SimpleType){
				System.out.println(schema.getType(type).getName());
			}



		}




	}









	//	private void first(){
	//
	//		
	//
	//		//SchemaReader r = org.ow2.easywsdl.schema.SchemaFactory.newInstance().newSchemaReader();
	//		//r.read(new URI("").toURL()).getTypes()
	//		
	//		try{
	//			WSDLReader reader = WSDLFactory.newInstance().newWSDLReader();
	//			Description d = reader.read(new URI(filePath).toURL());
	//			//String str = d.getBindings().get(0).getBindingOperations().get(0).getOperation().getQName().getLocalPart();
	//			for(Binding b : d.getBindings()){
	//				for(BindingOperation o : b.getBindingOperations()) {
	//					String str = o.getQName().getLocalPart();
	//					str = o.getInput().getName();
	//					str = o.getHttpInputSerialization();
	//					
	//					System.out.println(str);
	//				}
	//			}
	////			String str = d.getTypes().getSchemas().get(0).get
	//			
	//			String str = d.getServices().get(0).getInterface().getOperations().get(0).
	//					toString();
	//			str = d.getTypes().getSchemas().get(0).getTypes().get(0).getQName().getLocalPart();
	//					
	//			System.out.println(str);
	////			Types t = d.getTypes();
	////			d.getTypes().getOtherElements().get(0).getNodeName();
	//			
	//			
	//			Document doc= DocumentBuilderFactory
	//					.newInstance().newDocumentBuilder().parse(filePath);
	//			String query = "/definitions/portType/operation/@name";
	//			//query = "//operation/@name";
	//			XPath xPath = XPathFactory.newInstance().newXPath();
	//			NodeList nodeList = (NodeList) xPath.compile(query).evaluate(doc, XPathConstants.NODESET);
	//			
	//			for(int i = 0; i<nodeList.getLength(); i++) {
	//			
	//				String name = nodeList.item(i).getNodeValue();
	//				
	//			}
	//		} catch(ParserConfigurationException e) {
	//			e.printStackTrace();
	//		} catch(IOException e) {
	//			e.printStackTrace();
	//		} catch (SAXException e) {
	//			e.printStackTrace();
	//		} catch (XPathExpressionException e) {
	//			// TODO Auto-generated catch block
	//			e.printStackTrace();
	//		} catch (Exception e) {
	//			e.printStackTrace();
	//		}
	//		
	//	
	//	}
}
