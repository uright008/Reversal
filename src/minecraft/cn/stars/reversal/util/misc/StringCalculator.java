package cn.stars.reversal.util.misc;

import java.text.DecimalFormat;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Pattern;

public class StringCalculator {
    public static String calculate(String expression) {
        // 去除所有空格
        expression = expression.replaceAll("\\s+", "");
        if (expression.isEmpty()) return "";

        // 解析表达式为token列表
        List<String> tokens = parseTokens(expression);
        if (tokens.isEmpty() || tokens.size() % 2 == 0) return "";

        // 验证token格式
        if (!validateTokens(tokens)) return "";

        // 创建链表便于修改
        LinkedList<String> list = new LinkedList<>(tokens);

        // 先处理所有乘除运算
        if (!processMultiplyDivide(list)) return "";

        // 再处理所有加减运算
        if (!processAddSubtract(list)) return "";

        // 最终结果应只有一个元素
        if (list.size() != 1) return "";

        // 格式化最终结果
        return formatResult(Double.parseDouble(list.get(0)));
    }

    // 解析表达式为token列表
    private static List<String> parseTokens(String expression) {
        List<String> tokens = new LinkedList<>();
        int index = 0;
        int length = expression.length();

        while (index < length) {
            char c = expression.charAt(index);

            // 处理运算符
            if (c == '+' || c == '*' || c == 'x' || c == 'X' || c == '÷' || c == '/') {
                tokens.add(String.valueOf(c));
                index++;
            }
            // 处理减号（可能是运算符或负号）
            else if (c == '-') {
                // 负号情况：开头或运算符后
                if (tokens.isEmpty() || isOperator(tokens.get(tokens.size() - 1))) {
                    int start = index;
                    index++;
                    // 读取完整的数字（包括小数点）
                    while (index < length && (Character.isDigit(expression.charAt(index)) ||
                            expression.charAt(index) == '.')) {
                        index++;
                    }
                    tokens.add(expression.substring(start, index));
                }
                // 运算符情况
                else {
                    tokens.add("-");
                    index++;
                }
            }
            // 处理数字和小数点
            else if (Character.isDigit(c) || c == '.') {
                int start = index;
                while (index < length && (Character.isDigit(expression.charAt(index)) ||
                        expression.charAt(index) == '.' ||
                        expression.charAt(index) == 'E' ||
                        expression.charAt(index) == 'e')) {
                    index++;
                }
                tokens.add(expression.substring(start, index));
            }
            // 无效字符
            else {
                return new LinkedList<>();
            }
        }
        return tokens;
    }

    // 验证token格式
    private static boolean validateTokens(List<String> tokens) {
        for (int i = 0; i < tokens.size(); i++) {
            String token = tokens.get(i);
            // 偶数位置应为数字
            if (i % 2 == 0) {
                if (!isValidNumber(token)) return false;
            }
            // 奇数位置应为运算符
            else {
                if (!isOperator(token)) return false;
            }
        }
        return true;
    }

    // 检查是否为有效数字
    private static boolean isValidNumber(String token) {
        if (token.isEmpty()) return false;
        try {
            Double.parseDouble(token);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    // 检查是否为有效运算符
    private static boolean isOperator(String token) {
        return token.equals("+") || token.equals("-") || token.equals("*") ||
                token.equals("x") || token.equals("X") || token.equals("÷") || token.equals("/");
    }

    // 处理乘除运算
    private static boolean processMultiplyDivide(LinkedList<String> list) {
        int i = 1;
        while (i < list.size()) {
            String operator = list.get(i);

            // 处理乘除运算
            if (operator.equals("*") || operator.equals("x") || operator.equals("X") ||
                    operator.equals("/") || operator.equals("÷")) {

                // 获取操作数
                double num1 = Double.parseDouble(list.get(i - 1));
                double num2 = Double.parseDouble(list.get(i + 1));
                double result = 0;

                // 执行运算
                if (operator.equals("*") || operator.equals("x") || operator.equals("X")) {
                    result = num1 * num2;
                } else { // 除法运算
                    if (Math.abs(num2) < 1e-15) {
                        return false; // 除数为0
                    }
                    double quotient = num1 / num2;
                    // 除法结果四舍五入
                    if (Math.abs(quotient - Math.round(quotient)) < 1e-10) {
                        result = Math.round(quotient);
                    } else {
                        result = Math.round(quotient * 1e10) / 1e10;
                    }
                }

                // 更新列表
                list.set(i - 1, String.valueOf(result));
                list.remove(i); // 移除运算符
                list.remove(i); // 移除第二个操作数

                // 重置索引以重新检查列表
                i = 1;
            } else {
                i += 2; // 跳过加减运算符
            }
        }
        return true;
    }

    // 处理加减运算
    private static boolean processAddSubtract(LinkedList<String> list) {
        int i = 1;
        while (i < list.size()) {
            String operator = list.get(i);

            if (operator.equals("+") || operator.equals("-")) {
                // 获取操作数
                double num1 = Double.parseDouble(list.get(i - 1));
                double num2 = Double.parseDouble(list.get(i + 1));
                double result = 0;

                // 执行运算
                if (operator.equals("+")) {
                    result = num1 + num2;
                } else {
                    result = num1 - num2;
                }

                // 更新列表
                list.set(i - 1, String.valueOf(result));
                list.remove(i); // 移除运算符
                list.remove(i); // 移除第二个操作数

                // 重置索引以重新检查列表
                i = 1;
            } else {
                i += 2; // 跳过已处理的运算符
            }
        }
        return true;
    }

    // 格式化结果（整数去掉小数部分，小数去掉末尾0）
    private static String formatResult(double value) {
        if (Double.isNaN(value) || Double.isInfinite(value)) {
            return "";
        }

        // 检查是否为整数
        if (Math.abs(value - Math.round(value)) < 1e-10) {
            return String.valueOf(Math.round(value));
        }

        // 格式化小数（去末尾0）
        DecimalFormat df = new DecimalFormat("0.##########");
        return df.format(value);
    }
}