package Ecom;

import java.util.Map;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Utils {
    public static Map<String, String> stringToHashMap(String strMap) {
    	System.out.println(strMap);
    	List<String> keysList = Arrays.asList("Produit","prix","quantite", "delai");

        Map<String, String> map = new HashMap<>();
        String[] pairs = strMap.split(", ");
        for (int i = 0; i < pairs.length; i++) {
            map.put(keysList.get(i), pairs[i]);
        }
        return map;
    }
}
