package umbc.csee.ebiquity.ontologymatcher.textprocessing;

public class TextContentMatcher{

	public enum MatchMethod {
		SYNONYM, EXACT, GGRAM
	}

	private TextContentMatcher() {
	}

	public static Matcher compile(String content, MatchMethod method) {
		Matcher matcher = null;
		switch (method) {
		case SYNONYM:
			matcher = new SynonymMatcher(content);
			break;
		case EXACT:
			matcher = new ExactStringMatcher(content);
			break;
		case GGRAM:
			matcher = new GGramMatcher(content);
			break;
		}
		return matcher;
	}
}
