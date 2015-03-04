package id2208Project;

import java.util.List;

public class Operationa {

	private String name;
	private List<Input> inputList;
	private List<Input> outputList;
	public Operationa(String name, List<Input> inputList,
			List<Input> outputList) {
		super();
		this.name = name;
		this.inputList = inputList;
		this.outputList = outputList;
	}
	public String getName() {
		return name;
	}
	public List<Input> getInputList() {
		return inputList;
	}
	public List<Input> getOutputList() {
		return outputList;
	}
}
