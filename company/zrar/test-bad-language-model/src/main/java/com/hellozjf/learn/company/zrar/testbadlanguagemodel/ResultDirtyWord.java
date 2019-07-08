package com.hellozjf.learn.company.zrar.testbadlanguagemodel;

import lombok.Data;

/**
 * @author Jingfeng Zhou
 */
@Data
public class ResultDirtyWord {

    @ExcelColumn(value = "lineNum", col = 1)
    private String lineNum;

    @ExcelColumn(value = "sentence", col = 2)
    private String sentence;

    @ExcelColumn(value = "label_t", col = 3)
    private String labelT;

    @ExcelColumn(value = "label_pre", col = 4)
    private String labelPre;

    /**
     * 返回int的行号
     * @return
     */
    public Integer getIntLineNum() {
        return Integer.valueOf(lineNum);
    }
}
