package umbc.csee.ebiquity.ontologymatcher.utility;

public class DateCheckResult {
	
	private boolean isDate;
	private String result;

	public DateCheckResult(boolean isDate, String result) {
		this.isDate = isDate;
		this.result = result;
	}

	public boolean isDate() {
		return this.isDate;
	}

	public String getResult() {
		return this.result;
	}
}
