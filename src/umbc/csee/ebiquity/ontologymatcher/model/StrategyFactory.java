package umbc.csee.ebiquity.ontologymatcher.model;

import umbc.csee.ebiquity.ontologymatcher.model.ResourceModel.ResourceLevel;

public class StrategyFactory {
	
	
	public MatchStrategy createStrategy(ResourceModel sr, ResourceModel tr){
		return chooseStrategy(sr, tr);
	}
	
	private MatchStrategy chooseStrategy(ResourceModel sr, ResourceModel tr){
		
		ResourceLevel  sResType = sr.getResourceLevel();
		ResourceLevel tResType = tr.getResourceLevel();
		
		if(sResType == ResourceLevel.CLASS && tResType == ResourceLevel.INSTANCE) {
			System.out.println("call CIMatchStrategy");
			return new CIMatchStrategy(sr, tr);
		} else if (sResType == ResourceLevel.CLASS && tResType == ResourceLevel.CLASS){
			System.out.println("call CCMatchStrategy");
			return new CCMatchStrategy();
		} else if (sResType == ResourceLevel.INSTANCE && tResType == ResourceLevel.INSTANCE){
			System.out.println("call IIMatchStrategy");
			return new IIMatchStrategy();
		} else {
			System.out.println("call ICMatchStrategy");
			return new ICMatchStrategy();
		}
	}

}
