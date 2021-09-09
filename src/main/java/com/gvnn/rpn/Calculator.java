package com.gvnn.rpn;

import java.util.Stack;

public class Calculator {

    private Stack<Double> valuesStack = new Stack<Double>();
    private Stack<Instruction> instructionsStack = new Stack<Instruction>();
    private int currentTokenIndex = 0;

    private Double tryParseDouble(String str) {
        try {
            return Double.parseDouble(str);
        } catch (NumberFormatException nfe) {
            return null;
        }
    }

//    Обрабатывает токен строки RPN
//
//    @param token Токен RPN
//    @param isUndoOperation указывает, является ли операция операцией отмены.
//    @throws CalculatorException

    private void processToken(String token, boolean isUndoOperation) throws CalculatorException {
        Double value = tryParseDouble(token);
        if (value == null) {
            processOperator(token, isUndoOperation);
        } else {
            valuesStack.push(Double.parseDouble(token));
            if (!isUndoOperation) {
                instructionsStack.push(null);
            }
        }
    }

//    Выполняет операцию в стеке
//
//    @param operatorString Действительный оператор RPN
//    @param isUndoOperation указывает, является ли операция операцией отмены.
//    @throws CalculatorException
    private void processOperator(String operatorString, boolean isUndoOperation) throws CalculatorException {

        if (valuesStack.isEmpty()) {
            throw new CalculatorException("empty stack");
        }

        Operator operator = Operator.getEnum(operatorString);
        if (operator == null) {
            throw new CalculatorException("invalid operator");
        }

        // очищаем полностью стек
        if (operator == Operator.CLEAR) {
            clearStacks();
            return;
        }

        if (operator == Operator.UNDO) {
            undoLastInstruction();
            return;
        }

        if (operator.getOperandsNumber() > valuesStack.size()) {
            throwInvalidOperand(operatorString);
        }

        Double firstOperand = valuesStack.pop();
        Double secondOperand = (operator.getOperandsNumber() > 1) ? valuesStack.pop() : null;

        Double result = operator.calculate(firstOperand, secondOperand);

        if (result != null) {
            valuesStack.push(result);
            if (!isUndoOperation) {
                instructionsStack.push(new Instruction(Operator.getEnum(operatorString), firstOperand));
            }
        }

    }

    private void undoLastInstruction() throws CalculatorException {
        if (instructionsStack.isEmpty()) {
            throw new CalculatorException("no operations to undo");
        }

        Instruction lastInstruction = instructionsStack.pop();
        if (lastInstruction == null) {
            valuesStack.pop();
        } else {
            eval(lastInstruction.getReverseInstruction(), true);
        }
    }

    private void clearStacks() {
        valuesStack.clear();
        instructionsStack.clear();
    }

    private void throwInvalidOperand(String operator) throws CalculatorException {
        throw new CalculatorException(
                String.format("operator %s (position: %d): insufficient parameters", operator, currentTokenIndex));
    }

    public Stack<Double> getValuesStack() {
        return valuesStack;
    }

    public Double getStackItem(int index) {
        return valuesStack.get(index);
    }

    public void eval(String input) throws CalculatorException {
        eval(input, false);
    }

    private void eval(String input, boolean isUndoOperation) throws CalculatorException {
        if (input == null) {
            throw new CalculatorException("Input cannot be null.");
        }
        currentTokenIndex = 0;
        String[] result = input.split("\\s");
        for (String aResult : result) {
            currentTokenIndex++;
            processToken(aResult, isUndoOperation);
        }
    }
}
