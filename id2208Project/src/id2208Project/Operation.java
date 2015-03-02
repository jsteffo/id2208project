package id2208Project;

import java.util.List;

public class Operation {

	private String name;
	private List<String> inputList;
	private List<String> outputList;
	public Operation(String name, List<String> inputList,
			List<String> outputList) {
		super();
		this.name = name;
		this.inputList = inputList;
		this.outputList = outputList;
	}
	public String getName() {
		return name;
	}
	public List<String> getInputList() {
		return inputList;
	}
	public List<String> getOutputList() {
		return outputList;
	}
}
