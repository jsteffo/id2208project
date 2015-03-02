package id2208Project;


import java.util.ArrayList;
import java.util.List;

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
			"/home/stefan/programs/gitLocal/id2208project/id2208Project/resources/WSDLs/Click%26PledgeAPIProfile.wsdl";
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
				List<String> inputList = listParams(defs.getElement(op.getInput().
						getMessage().getParts().get(0).getElement().getQname()));
				
				//Output
				System.out.println();
				System.out.println("output: ");
				List<String> outputList = listParams(defs.getElement(op.getOutput().getMessage().getParts().
						get(0).getElement().getQname()));

				operationList.add(new id2208Project.Operation(operationName, inputList, outputList));
				System.out.println();
				System.out.println();
			}
		}
	}
	/**
	 * Get the params associated with Operation element 
	 * @param element - Operation element whose argument to be inspected
	 * @return - A list containing all parameters associated with Operation
	 */
	private List<String> listParams(Element element){
		List<String> paramList = new ArrayList<String>();
		ComplexType ct = (ComplexType) element.getEmbeddedType();
		Schema schema = element.getSchema();
		List<Schema> schemaList = schema.getAllSchemas();

		for(Element ee : ct.getSequence().getElements()){
			String type = ee.getType().getLocalPart();

			for(Schema currentSchema : schemaList) {
				if(ee.getType().getNamespaceURI().equals("http://www.w3.org/2001/XMLSchema")){
					System.out.println(ee.getName());
					paramList.add(ee.getName());
					break;
				} 


				if(currentSchema.getType(type) instanceof ComplexType){
					for(Element e : currentSchema.getComplexType(type).getSequence().getElements()) {

						System.out.println(e.getName());
						paramList.add(e.getName());

					}
					break;
				}
				if(currentSchema.getType(type) instanceof SimpleType){
					System.out.println(schema.getType(type).getName());
					paramList.add(schema.getType(type).getName());
					break;
				}
			}

		}
		return paramList;
	}
}
