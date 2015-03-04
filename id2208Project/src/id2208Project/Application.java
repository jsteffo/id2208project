package id2208Project;


import groovy.xml.QName;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.ow2.easywsdl.extensions.sawsdl.SAWSDLFactory;
import org.ow2.easywsdl.extensions.sawsdl.api.Description;
import org.ow2.easywsdl.extensions.sawsdl.api.SAWSDLException;
import org.ow2.easywsdl.extensions.sawsdl.api.SAWSDLReader;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import autoGen.MatchedElementType;
import autoGen.MatchedOperationType;
import autoGen.MatchedWebServiceType;
import autoGen.WSMatchingType;

import com.predic8.policy.All;
import com.predic8.schema.ComplexContent;
import com.predic8.schema.ComplexType;
import com.predic8.schema.Derivation;
import com.predic8.schema.Element;
import com.predic8.schema.ModelGroup;
import com.predic8.schema.Schema;
import com.predic8.schema.SchemaComponent;
import com.predic8.schema.SimpleType;
import com.predic8.schema.TypeDefinition;
import com.predic8.wsdl.Definitions;
import com.predic8.wsdl.Operation;
import com.predic8.wsdl.Part;
import com.predic8.wsdl.PortType;
import com.predic8.wsdl.WSDLParser;
import com.predic8.xml.util.PrefixedName;
public class Application {
	private UniversalNamespaceResolver namespaceResolver;
	private String schemaNamespace = "http://www.w3.org/2001/XMLSchema";
	private final String outputPath = "src/out/out.xml";
	private static final String baseURI = "resources/WSDLs/";
	private final String sawsdlDir = "/home/stefan/programs/gitLocal/id2208project/id2208Project/resources/SAWSDL";
	private Document doc;
	//private static String filePath1;
	//private static String filePath2;
	//"resources/WSDLs/ACHWorksAPIProfile.wsdl";
	//"resources/WSDLs/BangoSubscriptionBillingAPIProfile.wsdl";
	private WSMatchingType ws = new WSMatchingType();
	public static void main(String args []) {
//		if(args.length != 2){
//			System.out.println("Submit: ws1Name ws2Name");
//			System.exit(0);
//		}
//		new Application(args[0], args[1]);
		new Application("w","w");		
	}

	Application(String path1, String path2){
		List<Path> pathList = new ArrayList<>();
		try {
			Files.walk(Paths.get(sawsdlDir)).forEach(filePath -> {
			    if (Files.isRegularFile(filePath)) {
			        pathList.add(filePath);
			    }
			});
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(Path p1 : pathList){
			for(Path p2 : pathList) {
				System.out.println(p1.toString());
				List <id2208Project.Operationa> oplist1 = domParseWSDL(p1.toString());
				System.out.println(p2.toString());
				List <id2208Project.Operationa> oplist2 = domParseWSDL(p2.toString());
				
				
				//printOperationList(oplist1);
				compareSemanticsWs(oplist1, oplist2, p1.toString(), p2.toString());

			}
		}
		String filePath1 = baseURI + path1;
		String filePath2 = baseURI + path2;
		
		filePath1 = "/home/stefan/programs/gitLocal/id2208project/id2208Project/resources/SAWSDL/_comedyfilmactionfilm_service.wsdl";
		filePath2 ="/home/stefan/programs/gitLocal/id2208project/id2208Project/resources/SAWSDL/_comedyfilmfantacyfilm_service.wsdl";
		//List <id2208Project.Operation> opList1 = parseWsdl(filePath1);
		//List <id2208Project.Operation> opList2 = parseWsdl(filePath2);
		
		//testDom(filePath1);

		//printOperationList2(oplist1);
		
		//compareWs(opList1, opList2, false);
		
		writeFile();
		//testSa(filePath1);
		//System.out.println(ontology.App.getMatching("DepositAccount", "CheckingAccount"));
		//System.out.println(ontology.App.isSubclass("DepositAccount", "CheckingAccount"));
	}
	
	private void compareSemanticsWs(List<id2208Project.Operationa> opInputList,
			List<id2208Project.Operationa> opOutputList, String ws1, String ws2) {
		MatchedWebServiceType m = new MatchedWebServiceType();
		m.setInputServiceName(ws1);
		m.setOutputServiceName(ws2);
		

		double serviceScore = 0;
		int nbrOperationMatch = 0;
		
		for(id2208Project.Operationa op1 : opInputList) {
			for(id2208Project.Operationa op2 : opOutputList) {
				nbrOperationMatch++;
				String op1Name= op1.getName();
				String op2Name = op2.getName();

				MatchedOperationType mOp = new MatchedOperationType();
				mOp.setInputOperationName(op1Name);
				mOp.setOutputOperationName(op2Name);
				m.getMacthedOperation().add(mOp);
				List<Input> argumentInput = op1.getInputList();
				List<Input> argumentOutput = op2.getOutputList();
				int min = Math.min(op1.getInputList().size(), op2.getOutputList().size());
				int numberOfTimes = min;
				double opSum = 0;
				for(int i = 0; i < min; i++) {
					String input = argumentInput.get(i).getName();
					String output = argumentOutput.get(i).getName();

					MatchedElementType mElem = new MatchedElementType();
					mElem.setInputElement(input);

					mElem.setOutputElement(output);
					//mElem.setScore(EditDistance.getSimilarity(input, output));
					double score = ontology.App.getMatching(argumentInput.get(i).getAnnotation(),
							argumentOutput.get(i).getAnnotation());
					mElem.setScore(score);
					mOp.getMacthedElement().add(mElem);
					opSum += score;
				}
				double avg;
				if(numberOfTimes==0) {
					avg = 0;
				} else {
					avg = opSum / numberOfTimes;	
				}

				serviceScore += avg;
				mOp.setOpScore(avg);

			}
		}
		double score = serviceScore/nbrOperationMatch;
		m.setWsScore(serviceScore/nbrOperationMatch);
		if(score > 0.1){
			ws.getMacthing().add(m);
		}
	}
	
	private List<id2208Project.Operationa> domParseWSDL(String filePath1){
		List<id2208Project.Operationa> opList = new ArrayList<>();
		try {
			
			//Initiate important stuff...
			DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
			f.setNamespaceAware(true);
			
			doc = f.newDocumentBuilder().parse(new File(filePath1));
			//System.out.println(doc.getDocumentElement().getNamespaceURI());
			//System.out.println(doc.getDocumentElement().);
			XPath xpath = XPathFactory.newInstance().newXPath();
			String expr;
			HashMap<String, String> prefMap = new HashMap<String, String>();
			prefMap.put("xsd", schemaNamespace);
			prefMap.put("wsdl", "http://schemas.xmlsoap.org/wsdl/");
			prefMap.put("sawsdl", "http://www.w3.org/ns/sawsdl");
			SimpleNamespaceContext namespaces = new SimpleNamespaceContext(prefMap);
			xpath.setNamespaceContext(namespaces);
			namespaceResolver = new UniversalNamespaceResolver(doc);
			
			//First off we need to query to get the operation names...
			expr = "/wsdl:definitions/wsdl:portType/wsdl:operation";
			NodeList operationList = (NodeList) xpath.compile(expr).evaluate(doc, XPathConstants.NODESET);
			//For each operation in list...
			for(int i = 0; i<operationList.getLength(); i++) {
				Node operationNode= operationList.item(i);
				String operationName = operationNode.getAttributes().getNamedItem("name").getNodeValue();
				
				//input
				expr = "wsdl:input";
				Node inputNode = (Node) xpath.compile(expr).evaluate(operationNode, XPathConstants.NODE);
				String inputMessage = inputNode.getAttributes().getNamedItem("message").getNodeValue();
				
				expr ="/wsdl:definitions/wsdl:message[@name='" + getLocalName(inputMessage) +"']";
				Node messageNode = (Node) xpath.compile(expr).evaluate(doc, XPathConstants.NODE);
				expr="wsdl:part";
				NodeList partNodeList = (NodeList) xpath.compile(expr).evaluate(messageNode, XPathConstants.NODESET);
				List<Input> inputList = new ArrayList<Input>();
				
				for(int j = 0; j<partNodeList.getLength(); j++) {
					Node partNode = partNodeList.item(j);
					
					//System.out.println(partNode.getAttributes().getNamedItem("name").getNodeValue());
					partNode.getAttributes().getNamedItem("type").getNodeName();
					String typeName = getLocalName(partNode.getAttributes().getNamedItem("type").getNodeValue());
					//Nu är det dags att röra sig mot schemas...
					expr="//*[@name='" + typeName + "']";
					Node root= (Node) xpath.compile(expr).evaluate(doc, XPathConstants.NODE);
//					Node root = rootList.item(0);
//					for(int k = 0; k<rootList.getLength(); k++) {
//						Node nn = rootList.item(k);
//						if(nn.getAttributes().getNamedItem("sawsdl:modelReference") != null){
//							root = nn;
//						}
//					}
					//System.out.println("input");
					String argumentName = root.getAttributes().getNamedItem("name").getNodeValue();
					String modelRef= root.getAttributes().getNamedItem("sawsdl:modelReference").getNodeValue();
				
					String ref = modelRef.split("#")[1];
					inputList.add(new Input(argumentName, ref));
//					Node root = (Node) xpath.compile(expr).evaluate(doc, XPathConstants.NODE);
//					//System.out.println(root.getAttributes().getNamedItem("name").getNodeValue());
//					List<Input> inputListFragment = new ArrayList<Input>();
//					recursiveTraverse(root, xpath, inputListFragment,"");
//					inputList.addAll(inputListFragment);
				}
				
				expr = "wsdl:output";
				Node outputNode = (Node) xpath.compile(expr).evaluate(operationNode, XPathConstants.NODE);

				String outputMessage = outputNode.getAttributes().getNamedItem("message").getNodeValue();
				
				expr ="/wsdl:definitions/wsdl:message[@name='" + getLocalName(outputMessage) +"']";
				Node messageOutNode = (Node) xpath.compile(expr).evaluate(doc, XPathConstants.NODE);
				expr="wsdl:part";
				partNodeList = (NodeList) xpath.compile(expr).evaluate(messageOutNode, XPathConstants.NODESET);
				List<Input> outputList = new ArrayList<Input>();
				
				for(int j = 0; j<partNodeList.getLength(); j++) {
					
					Node partNode = partNodeList.item(j);
					partNode.getAttributes().getNamedItem("type").getNodeName();
					String typeName = getLocalName(partNode.getAttributes().getNamedItem("type").getNodeValue());
					//Nu är det dags att röra sig mot schemas...
					expr="//*[@name='" + typeName + "']";
					
					Node root= (Node) xpath.compile(expr).evaluate(doc, XPathConstants.NODE);
//					
//					for(int k = 0; k<rootList.getLength(); k++) {
//						Node nn = rootList.item(k);
//						if(nn.getAttributes().getNamedItem("sawsdl:modelReference") != null){
//							root = nn;
//						}
//					}
					String argumentName = root.getAttributes().getNamedItem("name").getNodeValue();
					String modelRef= root.getAttributes().getNamedItem("sawsdl:modelReference").getNodeValue();
					String ref = modelRef.split("#")[1];
					
					outputList.add(new Input(argumentName, ref));
					//System.out.println(root.getAttributes().getNamedItem("name").getNodeValue());
//					List<Input> outputListFragment = new ArrayList<Input>();
//					recursiveTraverse(root, xpath, outputListFragment,"");
//					outputList.addAll(outputListFragment);
					
				}
				
				
				
				
				opList.add(new id2208Project.Operationa(operationName, inputList, outputList));
			}
			
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return opList;
	}
	
	
	private void testDom(String filePath1){
		try {
			
			DocumentBuilderFactory f = DocumentBuilderFactory.newInstance();
			f.setNamespaceAware(true);
			Document doc = f.newDocumentBuilder().parse(new File(filePath1));
			System.out.println(doc.getDocumentElement().getNamespaceURI());
			//System.out.println(doc.getDocumentElement().);
			XPath xpath = XPathFactory.newInstance().newXPath();
			String expr = "//xsd:element";
			HashMap<String, String> prefMap = new HashMap<String, String>();
			prefMap.put("xsd", schemaNamespace);
			SimpleNamespaceContext namespaces = new SimpleNamespaceContext(prefMap);
			xpath.setNamespaceContext(namespaces);
			namespaceResolver = new UniversalNamespaceResolver(doc);
			//Kan använda en egen... Är sannolikt mkt bra att göra.
			
			try {
				NodeList n = (NodeList) xpath.compile(expr).evaluate(doc, XPathConstants.NODESET);
				
				for(int i = 0; i<n.getLength(); i++) {
					Node nn = n.item(i);
					Node typeAttribute = nn.getAttributes().getNamedItem("type");
					//System.out.println();
					if(typeAttribute != null){
						if(isSchemaNamespace(typeAttribute.getNodeValue())){
							System.out.println(typeAttribute.getNodeValue() + "schema namespace...done");	
						}
						else{
							System.out.println(typeAttribute.getNodeValue() + "annat... ejdone");
							String localName = getLocalName(typeAttribute.getNodeValue());
							expr = "//*[@name='" + localName + "']";
							Node nnn = (Node) xpath.compile(expr).evaluate(doc, XPathConstants.NODE);
							System.out.println(nnn.getAttributes().getNamedItem("name").getNodeValue());
						}

						
					}
				}
				
			} catch (XPathExpressionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			
		} catch (SAXException | IOException | ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private String resolveNamespace(String str){
		String prefix = str.split(":")[0];
		//String localName = str.split(":")[1];
		return namespaceResolver.getNamespaceURI(prefix);
	}
	
	private boolean isSchemaNamespace(String str){
		String prefix = str.split(":")[0];
		if(str.split(":").length == 1){
			return false;
		}
		return schemaNamespace.equals(resolveNamespace(str));
	}
	
	private String getLocalName(String str) {
		if(str.split(":").length==1){
			return str;
		}
		String prefix = str.split(":")[0];
		
		String localName = str.split(":")[1];
		return localName;
	}
	
	private void recursiveTraverse(Node n, XPath xpath, List<Input> list, String annotation) {
	

		Node annotNode = n.getAttributes().getNamedItem("sawsdl:modelReference");
		String annot ="";
		if(annotNode != null) {
			annot = annotNode.getNodeValue();
		}
		//System.out.println(n.getAttributes().item(1).getNodeName());
		try {
			String first = getLocalName(n.getNodeName());

			if(first.equals("element")){
				
				if(n.getAttributes().getNamedItem("type") == null){
					
					//list.add(n.getAttributes().getNamedItem("name").getNodeValue());
					
				}
				
				if(n.getAttributes().getNamedItem("type") != null){
					String attributeType= n.getAttributes().getNamedItem("type").getNodeValue();

					
					if(isSchemaNamespace(attributeType)) {
						//System.out.println(attributeType);
						Input i;
	
						
						if(annotNode != null) {
						
							i = new Input(n.getAttributes().getNamedItem("name").getNodeValue(),
									annotNode.getNodeValue());
						}
						else {
							if(!annotation.isEmpty()) {
								i = new Input(n.getAttributes().getNamedItem("name").getNodeValue(),
										annotation);
							}
							else{
								i = new Input(n.getAttributes().getNamedItem("name").getNodeValue(),
										"");
							}
						}
						list.add(i);
					}
				}
			}
	
			
			
			String expr = "child::node()//xsd:element";
			NodeList nodeList = (NodeList)xpath.compile(expr).evaluate(n, XPathConstants.NODESET);
			for(int i = 0; i<nodeList.getLength(); i++) {
				Node node = nodeList.item(i);
				//Osäker om nedan behöves
				
				annotNode = node.getAttributes().getNamedItem("sawsdl:modelReference");
				if(annotNode != null) {
					annot = annotNode.getNodeValue();
				}
				
				if(node.getAttributes().getNamedItem("type") == null){
					//list.add(node.getAttributes().getNamedItem("name").getNodeValue());
					
					recursiveTraverse(node, xpath, list, annot);
					continue;
				}
				
				String attributeType= node.getAttributes().getNamedItem("type").getNodeValue();
				if(isSchemaNamespace(attributeType)) {
					recursiveTraverse(node, xpath, list, annot);
					//list.add(node.getAttributes().getNamedItem("name").getNodeValue());
				}
				else{
									String localName = getLocalName(attributeType);
					expr = "//*[@name='" + localName + "']";
					Node nextNode = (Node) xpath.compile(expr).evaluate(doc, XPathConstants.NODE);
					if(!nextNode.getNodeName().equals("xsd:complexType")){
						list.add(new Input(node.getAttributes().getNamedItem("name").getNodeValue(), annotation));
					}
					//System.out.println(nextNode.getNodeName());
					recursiveTraverse(node, xpath, list, annot);
					recursiveTraverse(nextNode, xpath, list, annot);
				}
			}
		} catch (XPathExpressionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void testSa(String filePath){
		
		WSDLParser parser = new WSDLParser();
		//verkar alltså ej kunna gå denna väg av någon jävla anledning
		//Binding vägen ist?
		Definitions defs = parser
				.parse(filePath);

		System.out.println(defs.getTargetNamespace());
		//System.out.println(defs.getSchemas().get(0).getAsString());
		defs.getSchemas().get(0).setTargetNamespace(defs.getTargetNamespace());
		System.out.println(defs.getSchemas().get(0).getAsString());
//		System.out.println(defs.getOperations().get(0).getInput().getMessage().getParts().get(0).getTypePN());
		//String name = defs.getOperations().get(0).getInput().getMessage().getParts().get(0).getTypePN().getLocalName();
//		System.out.println(name);
//		System.out.println(defs.getMessages().get(0).getParts().get(0).getType().getName());
		
	}

	private void printOperationList(List<id2208Project.Operation> list) {
		for(id2208Project.Operation o: list) {
			System.out.println();
			System.out.println("Operation name: " + o.getName());
			System.out.println();
			System.out.println("input: ");
			for(String s : o.getInputList()){
				System.out.println(s);
			}
			System.out.println();
			System.out.println("output: ");
			for(String s: o.getOutputList()) {
				System.out.println(s);
			}
		}
	}

	private void printOperationList2(List<id2208Project.Operationa> list) {
		for(id2208Project.Operationa o: list) {
			System.out.println();
			System.out.println("Operation name: " + o.getName());
			System.out.println();
			System.out.println("input: ");
			for(Input s : o.getInputList()){
				System.out.println(s.getName() + "  " + s.getAnnotation());
			}
			System.out.println();
			System.out.println("output: ");
			for(Input s: o.getOutputList()) {
				System.out.println(s.getName() + "  " + s.getAnnotation());
				
			}
		}
	}
	private void compareWs(List<id2208Project.Operation> opInputList, List<id2208Project.Operation> opOutputList, 
			boolean matchingLimit){

		MatchedWebServiceType m = new MatchedWebServiceType();
		m.setInputServiceName("ws1");
		m.setOutputServiceName("ws2");
		ws.getMacthing().add(m);

		double serviceScore = 0;
		int nbrOperationMatch = 0;
		for(id2208Project.Operation op1 : opInputList) {
			for(id2208Project.Operation op2 : opOutputList) {
				nbrOperationMatch++;
				String op1Name= op1.getName();
				String op2Name = op2.getName();

				MatchedOperationType mOp = new MatchedOperationType();
				mOp.setInputOperationName(op1Name);
				mOp.setOutputOperationName(op2Name);
				m.getMacthedOperation().add(mOp);
				List<String> argumentInput = op1.getInputList();
				List<String> argumentOutput = op2.getOutputList();
				int min = Math.min(op1.getInputList().size(), op2.getOutputList().size());
				int numberOfTimes = min;
				double opSum = 0;
				for(int i = 0; i < min; i++) {
					String input = argumentInput.get(i);
					String output = argumentOutput.get(i);
					if(matchingLimit){
						if(0.8 > EditDistance.getSimilarity(input, output)){
							numberOfTimes--;
							continue;
						}
					}
					MatchedElementType mElem = new MatchedElementType();
					mElem.setInputElement(input);

					mElem.setOutputElement(output);
					mElem.setScore(EditDistance.getSimilarity(input, output));
					mOp.getMacthedElement().add(mElem);
					opSum += EditDistance.getSimilarity(input, output);
				}
				double avg;
				if(numberOfTimes==0) {
					avg = 0;
				} else {
					avg = opSum / numberOfTimes;	
				}

				serviceScore += avg;
				mOp.setOpScore(avg);

			}
		}
		m.setWsScore(serviceScore/nbrOperationMatch);
	}

	private void writeFile(){
		File file = new File(outputPath);

		try {
			Marshaller m = JAXBContext.newInstance(WSMatchingType.class).createMarshaller();
			m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
			m.marshal(ws, file);

		} catch (PropertyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JAXBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	//Append to operationList. After method completion operationList is populated with the correct data
	private List<id2208Project.Operation> parseWsdl(String filePath){
		List<id2208Project.Operation> operationList = new ArrayList<>();
		WSDLParser parser = new WSDLParser();
		int count = 0;
		Definitions defs = parser
				.parse(filePath);
		defs.getSchemas().get(0).setTargetNamespace(defs.getTargetNamespace());
		for (PortType pt : defs.getPortTypes()) {

			for (Operation op : pt.getOperations()) {
				//System.out.println("operation: " + op.getName());
				String operationName = op.getName();

				//Input
				//System.out.println("Input: ");
				List<String> inputList = new ArrayList<String>();
				for(Part p : op.getInput().getMessage().getParts()){	
					//No type
					if(p.getElement() ==  null && p.getType().getName() == null){
						//System.out.println(p.getName());
						inputList.add(p.getName());
					}
					//Handle missing root element
					else if(p.getElement() == null && p.getType().getName() != null){
						Schema s = p.getType().getSchema();

						s.newElement("tmp1", p.getType().getName());
						List<String> partInputList = new ArrayList<String>(); 
						listParams(s.getElement("tmp1"), partInputList);
						inputList.addAll(partInputList);
					}
					else {
						List<String> partInputList = new ArrayList<String>(); 
						listParams(defs.getElement(p.getElement().getQname()), partInputList);
						inputList.addAll(partInputList);	
					}
				}

				//Output


				//System.out.println();
				//System.out.println("output: ");
				List<String> outputList = new ArrayList<String>();
				for(Part p : op.getOutput().getMessage().getParts()){	
					//No type
					if(p.getElement() ==  null && p.getType().getName() == null){
						//System.out.println(p.getName());
						outputList.add(p.getName());
					}
					//Handle missing root element
					else if(p.getElement() == null && p.getType().getName() != null){
						Schema s = p.getType().getSchema();
						s.newElement("tmp2", p.getType().getName());
						List<String> partOutputList = new ArrayList<String>(); 
						listParams(s.getElement("tmp2"), partOutputList);
						outputList.addAll(partOutputList);
					}
					else {
						List<String> partOutputList = new ArrayList<String>();
						listParams(defs.getElement(p.getElement().getQname()), partOutputList);
						outputList.addAll(partOutputList);	
					}
				}

				operationList.add(new id2208Project.Operation(operationName, inputList, outputList));
			}
		}
		

		return operationList;
	}



	
	/**
	 * Get the params associated with Operation element 
	 * @param element - Operation element whose argument to be inspected
	 * @return - A list containing all parameters associated with Operation
	 */
	private void listParams(Element element, List <String> list){

		//System.out.println(element.getName());
		Schema schema = element.getSchema();
		List<Schema> schemaList = schema.getAllSchemas();
		TypeDefinition someType = element.getEmbeddedType();
		//System.out.println(element.getName());
		
		//Base case
		if(someType == null){
			
			if(element.getType() == null || element.getType().getNamespaceURI().equals("http://www.w3.org/2001/XMLSchema")) {
				//base case
				//System.out.println(element.getName());
			
				list.add(element.getName());
				
			}
		
			else {
			
				for(Schema s : schemaList) {
					
					if(s.getType(element.getType().getLocalPart()) instanceof ComplexType){	
						
						ComplexType c = s.getComplexType(element.getType().getLocalPart());
						if(c.getModel() instanceof ComplexContent) {
							Derivation der = ((ComplexContent) c.getModel()).getDerivation();
							//System.out.println(c.getModel().getAsString());
							
							TypeDefinition typeDef = s.getType((der.getBase().getQualifiedName()));
							if(typeDef instanceof ComplexType){
								
								if(s.getComplexType(der.getBase().getQualifiedName()).getSequence()!= null) {
									
									for(Element e : s.getComplexType(der.getBase().getQualifiedName()).
											getSequence().getElements()) {
									
										listParams(e, list);
									}
								}

							}


						}
						//c.getSequence().getElements();
						//If all type
						else if(c.getModel() instanceof ModelGroup){
							System.out.println("hej");
							//System.out.println(c.getName());
							ModelGroup m = (ModelGroup) c.getModel();
							m.getElements();
							for(Element e: ((ModelGroup) c.getModel()).getElements()) {
								
								listParams(e, list);		
							}
						}

						else {
						
							for(Element e: s.getComplexType(element.getType().
									getLocalPart()).getSequence().getElements()) {
								
								listParams(e, list);		
							}
						}
					}
				}
			}
		}
		
		if(someType instanceof ComplexType){
			
			ComplexType c = (ComplexType) someType;
			//In case of all
			if(c.getModel() instanceof ModelGroup){

				ModelGroup m = (ModelGroup) c.getModel();
				m.getElements();
				for(Element e: ((ModelGroup)c.getModel()).getElements()) {
				
					listParams(e, list);		
				}
			}

			else{
			
				if(c.getSequence() != null) {
					for(Element ee : ((ComplexType) someType).getSequence().getElements()){
						//String type = ee.getType().getLocalPart();
					
						listParams(ee, list);

					}
				}
			}


		}

		if(someType instanceof SimpleType) {
			System.out.println(element.getName());
			
			list.add(element.getName());
		}


	}
}
