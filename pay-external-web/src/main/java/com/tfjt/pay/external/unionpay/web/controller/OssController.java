package com.tfjt.pay.external.unionpay.web.controller;

import cn.xuyanwu.spring.file.storage.FileInfo;
import cn.xuyanwu.spring.file.storage.FileStorageService;
import com.tfjt.pay.external.unionpay.service.UnionPayLoansApiService;
import com.tfjt.pay.external.unionpay.utils.FileUtil;
import com.tfjt.tfcommon.dto.response.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * @description: OssController <br>
 * @date: 2023/5/20 14:22 <br>
 * @author: young <br>
 * @version: 1.0
 */
@RestController
@RequestMapping("oss")
@Slf4j
public class OssController {

    @Resource
    private FileStorageService fileStorageService;

    @Resource
    private UnionPayLoansApiService yinLianLoansApiService;

    @PostMapping("/upload")
    public Result upload(@RequestParam(value = "file") MultipartFile multiFile, HttpServletRequest request) {
        Map<String, String> resultMap = new HashMap<>();
        FileInfo fileInfo = fileStorageService.of(multiFile)
                .upload();

        String url = fileInfo.getUrl();
        resultMap.put("url", url);
        String type = request.getParameter("type");
        if ("idcard".equals(type)) {
            String side = request.getParameter("side");
            String mediaId = yinLianLoansApiService.upload(FileUtil.transferToFile(multiFile));
            if ("face".equals(side)) {
                resultMap.put("frontIdCardUrlMediaId", mediaId);
            }
            if ("back".equals(side)) {
                resultMap.put("backIdCardUrlMediaId", mediaId);
            }
        }

        if("license".equals(type)){
            String mediaId = yinLianLoansApiService.upload(FileUtil.transferToFile(multiFile));
            resultMap.put("businessImgMediaId", mediaId);
        }

        if (url == null) {
            return Result.failed("上传失败");
        }
        return Result.ok(resultMap);
    }
}
