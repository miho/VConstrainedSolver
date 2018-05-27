/*
 * Copyright (c) 2015, Alex Birkett All rights reserved.
 * Copyright (c) 2018-2018 Michael Hoffer <info@michaelhoffer.de>. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * * Neither the name of kiwi-java nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package eu.mihosoft.vconstsol.kiwi;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by alex on 25/09/2014.
 */
public class ConstraintParser {

    private static final Pattern pattern = Pattern.compile("\\s*(.*?)\\s*(<=|==|>=|[GL]?EQ)\\s*(.*?)\\s*(!(required|strong|medium|weak))?");

    final static String OPS = "-+/*^";

    public interface CassowaryVariableResolver {

        Variable resolveVariable(String variableName);
        Expression resolveConstant(String name);
    }

    public static Constraint parseConstraint(String constraintString, CassowaryVariableResolver variableResolver) throws NonlinearExpressionException {

        Matcher matcher = pattern.matcher(constraintString);
        matcher.find();
        if (matcher.matches()) {
            Variable variable = variableResolver.resolveVariable(matcher.group(1));
            RelationalOperator operator = parseOperator(matcher.group(2));
            Expression expression = resolveExpression(matcher.group(3), variableResolver);
            double strength = parseStrength(matcher.group(4));

            return new Constraint(Symbolics.subtract(variable, expression), operator, strength);
        } else {
            throw new RuntimeException("could not parse " +   constraintString);
        }
    }

    private static RelationalOperator parseOperator(String operatorString) {

        RelationalOperator operator = null;
        if ("EQ".equals(operatorString) || "==".equals(operatorString)) {
            operator = RelationalOperator.OP_EQ;
        } else if ("GEQ".equals(operatorString) || ">=".equals(operatorString)) {
            operator = RelationalOperator.OP_GE;
        } else if ("LEQ".equals(operatorString) || "<=".equals(operatorString)) {
            operator = RelationalOperator.OP_LE;
        }
        return operator;
    }

    private static double parseStrength(String strengthString) {

        double strength =  Strength.REQUIRED;
        if ("!required".equals(strengthString)) {
            strength = Strength.REQUIRED;
        } else if ("!strong".equals(strengthString)) {
            strength = Strength.STRONG;
        } else if ("!medium".equals(strengthString)) {
            strength = Strength.MEDIUM;
        } else if ("!weak".equals(strengthString)) {
            strength = Strength.WEAK;
        }
        return strength;
    }

    public static Expression resolveExpression(String expressionString, CassowaryVariableResolver variableResolver) throws NonlinearExpressionException {

        List<String> postFixExpression = infixToPostfix(tokenizeExpression(expressionString));

        Stack<Expression> expressionStack = new Stack<Expression>();

        for (String expression : postFixExpression) {
            if ("+".equals(expression)) {
                expressionStack.push(Symbolics.add(expressionStack.pop(), (expressionStack.pop())));
            } else if ("-".equals(expression)) {
                Expression a = expressionStack.pop();
                Expression b = expressionStack.pop();
                
                expressionStack.push(Symbolics.subtract(b, a));
            } else if ("/".equals(expression)) {
                Expression denominator = expressionStack.pop();
                Expression numerator = expressionStack.pop();
                expressionStack.push(Symbolics.divide(numerator, denominator));
            } else if ("*".equals(expression)) {
                expressionStack.push(Symbolics.multiply(expressionStack.pop(), (expressionStack.pop())));
            } else {
                Expression linearExpression =  variableResolver.resolveConstant(expression);
                if (linearExpression == null) {
                    linearExpression = new Expression(new Term(variableResolver.resolveVariable(expression)));
                }
                expressionStack.push(linearExpression);
            }
        }

        return expressionStack.pop();
    }

    public static List<String> infixToPostfix(List<String> tokenList) {

        Stack<Integer> s = new Stack<Integer>();

        List<String> postFix = new ArrayList<String>();
        for (String token : tokenList) {
            char c = token.charAt(0);
            int idx = OPS.indexOf(c);
            if (idx != -1 && token.length() == 1) {
                if (s.isEmpty())
                    s.push(idx);
                else {
                    while (!s.isEmpty()) {
                        int prec2 = s.peek() / 2;
                        int prec1 = idx / 2;
                        if (prec2 > prec1 || (prec2 == prec1 && c != '^'))
                            postFix.add(Character.toString(OPS.charAt(s.pop())));
                        else break;
                    }
                    s.push(idx);
                }
            } else if (c == '(') {
                s.push(-2);
            } else if (c == ')') {
                while (s.peek() != -2)
                    postFix.add(Character.toString(OPS.charAt(s.pop())));
                s.pop();
            } else {
                postFix.add(token);
            }
        }
        while (!s.isEmpty())
            postFix.add(Character.toString(OPS.charAt(s.pop())));
        return postFix;
    }

    public static List<String> tokenizeExpression(String expressionString) {
        ArrayList<String> tokenList = new ArrayList<String>();

        StringBuilder stringBuilder = new StringBuilder();
        int i;
        for (i = 0; i < expressionString.length(); i++) {
            char c = expressionString.charAt(i);
            switch (c) {
                case '+':
                case '-':
                case '*':
                case '/':
                case '(':
                case ')':
                    if (stringBuilder.length() > 0) {
                        tokenList.add(stringBuilder.toString());
                        stringBuilder.setLength(0);
                    }
                    tokenList.add(Character.toString(c));
                    break;
                case ' ':
                    // ignore space
                    break;
                default:
                    stringBuilder.append(c);
            }

        }
        if (stringBuilder.length() > 0) {
            tokenList.add(stringBuilder.toString());
        }

        return tokenList;
    }

}
