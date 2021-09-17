package com.gvnn.rpn;

public class To_RPN {

    public static String convertaion(String input) {
        StringBuilder output = new StringBuilder();
        if (input.charAt(0)>=48 && input.charAt(0)<=57) {
            output.append(input.charAt(0));
        } else {
            System.err.println("First char is not number.");
            System.exit(1);
        }
        for (int i = 1; i < input.length(); i++)
        {
            if (input.charAt(i)>=48 && input.charAt(i)<=57) {
                output.append(' ');
                output.append(input.charAt(i));
            } else {
                output.append(' ');
                output.append(input.charAt(i + 1));
                output.append(' ');
                output.append(input.charAt(i));
                i++;
            }
        }
        return output.toString();
    }
}
