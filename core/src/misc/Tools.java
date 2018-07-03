package misc;

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
}
