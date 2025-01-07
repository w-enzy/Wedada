package com.yupi.yudada.service.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.yupi.yudada.common.ErrorCode;
import com.yupi.yudada.constant.CommonConstant;
import com.yupi.yudada.exception.ThrowUtils;
import com.yupi.yudada.mapper.ScoringResultMapper;
import com.yupi.yudada.model.dto.scoringresult.ScoringResultQueryRequest;
import com.yupi.yudada.model.entity.App;
import com.yupi.yudada.model.entity.ScoringResult;
import com.yupi.yudada.model.entity.User;
import com.yupi.yudada.model.vo.ScoringResultVO;
import com.yupi.yudada.model.vo.UserVO;
import com.yupi.yudada.service.AppService;
import com.yupi.yudada.service.ScoringResultService;
import com.yupi.yudada.service.UserService;
import com.yupi.yudada.utils.SqlUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 评分结果服务实现
 *
 * @author <a href="https://github.com/liyupi">程序员鱼皮</a>
 * @from <a href="https://www.code-nav.cn">编程导航学习圈</a>
 */
@Service
@Slf4j
public class ScoringResultServiceImpl extends ServiceImpl<ScoringResultMapper, ScoringResult> implements ScoringResultService {

    @Resource
    private UserService userService;
    @Resource
    private AppService appService;

    /**
     * 校验数据
     *
     * @param scoringresult
     * @param add      对创建的数据进行校验
     */
    @Override
    public void validScoringResult(ScoringResult scoringresult, boolean add) {
        ThrowUtils.throwIf(scoringresult == null, ErrorCode.PARAMS_ERROR);
        // 从对象中取值
        String resultName = scoringresult.getResultName();
        Long appId = scoringresult.getAppId();
        // 创建数据时，参数不能为空
        if (add) {
            // 补充校验规则
            ThrowUtils.throwIf(StringUtils.isBlank(resultName), ErrorCode.PARAMS_ERROR);
        }
        // 修改数据时，有参数则校验
        // 补充校验规则
        if (StringUtils.isNotBlank(resultName)) {
            ThrowUtils.throwIf(resultName.length() > 128, ErrorCode.PARAMS_ERROR, "结果名称不能超过128");
            ThrowUtils.throwIf(appId==null||appId<=0, ErrorCode.PARAMS_ERROR,"appID非法");
        }
        // 补充校验规则，appid不为空时，判断app是否存在
        if (appId!=null) {
            App app=appService.getById(appId);
            ThrowUtils.throwIf(app==null, ErrorCode.PARAMS_ERROR,"应用不存在");
        }
    }

    /**
     * 获取查询条件
     *
     * @param scoringresultQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<ScoringResult> getQueryWrapper(ScoringResultQueryRequest scoringresultQueryRequest) {
        QueryWrapper<ScoringResult> queryWrapper = new QueryWrapper<>();
        if (scoringresultQueryRequest == null) {
            return queryWrapper;
        }
        // 从对象中取值
        Long id = scoringresultQueryRequest.getId();
        String resultName = scoringresultQueryRequest.getResultName();
        String resultDesc = scoringresultQueryRequest.getResultDesc();
        String resultPicture = scoringresultQueryRequest.getResultPicture();
        String resultProp = scoringresultQueryRequest.getResultProp();
        Integer resultScoreRange = scoringresultQueryRequest.getResultScoreRange();
        Long appId = scoringresultQueryRequest.getAppId();
        Long userId = scoringresultQueryRequest.getUserId();
        Long notId = scoringresultQueryRequest.getNotId();
        String searchText = scoringresultQueryRequest.getSearchText();
        String sortField = scoringresultQueryRequest.getSortField();
        String sortOrder = scoringresultQueryRequest.getSortOrder();

        // 补充需要的查询条件
        // 从多字段中搜索
        if (StringUtils.isNotBlank(searchText)) {
            // 需要拼接查询条件
            queryWrapper.and(qw -> qw.like("resultName", searchText).or().like("resultDesc", searchText));
        }
        // 模糊查询
        queryWrapper.like(StringUtils.isNotBlank(resultName), "resultName", resultName);
        queryWrapper.like(StringUtils.isNotBlank(resultDesc), "resultDesc", resultDesc);
        queryWrapper.like(StringUtils.isNotBlank(resultProp), "resultProp", resultProp);
        // 精确查询
        queryWrapper.ne(ObjectUtils.isNotEmpty(notId), "id", notId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(appId), "appId", appId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(resultScoreRange), "resultScoreRange", resultScoreRange);
        queryWrapper.eq(StringUtils.isNotBlank(resultPicture), "resultPicture", resultPicture);
        // 排序规则
        queryWrapper.orderBy(SqlUtils.validSortField(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;
    }

    /**
     * 获取评分结果封装
     *
     * @param scoringresult
     * @param request
     * @return
     */
    @Override
    public ScoringResultVO getScoringResultVO(ScoringResult scoringresult, HttpServletRequest request) {
        // 对象转封装类
        ScoringResultVO scoringresultVO = ScoringResultVO.objToVo(scoringresult);

        // 可以根据需要为封装对象补充值，不需要的内容可以删除
        // region 可选
        // 1. 关联查询用户信息
        Long userId = scoringresult.getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userService.getById(userId);
        }
        UserVO userVO = userService.getUserVO(user);
        scoringresultVO.setUser(userVO);
        // endregion

        return scoringresultVO;
    }

    /**
     * 分页获取评分结果封装
     *
     * @param scoringresultPage
     * @param request
     * @return
     */
    @Override
    public Page<ScoringResultVO> getScoringResultVOPage(Page<ScoringResult> scoringresultPage, HttpServletRequest request) {
        List<ScoringResult> scoringresultList = scoringresultPage.getRecords();
        Page<ScoringResultVO> scoringresultVOPage = new Page<>(scoringresultPage.getCurrent(), scoringresultPage.getSize(), scoringresultPage.getTotal());
        if (CollUtil.isEmpty(scoringresultList)) {
            return scoringresultVOPage;
        }
        // 对象列表 => 封装对象列表
        List<ScoringResultVO> scoringresultVOList = scoringresultList.stream().map(scoringresult -> {
            return ScoringResultVO.objToVo(scoringresult);
        }).collect(Collectors.toList());

        // 可以根据需要为封装对象补充值，不需要的内容可以删除
        // region 可选
        // 1. 关联查询用户信息
        Set<Long> userIdSet = scoringresultList.stream().map(ScoringResult::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
        // 填充信息
        scoringresultVOList.forEach(scoringresultVO -> {
            Long userId = scoringresultVO.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            scoringresultVO.setUser(userService.getUserVO(user));
        });
        // endregion

        scoringresultVOPage.setRecords(scoringresultVOList);
        return scoringresultVOPage;
    }

}
