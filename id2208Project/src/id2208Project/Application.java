package id2208Project;


import groovy.xml.QName;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.PropertyException;

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
import com.predic8.schema.SimpleType;
import com.predic8.schema.TypeDefinition;
import com.predic8.wsdl.Definitions;
import com.predic8.wsdl.Operation;
import com.predic8.wsdl.Part;
import com.predic8.wsdl.PortType;
import com.predic8.wsdl.WSDLParser;
public class Application {

	private final String outputPath = "src/out/out.xml";


	private WSMatchingType ws = new WSMatchingType();
	public static void main(String args []) {
		new Application();

	}

	Application(){
		String filePath1 = 
				"/home/stefan/programs/gitLocal/id2208project/id2208Project/resources/WSDLs/ACHWorksAPIProfile.wsdl";
		String filePath2 = 
				"/home/stefan/programs/gitLocal/id2208project/id2208Project/resources/WSDLs/BangoSubscriptionBillingAPIProfile.wsdl";
		List <id2208Project.Operation> opList1 = parseWsdl(filePath1);
		List <id2208Project.Operation> opList2 = parseWsdl(filePath2);

		parseWsdl(filePath2);
		printOperationList(opList1);
		compareWs(opList1, opList2, false);
		writeFile();
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



//		MatchedWebServiceType mType = new MatchedWebServiceType();
//		mType.setInputServiceName("Ws1");
//		mType.setOutputServiceName("ws2");
//		ws.getMacthing().add(mType);
//		MatchedOperationType mOp = new MatchedOperationType();
//		mOp.setInputOperationName("inputOp");
//		mOp.setOpScore(22);
//		mOp.setOutputOperationName("outputOp");
//		mType.getMacthedOperation().add(mOp);
//		MatchedElementType mElem = new MatchedElementType();
//		mElem.setInputElement("Some inputarg");
//		mElem.setOutputElement("Some outarg");
//		mElem.setScore(99);
//		mOp.getMacthedElement().add(mElem);
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

		Definitions defs = parser
				.parse(filePath);

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


		Schema schema = element.getSchema();
		List<Schema> schemaList = schema.getAllSchemas();
		TypeDefinition someType = element.getEmbeddedType();

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

							TypeDefinition typeDef = s.getType((der.getBase().getQualifiedName()));
							if(typeDef instanceof ComplexType){
								for(Element e : s.getComplexType(der.getBase().getQualifiedName()).
										getSequence().getElements()) {
									listParams(e, list);
								}
							}


						}
						//c.getSequence().getElements();
						//If all type
						else if(c.getModel() instanceof ModelGroup){


							ModelGroup m = (ModelGroup) c.getModel();
							m.getElements();
							for(Element e: ((ModelGroup)c.getModel()).getElements()) {
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
		//		for(Element ee : ct.getSequence().getElements()){
		//			String type = ee.getType().getLocalPart();
		//			//System.out.println(ee.getEmbeddedType().getName());
		//			for(Schema currentSchema : schemaList) {
		//				if(ee.getType().getNamespaceURI().equals("http://www.w3.org/2001/XMLSchema")){
		//					System.out.println(ee.getName());
		//					paramList.add(ee.getName());
		//					break;
		//				} 
		//
		//
		//				if(currentSchema.getType(type) instanceof ComplexType){
		//					for(Element e : currentSchema.getComplexType(type).getSequence().getElements()) {
		//
		//						System.out.println(e.getName());
		//						paramList.add(e.getName());
		//
		//					}
		//					break;
		//				}
		//				if(currentSchema.getType(type) instanceof SimpleType){
		//					System.out.println(schema.getType(type).getName());
		//					paramList.add(schema.getType(type).getName());
		//					break;
		//				}
		//			}
		//
		//		}

	}
}
