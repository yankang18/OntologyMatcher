package umbc.csee.ebiquity.ontologymatcher.algorithm.component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class IndividualTypeInfo {
	
	private OntResourceInfo[] individualClassArray;
	private List<OntPropertyInfo> propertylst_WD;
	private List<OntPropertyInfo> propertylst_ND;
	private boolean isRenewed;
	private String key;
	
	public IndividualTypeInfo(OntResourceInfo[] classArray){
		this.individualClassArray = classArray;
		this.generateKey();
		this.propertylst_WD = new ArrayList<OntPropertyInfo>();
		this.propertylst_ND = new ArrayList<OntPropertyInfo>();
		this.isRenewed = false;
	}
	


	public void addProperty(OntPropertyInfo onPropertyInfo){
		this.propertylst_WD.add(onPropertyInfo);
		this.isRenewed = false;
	}
	
	public List<OntPropertyInfo> listProperties(){
		this.removeDuplicate();
		return propertylst_ND;
	}
	
	private void removeDuplicate() {
		if (this.isRenewed == false) {
			HashMap<String, Boolean> propertyExistenceMap = new HashMap<String, Boolean>();
			for (OntPropertyInfo ontPropertyInfo : propertylst_WD) {
				if (!propertyExistenceMap.containsKey(ontPropertyInfo.getKey())) {
					propertylst_ND.add(ontPropertyInfo);
					propertyExistenceMap.put(ontPropertyInfo.getKey(), true);
				}
			}
			this.isRenewed = true;
		}
	}
	
	private void generateKey() {
		StringBuilder key = new StringBuilder();
		for(OntResourceInfo res : individualClassArray){
//			System.out.println("res.getURI" + res.getURI());
			key.append(res.getURI());
		}
		this.key = key.toString();
	}
	
	public String getKey(){
		return key;
	}

	@Override
	public String toString() {
		return this.individualClassArray.toString();
	}

}
