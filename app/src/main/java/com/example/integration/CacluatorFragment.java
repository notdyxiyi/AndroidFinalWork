package com.example.integration;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

public class CacluatorFragment extends Fragment {

    private View view;
    private TextView tvExpression;
    private String currentNumber = "";
    private String expression = "";
    private String operator = "";
    private boolean isNewOperation = true;
    private boolean isResultShown = false;
    public CacluatorFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_calcuator, container, false);
        super.onCreate(savedInstanceState);
        // 初始化
        initViews();
        // 设置按钮点击监听器
        setupButtonListeners();
        return view;
    }
    public void initViews(){
        tvExpression = view.findViewById(R.id.tvExpression);
    }
    private void setupButtonListeners() {
        // 数字按钮
        int[] numberButtonIds = {
                R.id.btn0, R.id.btn1, R.id.btn2, R.id.btn3, R.id.btn4,
                R.id.btn5, R.id.btn6, R.id.btn7, R.id.btn8, R.id.btn9
        };

        for (int id : numberButtonIds) {
            Button button = view.findViewById(id);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onNumberButtonClick(((Button) v).getText().toString());
                }
            });
        }

        // 运算符按钮
        view.findViewById(R.id.btnPlus).setOnClickListener(v -> onOperatorButtonClick("+"));
        view.findViewById(R.id.btnMinus).setOnClickListener(v -> onOperatorButtonClick("-"));
        view.findViewById(R.id.btnMultiply).setOnClickListener(v -> onOperatorButtonClick("×"));
        view.findViewById(R.id.btnDivide).setOnClickListener(v -> onOperatorButtonClick("÷"));

        // 功能按钮
        view.findViewById(R.id.btnDot).setOnClickListener(v -> onDotButtonClick());
        view.findViewById(R.id.btnEquals).setOnClickListener(v -> onEqualsButtonClick());
        view.findViewById(R.id.btnClear).setOnClickListener(v -> onClearButtonClick());
        view.findViewById(R.id.btnBackspace).setOnClickListener(v -> onBackspaceButtonClick());
    }
    private void onNumberButtonClick(String number) {
        if (isNewOperation || isResultShown) {
            // 如果刚完成计算，开始新的计算
            if (isResultShown) {
                clearAll();
            }
            currentNumber = number;
            expression = number;
            isNewOperation = false;
            isResultShown = false;
        } else {
            // 追加数字
            if (currentNumber.equals("0")) {
                currentNumber = number;
                expression = expression.substring(0, expression.length() - 1) + number;
            } else {
                currentNumber += number;
                expression += number;
            }
        }
        updateDisplays();
    }
    private void updateDisplays() {
        // 更新表达式显示
        if (expression.isEmpty()) {
            tvExpression.setText("");
        } else {
            tvExpression.setText(expression);
        }
    }
    private void clearAll() {
        currentNumber = "";
        expression = "";
        operator = "";
        isNewOperation = true;
        isResultShown = false;
    }
    private void onOperatorButtonClick(String op) {
        if (expression.isEmpty()) {
            // 如果表达式为空，以当前数字开始
            expression = currentNumber + " " + op + " ";
            operator = op;
        } else if (isResultShown) {
            // 如果刚显示结果，用结果继续计算
            expression = currentNumber + " " + op + " ";
            operator = op;
            isResultShown = false;
        } else if (operator.isEmpty()) {
            // 还没有操作符，添加操作符
            expression = currentNumber + " " + op + " ";
            operator = op;
        } else {
            // 已经有操作符，先计算再添加新的
            calculate();
            if (!currentNumber.equals("错误")) {
                expression = currentNumber + " " + op + " ";
                operator = op;
            }
        }
        currentNumber = "";
        updateDisplays();
    }
    private void calculate() {
        try {
            // 从表达式中提取数字
            String[] parts = expression.trim().split(" ");
            if (parts.length >= 3) {
                String num1Str = parts[0];
                String num2Str = currentNumber;

                double num1 = Double.parseDouble(num1Str);
                double num2 = Double.parseDouble(num2Str);
                double result = 0;

                switch (operator) {
                    case "+":
                        result = num1 + num2;
                        break;
                    case "-":
                        result = num1 - num2;
                        break;
                    case "×":
                        result = num1 * num2;
                        break;
                    case "÷":
                        if (num2 != 0) {
                            result = num1 / num2;
                        } else {
                            currentNumber = "不能除以0";
                            return;
                        }
                        break;
                }

                // 处理显示格式
                if (result == (int) result) {
                    currentNumber = String.valueOf((int) result);
                } else {
                    // 最多保留6位小数
                    currentNumber = String.format("%.6f", result);
                    // 去除多余的0
                    currentNumber = currentNumber.replaceAll("0*$", "").replaceAll("\\.$", "");
                }
            }
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            currentNumber = "错误";
        }
    }
    private void onDotButtonClick() {
        // 处理点 点击的

        if (isResultShown) {
            clearAll();
        }

        // 当前没有输入任何数字，或者刚才选择了操作符，那么自动补零->0.xxx
        if (currentNumber.isEmpty()) {
            currentNumber = "0.";
            expression = expression.isEmpty() ? "0." : expression + "0.";
        }
        // 当前数字没有小数点，那么直接追加小数点
        else if (!currentNumber.contains(".")) {
            currentNumber += ".";
            expression += ".";
        }
        updateDisplays();
    }

    private void onEqualsButtonClick() {
        if (!operator.isEmpty() && !currentNumber.isEmpty()) {
            String originalExpression = expression;
            calculate();

            if (!currentNumber.equals("错误")) {
                expression = originalExpression + " = " + currentNumber;
                isResultShown = true;
            }
            updateDisplays();
        }
    }

    private void onClearButtonClick() {
        clearAll();
        updateDisplays();
    }
    private void onBackspaceButtonClick() {
        if (isResultShown) {
            clearAll();// 如果正在显示结果，直接清空1所有
        } else if (!expression.isEmpty()) {
            if (!currentNumber.isEmpty()) {
                // 删除当前数字的最后一位
                currentNumber = currentNumber.substring(0, currentNumber.length() - 1);
                if (currentNumber.isEmpty()) {
                    currentNumber = "0";// 数字闪光了，那么设置为0
                }
            }

            // 删除表达式的最后一位
            expression = expression.substring(0, expression.length() - 1);

            // 123 + 光标在这里
            // 如果删除后最后一位是空格，继续删除操作符
            if (!expression.isEmpty() && expression.charAt(expression.length() - 1) == ' ') {
                expression = expression.substring(0, expression.length() - 3);
                operator = "";
            }

            if (expression.isEmpty()) {
                expression = "0";
                currentNumber = "0";
            }
        }
        updateDisplays();
    }
}