package com.sandh.billanalyzer.utility;


import java.io.BufferedReader;
import java.io.Reader;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by hamed on 08/02/2016.
 */
public class CharacterCounter {

    public Map<Character,Long> countCharacters(String strIn){

        Stream charsInt = strIn.chars().mapToObj(i->(char)i);
        Map<Character, Long> countResult = getCharacterLongMapFromStream(charsInt);

        return countResult;
    }

    private Map<Character, Long> getCharacterLongMapFromStream(Stream chars) {
        Map result = (Map) chars.collect(
                    Collectors.groupingBy(Function.identity(),Collectors.counting()));

        return result;
    }

    public Map<Character,Long> countCharacterFrequency(Reader strIn){
        BufferedReader bufferedReader = new BufferedReader(strIn);
        Stream stream= bufferedReader.lines().flatMap((String str)->str.chars().mapToObj(i->(char)i));
        Map<Character, Long> countResult = getCharacterLongMapFromStream(stream);
        return countResult;
    }
}
