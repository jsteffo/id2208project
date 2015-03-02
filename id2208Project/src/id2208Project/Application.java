package id2208Project;


import groovy.xml.QName;

import java.util.ArrayList;
import java.util.List;

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

	public final String filePath = 
			"/home/stefan/programs/gitLocal/id2208project/id2208Project/resources/WSDLs/GlobalMatrixAPIProfile.wsdl";
	private List<id2208Project.Operation> operationList = new ArrayList<>();

	public static void main(String args []) {
		new Application();

	}

	Application(){
		parseWsdl();

	}
	//Append to operationList. After method completion operationList is populated with the correct data
	private void parseWsdl(){
		WSDLParser parser = new WSDLParser();

		Definitions defs = parser
				.parse(filePath);

		for (PortType pt : defs.getPortTypes()) {

			for (Operation op : pt.getOperations()) {
				System.out.println("operation: " + op.getName());
				String operationName = op.getName();

				//Input
				System.out.println("Input: ");
				List<String> inputList = new ArrayList<String>();
				for(Part p : op.getInput().getMessage().getParts()){	
					//No type
					if(p.getElement() ==  null && p.getType().getName() == null){
						System.out.println(p.getName());
						inputList.add(p.getName());
					}
					//Handle missing root element
					else if(p.getElement() == null && p.getType().getName() != null){
						Schema s = p.getType().getSchema();
						//QName q = p.getType().getQname();
						s.newElement("tmp1", p.getType().getName());
						List<String> partInputList = listParams(s.getElement("tmp1"));
						inputList.addAll(partInputList);
					}
					else {
						List<String> partInputList = listParams(defs.getElement(p.getElement().getQname()));
						inputList.addAll(partInputList);	
					}

				}

				//Output


				System.out.println();
				System.out.println("output: ");
				List<String> outputList = new ArrayList<String>();
				for(Part p : op.getOutput().getMessage().getParts()){	
					//No type
					if(p.getElement() ==  null && p.getType().getName() == null){
						System.out.println(p.getName());
						outputList.add(p.getName());
					}
					//Handle missing root element
					else if(p.getElement() == null && p.getType().getName() != null){
						Schema s = p.getType().getSchema();
						s.newElement("tmp2", p.getType().getName());
						List<String> partOutputList = listParams(s.getElement("tmp2"));
						outputList.addAll(partOutputList);
					}
					else {
						List<String> partInputList = listParams(defs.getElement(p.getElement().getQname()));
						inputList.addAll(partInputList);	
					}
				}

				operationList.add(new id2208Project.Operation(operationName, inputList, outputList));
				System.out.println();
				System.out.println();
			}
		}
	}

	private void inputAndOutPut() {

	}
	/**
	 * Get the params associated with Operation element 
	 * @param element - Operation element whose argument to be inspected
	 * @return - A list containing all parameters associated with Operation
	 */
	private List<String> listParams(Element element){






		List<String> paramList = new ArrayList<String>();
		//ComplexType ct = (ComplexType) element.getEmbeddedType();
		Schema schema = element.getSchema();
		List<Schema> schemaList = schema.getAllSchemas();
		TypeDefinition someType = element.getEmbeddedType();

		//Base case
		if(someType == null){
			
			if(element.getType() == null || element.getType().getNamespaceURI().equals("http://www.w3.org/2001/XMLSchema")) {
				//base case
				System.out.println(element.getName());
				return new ArrayList<String>();
			}
			else {
				for(Schema s : schemaList) {
					
					if(s.getType(element.getType().getLocalPart()) instanceof ComplexType){	
						ComplexType c = s.getComplexType(element.getType().getLocalPart());
						if(c.getModel() instanceof ComplexContent) {
							Derivation der = ((ComplexContent) c.getModel()).getDerivation();
							System.out.println(der.getBase().getLocalPart());
							TypeDefinition typeDef = s.getType((der.getBase().getLocalPart()));
							if(typeDef instanceof ComplexType){
								for(Element e : s.getComplexType(der.getBase().getLocalPart()).
										getSequence().getElements()) {
									listParams(e);
								}
							}


						}
						//c.getSequence().getElements();
						//If all type
						else if(c.getModel() instanceof ModelGroup){
							//System.out.println(c.getModel());
							
							ModelGroup m = (ModelGroup) c.getModel();
							m.getElements();
							for(Element e: ((ModelGroup)c.getModel()).getElements()) {
								listParams(e);		
							}
						}

						else {
							for(Element e: s.getComplexType(element.getType().
									getLocalPart()).getSequence().getElements()) {
								listParams(e);		
							}
						}
						

					}

				}


			}
		}

		if(someType instanceof ComplexType){
			for(Element ee : ((ComplexType) someType).getSequence().getElements()){
				//String type = ee.getType().getLocalPart();
				ee.getName();
				listParams(ee);

			}
		}

		if(someType instanceof SimpleType) {
			System.out.println(element.getName());
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
		return paramList;
	}
}
