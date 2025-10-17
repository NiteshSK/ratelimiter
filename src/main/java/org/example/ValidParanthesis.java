package org.example;

import java.util.HashMap;
import java.util.Stack;

public class ValidParanthesis {

    public boolean isValid(String s) {
        Stack<Character> stack = new Stack<>();
        HashMap<Character, Character> map = new HashMap<>();
        map.put('(', ')');
        map.put('[', ']');
        map.put('{', '}');


        for(Character c: s.toCharArray()){
            if(c == '(' || '[' == c || '{' == c){
                stack.push(c);
            }else if(c == ')' || ']' == c || '}' == c){
                if(stack.isEmpty()){
                    return false;
                }else {
                    Character pop = stack.pop();
                    if(c != map.get(pop) ){
                        return false;
                    }
                }
            }
        }
        return stack.isEmpty();

    }
}
