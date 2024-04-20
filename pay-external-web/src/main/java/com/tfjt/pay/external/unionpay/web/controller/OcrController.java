package com.tfjt.pay.external.unionpay.web.controller;

import com.aliyun.ocr_api20210707.Client;
import com.aliyun.ocr_api20210707.models.RecognizeBusinessLicenseRequest;
import com.aliyun.ocr_api20210707.models.RecognizeBusinessLicenseResponse;
import com.aliyun.tea.TeaException;
import com.aliyun.teautil.Common;
import com.aliyun.teautil.models.RuntimeOptions;
import com.tfjt.pay.external.unionpay.service.OcrService;
import com.tfjt.pay.external.unionpay.utils.OCRApiUtil;
import com.tfjt.tfcommon.dto.response.Result;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

/**
 * @description: OcrController <br>
 * @date: 2023/5/22 17:59 <br>
 * @author: young <br>
 * @version: 1.0
 */
@RestController
@RequestMapping("ocr")
@Slf4j
@RefreshScope
public class OcrController {

    @Resource
    OcrService ocrService;

    @Value("${aliocr.accessKeyId}")
    private String accessKeyId;

    @Value("${aliocr.accessKeySecret}")
    private String accessKeySecret;

    @PostMapping
    public Result upload(@RequestBody Map<String,String> params) {
        String url = params.get("url");
        String type = params.get("type");
        String side = params.get("side");
        if (type == null) {
            return Result.failed("ocr类型不能为空");
        }
        try {
            String result = ocrService.ocrInfo(url, type, side);
            if (result != null) {
                return Result.ok(result);
            } else {
                return Result.failed("ocr异常");
            }
        } catch (Exception ex) {
            log.error("", ex);
            return Result.failed(ex.getMessage());
        }

    }

    @ApiOperation("OCR识别营业执照 - 崔冬贤")
    @GetMapping("/businessLicense")
    public Result<?> businessLicense(@RequestParam("url") String url) throws Exception {
        // 工程代码泄露可能会导致AccessKey泄露，并威胁账号下所有资源的安全性。以下代码示例仅供参考，建议使用更安全的 STS 方式，更多鉴权访问方式请参见：https://help.aliyun.com/document_detail/378657.html
        Client client = OCRApiUtil.createClient(accessKeyId, accessKeySecret);
        RecognizeBusinessLicenseRequest recognizeBusinessLicenseRequest = new RecognizeBusinessLicenseRequest();
        recognizeBusinessLicenseRequest.setUrl(url);
        RuntimeOptions runtime = new RuntimeOptions();
        try {
            // 复制代码运行请自行打印 API 的返回值
            RecognizeBusinessLicenseResponse recognizeBusinessLicenseResponse = client.recognizeBusinessLicenseWithOptions(recognizeBusinessLicenseRequest, runtime);
            return new Result().setCode(0).setData(recognizeBusinessLicenseResponse.getBody().getData());
        } catch (TeaException error) {
            error.printStackTrace();
            // 如有需要，请打印 error
            Common.assertAsString(error.message);
            return Result.failed("无法识别营业执照");
        } catch (Exception _error) {
            _error.printStackTrace();
            TeaException error = new TeaException(_error.getMessage(), _error);
            // 如有需要，请打印 error
            Common.assertAsString(error.message);
            return Result.failed("识别营业执照异常");
        }
    }


    @GetMapping("/test")
    public Result<String> test(){
        String msg = "accessKeyId:"+accessKeyId+";accessKeySecret:"+accessKeySecret;
        return Result.ok(msg);
    }

}
