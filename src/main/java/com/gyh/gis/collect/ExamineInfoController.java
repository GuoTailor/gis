package com.gyh.gis.collect;

import com.alibaba.excel.EasyExcel;
import com.gyh.gis.dto.ResponseInfo;
import com.gyh.gis.dto.req.ExamineReq;
import com.gyh.gis.dto.req.SummarizeReq;
import com.gyh.gis.dto.req.TrendReq;
import com.gyh.gis.dto.resp.ExamineResp;
import com.gyh.gis.dto.resp.StatisticResp;
import com.gyh.gis.dto.resp.SummarizeResp;
import com.gyh.gis.enums.PeriodEnum;
import com.gyh.gis.exception.BusinessException;
import com.gyh.gis.service.ExamineInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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
     * 查询综合概览
     */
    @Operation(summary = "查询综合概览")
    @PostMapping("/summarize")
    public ResponseInfo<SummarizeResp> selectSummarize(@RequestBody @Valid SummarizeReq req) {
        return ResponseInfo.ok(examineInfoService.selectSummarize(req));
    }

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

    @Operation(summary = "导出统计")
    @PostMapping("/output/statistic/{per}")
    public void outputStatistic(HttpServletResponse response, @PathVariable PeriodEnum per, @RequestBody @Valid ExamineReq req) throws IOException {
        List<StatisticResp> statisticResps = switch (per) {
            case HOUR -> examineInfoService.selectStatisticByHour(req);
            case DAY -> examineInfoService.selectStatisticByDay(req);
            case MONTH -> examineInfoService.selectStatisticByMonth(req);
            case YEAR -> examineInfoService.selectStatisticByYear(req);
        };
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
        String fileName = URLEncoder.encode(req.getTime().toString(), StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        EasyExcel.write(response.getOutputStream(), StatisticResp.class).sheet("sheet1").doWrite(statisticResps);
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

    @Operation(summary = "导出考核")
    @PostMapping("/output/examine/{per}")
    public void outputExamine(HttpServletResponse response, @PathVariable PeriodEnum per, @RequestBody @Valid ExamineReq req) throws IOException {
        List<ExamineResp> resps = switch (per) {
            case HOUR -> throw new BusinessException("不支持小时考核导出");
            case DAY -> examineInfoService.selectExamineByDay(req);
            case MONTH -> examineInfoService.selectExamineByMonth(req);
            case YEAR -> examineInfoService.selectExamineByYear(req);
        };
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("utf-8");
        // 这里URLEncoder.encode可以防止中文乱码 当然和easyexcel没有关系
        String fileName = URLEncoder.encode(req.getTime().toString(), StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
        EasyExcel.write(response.getOutputStream(), ExamineResp.class).sheet("sheet1").doWrite(resps);
    }
}
