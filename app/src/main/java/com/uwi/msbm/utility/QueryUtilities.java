package com.uwi.msbm.utility;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Travis on 24/11/2015.
 */
public class QueryUtilities{

    //Builds query parameters from HashMap
    //For example ?username=x&password=y
    public static String buildUrl(String domain , String path , String... parameterPairs) {
        HashMap<String, String> hashMap = buildHashMap(parameterPairs);
        String queryParameters = buildQueryParameters(hashMap);
        return  domain + path + queryParameters;
    }

    private static String buildQueryParameters(Map<String , String> parameters){
        String parameterString = "?";
        Iterator iterator = parameters.entrySet().iterator();
        while(iterator.hasNext()){
            HashMap.Entry pair = (HashMap.Entry)iterator.next();
            parameterString += String.format("%s=%s&" , pair.getKey() , pair.getValue());
            iterator.remove();
        }
        return parameterString.substring(0 , parameterString.length()-1);
    }

    private static HashMap<String , String> buildHashMap(String... entries){
        HashMap<String , String> hashMap = new HashMap<>();
        for(int i = 0; i < entries.length; i+= 2)
            hashMap.put(entries[i] , entries[i+1]);
        return hashMap;
    }

}
