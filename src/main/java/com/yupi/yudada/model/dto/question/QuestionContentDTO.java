package com.yupi.yudada.model.dto.question;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionContentDTO {
    /*
     * 题目标题
     */
    private String title;
    /*
     * 选项选项列表
     */
    private List<Option> options;
    /*
     * 题目选项
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public class Option {
        private String result;
        private int score;
        private String value;
        private String key;
    }
}



