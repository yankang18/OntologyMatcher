package umbc.csee.ebiquity.ontologymatcher.utility;

public class NumericCheckResult {
	
	private boolean isNumeric;
	private String result;

	public NumericCheckResult(boolean isNumeric, String result) {
		this.isNumeric = isNumeric;
		this.result = result;
	}

	public boolean isNumeric() {
		return this.isNumeric;
	}

	public String getResult() {
		return this.result;
	}

}
