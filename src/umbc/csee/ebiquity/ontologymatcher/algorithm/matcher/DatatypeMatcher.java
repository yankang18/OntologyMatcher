package umbc.csee.ebiquity.ontologymatcher.algorithm.matcher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;


/***
 * This class is to calculate similarity between different datatypes
 * @author kangyan2003
 */
public class DatatypeMatcher {
	
	private static HashMap<String, Datatype> datatype_mapping = new HashMap<String, DatatypeMatcher.Datatype>();
	private static HashMap<Datatype, DatatypeGroup> datatype_datetypegroup_mapping = new HashMap<DatatypeMatcher.Datatype, DatatypeMatcher.DatatypeGroup>();
	public enum Datatype {
		Literal, NCName, NMTOKEN, Name, PlainLiteral, XMLLiteral, AnyURI, NormalizedString, String, Token, 
		Base64Binary, HexBinary, DateTime, DateTimeStamp, Duration, Time, Date, gYearMonth, gYear, gMonthDay, gDay,
		gMonth, Boolean, Integer, Long, NegativeInteger, NonNegativeInteger, NonPositiveInteger, PositiveInteger, 
		Short, UnsignedInt, UnsignedLong, UnsignedShort, Float, Double, Decimal, Byte, UnsignedByte,
		Language, Rational, Real, QName, NOTATION
	}
	
	public enum DatatypeGroup {	
		STRING, BINARY, BYTE, DECIMAL, FLOAT, TEMPORAL
	}
	static {
		
		datatype_mapping.put("Literal", Datatype.Literal);
		datatype_mapping.put("NCName", Datatype.NCName);
		datatype_mapping.put("NMTOKEN", Datatype.NMTOKEN);
		datatype_mapping.put("Name", Datatype.Name);
		datatype_mapping.put("PlainLiteral", Datatype.PlainLiteral);
		datatype_mapping.put("XMLLiteral", Datatype.XMLLiteral);
		datatype_mapping.put("anyURI", Datatype.AnyURI);
		datatype_mapping.put("normalizedString", Datatype.NormalizedString);
		datatype_mapping.put("string", Datatype.String);
		datatype_mapping.put("token", Datatype.Token);
		datatype_mapping.put("QName", Datatype.QName);
		datatype_mapping.put("language", Datatype.Language);
		datatype_datetypegroup_mapping.put(Datatype.Literal, DatatypeGroup.STRING);
		datatype_datetypegroup_mapping.put(Datatype.NCName, DatatypeGroup.STRING);
		datatype_datetypegroup_mapping.put(Datatype.NMTOKEN, DatatypeGroup.STRING);
		datatype_datetypegroup_mapping.put(Datatype.Name, DatatypeGroup.STRING);
		datatype_datetypegroup_mapping.put(Datatype.PlainLiteral, DatatypeGroup.STRING);
		datatype_datetypegroup_mapping.put(Datatype.XMLLiteral, DatatypeGroup.STRING);
		datatype_datetypegroup_mapping.put(Datatype.AnyURI, DatatypeGroup.STRING);
		datatype_datetypegroup_mapping.put(Datatype.NormalizedString, DatatypeGroup.STRING);
		datatype_datetypegroup_mapping.put(Datatype.String, DatatypeGroup.STRING);
		datatype_datetypegroup_mapping.put(Datatype.Token, DatatypeGroup.STRING);
		datatype_datetypegroup_mapping.put(Datatype.QName, DatatypeGroup.STRING);
		datatype_datetypegroup_mapping.put(Datatype.Language, DatatypeGroup.STRING);
		
		datatype_mapping.put("base64Binary", Datatype.Base64Binary);
		datatype_mapping.put("hexBinary", Datatype.HexBinary);
		datatype_datetypegroup_mapping.put(Datatype.Base64Binary, DatatypeGroup.BINARY);
		datatype_datetypegroup_mapping.put(Datatype.HexBinary, DatatypeGroup.BINARY);
		
		datatype_mapping.put("dateTime", Datatype.DateTime);
		datatype_mapping.put("dateTimeStamp", Datatype.DateTimeStamp);
		datatype_mapping.put("duration", Datatype.Duration);
		datatype_mapping.put("time", Datatype.Time);
		datatype_mapping.put("date", Datatype.Date);
		datatype_mapping.put("gYear", Datatype.gYear);
		datatype_mapping.put("gYearMonth", Datatype.gYearMonth);
		datatype_mapping.put("gMonthDay", Datatype.gMonthDay);
		datatype_mapping.put("gDay", Datatype.gDay);
		datatype_mapping.put("gMonth", Datatype.gMonth);
		datatype_datetypegroup_mapping.put(Datatype.DateTime, DatatypeGroup.TEMPORAL);
		datatype_datetypegroup_mapping.put(Datatype.DateTimeStamp, DatatypeGroup.TEMPORAL);
		datatype_datetypegroup_mapping.put(Datatype.Duration, DatatypeGroup.TEMPORAL);
		datatype_datetypegroup_mapping.put(Datatype.Time, DatatypeGroup.TEMPORAL);
		datatype_datetypegroup_mapping.put(Datatype.Date, DatatypeGroup.TEMPORAL);
		datatype_datetypegroup_mapping.put(Datatype.gYear, DatatypeGroup.TEMPORAL);
		datatype_datetypegroup_mapping.put(Datatype.gYearMonth, DatatypeGroup.TEMPORAL);
		datatype_datetypegroup_mapping.put(Datatype.gMonthDay, DatatypeGroup.TEMPORAL);
		datatype_datetypegroup_mapping.put(Datatype.gDay, DatatypeGroup.TEMPORAL);
		datatype_datetypegroup_mapping.put(Datatype.gMonth, DatatypeGroup.TEMPORAL);
		
		datatype_mapping.put("decimal", Datatype.Decimal);
		datatype_mapping.put("integer", Datatype.Integer);
		datatype_mapping.put("int", Datatype.Integer);
		datatype_mapping.put("long", Datatype.Long);
		datatype_mapping.put("negativeInteger", Datatype.NegativeInteger);
		datatype_mapping.put("nonNegativeInteger", Datatype.NonNegativeInteger);
		datatype_mapping.put("NonPositiveInteger", Datatype.NonPositiveInteger);
		datatype_mapping.put("positiveInteger", Datatype.PositiveInteger);
		datatype_mapping.put("short", Datatype.Short);
		datatype_mapping.put("unsignedInt", Datatype.UnsignedInt);
		datatype_mapping.put("unsignedLong", Datatype.UnsignedLong);
		datatype_mapping.put("unsignedShort", Datatype.UnsignedShort);
		datatype_datetypegroup_mapping.put(Datatype.Decimal, DatatypeGroup.DECIMAL);
		datatype_datetypegroup_mapping.put(Datatype.Integer, DatatypeGroup.DECIMAL);
		datatype_datetypegroup_mapping.put(Datatype.Long, DatatypeGroup.DECIMAL);
		datatype_datetypegroup_mapping.put(Datatype.NegativeInteger, DatatypeGroup.DECIMAL);
		datatype_datetypegroup_mapping.put(Datatype.NonNegativeInteger, DatatypeGroup.DECIMAL);
		datatype_datetypegroup_mapping.put(Datatype.NonPositiveInteger, DatatypeGroup.DECIMAL);
		datatype_datetypegroup_mapping.put(Datatype.PositiveInteger, DatatypeGroup.DECIMAL);
		datatype_datetypegroup_mapping.put(Datatype.Short, DatatypeGroup.DECIMAL);
		datatype_datetypegroup_mapping.put(Datatype.UnsignedInt, DatatypeGroup.DECIMAL);
		datatype_datetypegroup_mapping.put(Datatype.UnsignedLong, DatatypeGroup.DECIMAL);
		datatype_datetypegroup_mapping.put(Datatype.UnsignedShort, DatatypeGroup.DECIMAL);

		datatype_mapping.put("float", Datatype.Float);
		datatype_mapping.put("double", Datatype.Double);
		datatype_datetypegroup_mapping.put(Datatype.Float, DatatypeGroup.FLOAT);
		datatype_datetypegroup_mapping.put(Datatype.Double, DatatypeGroup.FLOAT);
		
		datatype_mapping.put("byte", Datatype.Byte);
		datatype_mapping.put("unsignedByte", Datatype.UnsignedByte);
		datatype_datetypegroup_mapping.put(Datatype.Byte, DatatypeGroup.BYTE);
		datatype_datetypegroup_mapping.put(Datatype.UnsignedByte, DatatypeGroup.BYTE);
		
		datatype_mapping.put("rational", Datatype.Rational);
		datatype_mapping.put("real", Datatype.Real);
		datatype_mapping.put("NOTATION", Datatype.NOTATION);
		datatype_mapping.put("boolean", Datatype.Boolean);
	}
	
	public static Datatype getDatatype(String typeName){
		return datatype_mapping.get(typeName);
	}
	
	public static List<String> getAllDatatypes(){
		Set<String> dataytype_set = datatype_mapping.keySet();
		List<String> returns = new ArrayList<String>();
		returns.addAll(dataytype_set);
		Collections.sort(returns);
		return returns;
	}
	
	/***
	 * calculate the similarity between two datatypes.
	 * @param sourceType
	 * @param targetType
	 * @return similarity
	 */
	public static double getSimilarityScore(Datatype sourceType, Datatype targetType){
		
		if(sourceType == targetType){
			return 1.0;
		}
		DatatypeGroup sourceGroup = getGroupType(sourceType);
		DatatypeGroup targetGroup = getGroupType(targetType);
		if (sourceGroup != null && targetGroup != null && sourceGroup == targetGroup) {
			return 0.8;
		}
		return 0.0;
	}

	public static DatatypeGroup getGroupType(Datatype type) {
		return datatype_datetypegroup_mapping.get(type);
	}
}
