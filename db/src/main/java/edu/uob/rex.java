package edu.uob;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class rex {
    private String name;
    private List<String> result;



    public rex(String name) {
        this.name = name;
        //this.dataType = dataType;

    }



    public static ArrayList<ArrayList<String>> method2(String s) {
        ArrayList<ArrayList<String>> list = new ArrayList<>();

        Pattern pattern = Pattern.compile("(\\w+)\\s*(==|>=|<=|!=|<|>|LIKE)\\s*(\\w+|'.*')");

        Matcher matcher = pattern.matcher(s);
        while (matcher.find()) {
            ArrayList<String> subList = new ArrayList<>();
            subList.add(matcher.group(1));
            subList.add(matcher.group(2));
            subList.add(matcher.group(3));
            list.add(subList);
        }

        return list;
    }


    public static ArrayList<String> method3(String s){
        ArrayList<String> andOrList = new ArrayList<String>();

        Pattern pattern = Pattern.compile("(AND|OR|and|or)");
        Matcher matcher = pattern.matcher(s);

        while(matcher.find()) {
            andOrList.add(matcher.group());
        }

        return andOrList;
    }


    public static boolean evaluateConditions(List<Boolean> conditions, List<String> operators) {
        if (conditions.size() == 0) {
            return false;
        } else if (conditions.size() == 1) {
            return conditions.get(0);
        } else {
            int index = -1;
            for (int i = 0; i < operators.size(); i++) {
                if (operators.get(i).equals("AND")) {
                    index = i;
                    break;
                } else if (operators.get(i).equals("OR")) {
                    index = i;
                    break;
                }
            }

            boolean left = evaluateConditions(conditions.subList(0, index + 1), operators.subList(0, index));
            boolean right = evaluateConditions(conditions.subList(index + 1, conditions.size()), operators.subList(index + 1, operators.size()));

            String op = operators.get(index);
            boolean result = false; // = false;
            int i =0;
            while(i==0){
                i++;
                if (op.equals("AND")) {
                    if(left ==false || right == false){
                        result= false;

                        break;
                    }
                    result = left||right;
                    return result;
                    //if(left && right){return left && right;}{return false;}
                } else if (op.equals("OR")){
                    result = left || right;

                    return result;
                } else {
                    return result;
                }
            }
            return result;
        }
    }


}