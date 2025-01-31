package com.wenzy.wedada.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.wenzy.wedada.model.dto.scoringresult.ScoringResultQueryRequest;
import com.wenzy.wedada.model.entity.ScoringResult;
import com.wenzy.wedada.model.vo.ScoringResultVO;

import javax.servlet.http.HttpServletRequest;

/**
 * 评分结果服务
 *
 *  
 *  
 */
public interface ScoringResultService extends IService<ScoringResult> {

    /**
     * 校验数据
     *
     * @param scoringresult
     * @param add 对创建的数据进行校验
     */
    void validScoringResult(ScoringResult scoringresult, boolean add);

    /**
     * 获取查询条件
     *
     * @param scoringresultQueryRequest
     * @return
     */
    QueryWrapper<ScoringResult> getQueryWrapper(ScoringResultQueryRequest scoringresultQueryRequest);
    
    /**
     * 获取评分结果封装
     *
     * @param scoringresult
     * @param request
     * @return
     */
    ScoringResultVO getScoringResultVO(ScoringResult scoringresult, HttpServletRequest request);

    /**
     * 分页获取评分结果封装
     *
     * @param scoringresultPage
     * @param request
     * @return
     */
    Page<ScoringResultVO> getScoringResultVOPage(Page<ScoringResult> scoringresultPage, HttpServletRequest request);
}
