import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Reference for this code: https://notes.eatonphil.com/writing-a-simple-json-parser.html
 * @author swara
 *
 */
public class JsonParser {
	
	
	private static Set<String> jsonSyntax = new HashSet<>();

	private static void initSyntax() {
		
		jsonSyntax.add("{");
		jsonSyntax.add("}");
		jsonSyntax.add(":");
		jsonSyntax.add(",");
		jsonSyntax.add("[");
		jsonSyntax.add("]");
		
	}
	
	private static Object[] getString(String str) throws Exception {
		
		//System.out.println("here" + str);
		String parsedString = "";

		if (str.charAt(0) == '"') {
			str = str.substring(1);
		} else {
			Object[] res = new Object[2];
			res[0] = null;
			res[1] = str;	
			return res;
		}
		
		//System.out.println("here1" + str);

		for (int i = 0; i < str.length(); i++) {
			if (str.charAt(i) == '"') {
				
				Object[] res = new Object[2];
				res[0] = parsedString;
				res[1] = str.substring(parsedString.length() + 1);
				
				return res;
				
			} else {
				parsedString += String.valueOf(str.charAt(i));
			}
		}
		
		throw new Exception("Expected end of string");
			        
	}
	
	private static Object[] getNumber(String str) {
		
		
		String parsedNumber = "";

		Set<String> numberChars = new HashSet<>();
		
		numberChars.add("0");
		numberChars.add("1");
		numberChars.add("2");
		numberChars.add("3");
		numberChars.add("4");
		numberChars.add("5");
		numberChars.add("6");
		numberChars.add("7");
		numberChars.add("8");
		numberChars.add("9");
		numberChars.add("-");
		numberChars.add("e");
		numberChars.add(".");
		
		for (int i = 0; i < str.length(); i++) {
			if (numberChars.contains(String.valueOf(str.charAt(i)))) {
				parsedNumber += String.valueOf(str.charAt(i));
			} else {
				break;
			}
		}

		String remaining = str.substring(parsedNumber.length());

		if (parsedNumber.length() == 0) {
			
			Object[] res = new Object[2];
			res[0] = null;
			res[1] = str;
			
			return res;
		}
		
		if (parsedNumber.contains(".")) {
			
			Object[] res = new Object[2];
			res[0] = Double.valueOf(parsedNumber);
			res[1] = remaining;
			
			return res;
			
		}

		Object[] res = new Object[2];
		res[0] = Integer.valueOf(parsedNumber);
		res[1] = remaining;
		
		return res;
	}
	
	private static Object[] getBoolean(String str) {
		
		int strLen = str.length();

		if (strLen >= 4 && str.substring(0, 4).equals("true")) {
			
			Object[] res = new Object[2];
			res[0] = true;
			res[1] = str.substring(4);
			return res;
			
		} else if (strLen >= 5 && str.substring(0, 5).equals("false")) {
			
			Object[] res = new Object[2];
			res[0] = false;
			res[1] = str.substring(5);
			return res;
			
		}
		
		Object[] res = new Object[2];
		res[0] = null;
		res[1] = str;
		
		return res;
	}
	
	
	private static Object[] getNull(String str) {
		
		int strLen = str.length();
		
		//System.out.println("here2" + str);
		
		if (strLen >= 4 && str.substring(0, 4).equals("null")) {
			
			Object[] res = new Object[2];
			res[0] = true;
			res[1] = str.substring(4);
			return res;
		}
			        		
		Object[] res = new Object[2];
		res[0] = null;
		res[1] = str;
		
		return res;
	}
	
	
	
	private static List<Object> getTokens(String jsonString) throws Exception {
		
		List<Object> tokens = new ArrayList<>();

		while (jsonString.length() > 0) {

			Object[] res = getString(jsonString);
			jsonString = (String) res[1];
			if (res[0] != null) {
				tokens.add(res[0]);
			    continue;
			}
			
			res = getNumber(jsonString);
			jsonString = (String) res[1];
			if (res[0] != null) {
				tokens.add(res[0]);
			    continue;
			}
			
			res = getBoolean(jsonString);
			jsonString = (String) res[1];
			if (res[0] != null) {
				tokens.add(res[0]);
			    continue;
			}
			
			res = getNull(jsonString);
			jsonString = (String) res[1];
			if (res[0] != null) {
				tokens.add(null);
			    continue;
			}
			
			if (jsonString.charAt(0) == ' ') {
				
				jsonString = jsonString.substring(1);
				
			} else if (jsonSyntax.contains(String.valueOf(jsonString.charAt(0)))) {
				
				tokens.add(String.valueOf(jsonString.charAt(0)));
				jsonString = jsonString.substring(1);
				
			} else {
				throw new Exception("Unexpected character : " + jsonString.charAt(0));
			}
		}  

	    return tokens;
	}
	
//	def parse_array(tokens):
//	    return [], tokens
//
//	def parse_object(tokens):
//	    return {}, tokens
	
	private static Object[] parseArray(List<Object> tokens) throws Exception {
	
		List<Object> array = new ArrayList<>();
		
		
	    Object t = tokens.get(0);
	    
	    if ("]".equals(t)) {
	    	Object[] res = new Object[2];
	    	res[0] = array;
	    	tokens.remove(0);
	    	res[1] = tokens;
	        return res;
	    }

	    while (true) {
	    	
	    	Object[] res = parseTokens(tokens);
	        tokens = (List<Object>) res[1];
	        array.add(res[0]);

	        t = tokens.get(0);
	        
	        if ("]".equals(t)) {
	        	res = new Object[2];
		    	res[0] = array;
		    	tokens.remove(0);
		    	res[1] = tokens;
		        return res;
	        } else if (!",".equals(t)) {
	        	throw new Exception ("Expected comma after object in array");
	        } else {
	            tokens.remove(0);
	        }
	    }

	    //throw new Exception("Expected end-of-array bracket");
	}
	
	private static Object[] parseObject (List<Object> tokens) throws Exception {
		
		Map<String, Object> map = new HashMap<>();
		
		Object t = tokens.get(0);
		
		if ("}".equals(t)) {
			
			Object[] res = new Object[2];
			res[0] = map;
			tokens.remove(0);
			res[1] = tokens;
			return res;
		}

		while (true) {
			Object key = tokens.get(0);
			
		    if (key instanceof String) {
		    	tokens.remove(0);
		    } else {
		        throw new Exception("Expected string key, got: " + key);
		    }

		    if (!":".equals(tokens.get(0))) {
		    	throw new Exception("Expected colon after key in object, got: " + tokens.get(0));
		    }

		    tokens.remove(0);
		    Object[] res = parseTokens(tokens);
		    tokens = (List<Object>) res[1];
		    map.put((String)key, res[0]);
		    

		    t = tokens.get(0);
		    
		    if ("}".equals(t)) {
		    	res = new Object[2];
		    	res[0] = map;
		    	tokens.remove(0);
		    	res[1] = tokens;
		    	return res;
		    } else if (!",".equals(t)) {
		        throw new Exception("Expected comma after pair in object, got: "  + t);
		    }

		    tokens.remove(0);
		}

	}
	
	private static Object[] parseTokens(List<Object> tokens) throws Exception {
		
		Object t = tokens.get(0);
		
		if ("[".equals(t)) {
			tokens.remove(0);
			return parseArray(tokens);
			
		} else if ("{".equals(t)) {
			tokens.remove(0);
			return parseObject(tokens);
		} else {
			
			Object[] res = new Object[2];
			res[0] = t;
			tokens.remove(0);
			res[1] = tokens;
			return res;
		}
		
	}
	
	private static Map<String, Object> parse(String jsonString) {
		
		List<Object> tokens = new ArrayList<>();
		try {
			tokens = getTokens(jsonString);
		} catch (Exception e) {
			System.out.println("Tokens produced till now: " + tokens);
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		//System.out.println("Tokens produced till now: " + tokens);
		
		Object[] res = null;
		try {
			res = parseTokens(tokens);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return (Map<String, Object>) res[0];		   
		
	}

	public static void main(String[] args) {
		
		initSyntax();
		
		String sampleJson = "{" +
				"\"debug\" : \"on\"," + 
					"\"window\" : {" +
					"\"title\" : \"sample\", " +
					"\"size\": 500 " +
					"} " +
				"}";

		Map<String, Object> output = JsonParser.parse(sampleJson);
		
		System.out.println(output);
		assert output.get("debug").equals("on");
		assert ((Map<String, Object>) output.get("window")).get("title").equals("sample");
		assert ((Map<String, Object>) output.get("window")).get("size").equals(500);

	}

}
