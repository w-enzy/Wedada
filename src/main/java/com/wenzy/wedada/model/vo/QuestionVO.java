package com.wenzy.wedada.model.vo;

import cn.hutool.json.JSONUtil;
import com.wenzy.wedada.model.dto.question.QuestionContentDTO;
import com.wenzy.wedada.model.entity.Question;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 题目视图
 *
 *  
 *  
 */
@Data
public class QuestionVO implements Serializable {
    /**
     * id
     */
    private Long id;

    /**
     * 题目内容（json格式）,转换为封装类(DTO数据传输对象)避免解析json时出现异常
     */
    private List<QuestionContentDTO> questionContent;

    /**
     * 应用 id
     */
    private Long appId;

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 创建用户信息
     */
    private UserVO User;

    /**
     * vo封装类转dto转对象
     *
     * @param questionVO
     * @return
     */
    public static Question voToObj(QuestionVO questionVO) {
        if (questionVO == null) {
            return null;
        }
        Question question = new Question();
        BeanUtils.copyProperties(questionVO, question);     //将vo封装类中的属性值复制到对象question中
        List<QuestionContentDTO> questionContentDTO= questionVO.getQuestionContent();     //获取封装类中的questionContentDTO对象
        question.setQuestionContent(JSONUtil.toJsonStr(questionContentDTO));
        return question;
    }

    /**
     * 对象转dto转vo封装类
     *
     * @param question
     * @return
     */
    public static QuestionVO objToVo(Question question) {
        if (question == null) {
            return null;
        }
        QuestionVO questionVO = new QuestionVO();
        BeanUtils.copyProperties(question, questionVO);
        String questionContent = question.getQuestionContent();
        if(questionContent !=null) {
            questionVO.setQuestionContent(JSONUtil.toList(questionContent,QuestionContentDTO.class));
        }
        return questionVO;
    }
}
