package misc;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tools {
    
    public static String indent(int times){
        StringBuilder stringBuilder = new StringBuilder();
    
        for (int i = 0; i < times; i++) {
            stringBuilder.append('\t');
        }
        return stringBuilder.toString();
    }
    
    public static void indentOut(int times){
        System.out.println(indent(times));
    }
    
    public static String createMatchingString(Pattern p){
        return createMatchingString(p, 50000);
    }
    
    public static String createMatchingString(Pattern p, int sampleSize){
        
        List<String> output = new ArrayList<>();
        while(output.size() < sampleSize/100) {
            StringBuilder s = new StringBuilder();
            for (int i = 0; i < sampleSize; i++) {
                char c = (char) (Math.random() * 255);
                s.append(c);
            }
    
            String all = s.toString();
            Matcher matcher = p.matcher(all);
    
            while (matcher.find()) {
                output.add(matcher.group());
            }
        }
        return output.get((int)(Math.random() * output.size()));
    }
}
