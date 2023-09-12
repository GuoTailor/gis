package com.gyh.gis.collect;

import com.gyh.gis.dto.ResponseInfo;
import com.gyh.gis.dto.req.ExamineReq;
import com.gyh.gis.dto.req.TrendReq;
import com.gyh.gis.dto.resp.ExamineResp;
import com.gyh.gis.dto.resp.StatisticResp;
import com.gyh.gis.service.ExamineInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * create by GYH on 2023/9/13
 */
@RestController
@RequestMapping("/examine")
@Tag(name = "考核统计状态")
public class ExamineInfoController {
    @Autowired
    private ExamineInfoService examineInfoService;

    /**
     * 查询小时统计
     */
    @Operation(summary = "查询小时统计")
    @PostMapping("/statistic/hour")
    public ResponseInfo<List<StatisticResp>> selectStatisticByHour(@RequestBody @Valid ExamineReq req) {
        List<StatisticResp> statisticResps = examineInfoService.selectStatisticByHour(req);
        return ResponseInfo.ok(statisticResps);
    }

    @Operation(summary = "查询天统计")
    @PostMapping("/statistic/day")
    public ResponseInfo<List<StatisticResp>> selectStatisticByDay(@RequestBody @Valid ExamineReq req) {
        List<StatisticResp> statisticResps = examineInfoService.selectStatisticByDay(req);
        return ResponseInfo.ok(statisticResps);
    }

    @Operation(summary = "查询月统计")
    @PostMapping("/statistic/month")
    public ResponseInfo<List<StatisticResp>> selectStatisticByMonth(@RequestBody @Valid ExamineReq req) {
        List<StatisticResp> statisticResps = examineInfoService.selectStatisticByMonth(req);
        return ResponseInfo.ok(statisticResps);
    }

    @Operation(summary = "查询年统计")
    @PostMapping("/statistic/year")
    public ResponseInfo<List<StatisticResp>> selectStatisticByYear(@RequestBody @Valid ExamineReq req) {
        List<StatisticResp> statisticResps = examineInfoService.selectStatisticByYear(req);
        return ResponseInfo.ok(statisticResps);
    }

    /**
     * 查询小时趋势
     */
    @Operation(summary = "查询小时趋势")
    @PostMapping("/trend/hour")
    public ResponseInfo<List<StatisticResp>> selectTrendByHour(@RequestBody @Valid TrendReq req) {
        List<StatisticResp> statisticResps = examineInfoService.selectTrendByHour(req);
        return ResponseInfo.ok(statisticResps);
    }

    @Operation(summary = "查询天趋势")
    @PostMapping("/trend/day")
    public ResponseInfo<List<StatisticResp>> selectTrendByDay(@RequestBody @Valid TrendReq req) {
        List<StatisticResp> statisticResps = examineInfoService.selectTrendByDay(req);
        return ResponseInfo.ok(statisticResps);
    }

    @Operation(summary = "查询月趋势")
    @PostMapping("/trend/month")
    public ResponseInfo<List<StatisticResp>> selectTrendByMonth(@RequestBody @Valid TrendReq req) {
        List<StatisticResp> statisticResps = examineInfoService.selectTrendByMonth(req);
        return ResponseInfo.ok(statisticResps);
    }

    @Operation(summary = "查询年趋势")
    @PostMapping("/trend/year")
    public ResponseInfo<List<StatisticResp>> selectTrendByYear(@RequestBody @Valid TrendReq req) {
        List<StatisticResp> statisticResps = examineInfoService.selectTrendByYear(req);
        return ResponseInfo.ok(statisticResps);
    }

    /**
     * 查询天的考核
     */
    @Operation(summary = "查询天的考核")
    @PostMapping("/examine/day")
    public ResponseInfo<List<ExamineResp>> selectExamineByDay(@RequestBody @Valid ExamineReq req) {
        List<ExamineResp> resps = examineInfoService.selectExamineByDay(req);
        return ResponseInfo.ok(resps);
    }

    @Operation(summary = "查询月的考核")
    @PostMapping("/examine/month")
    public ResponseInfo<List<ExamineResp>> selectExamineByMonth(@RequestBody @Valid ExamineReq req) {
        List<ExamineResp> resps = examineInfoService.selectExamineByMonth(req);
        return ResponseInfo.ok(resps);
    }

    @Operation(summary = "查询年的考核")
    @PostMapping("/examine/year")
    public ResponseInfo<List<ExamineResp>> selectExamineByYear(@RequestBody @Valid ExamineReq req) {
        List<ExamineResp> resps = examineInfoService.selectExamineByYear(req);
        return ResponseInfo.ok(resps);
    }

}
