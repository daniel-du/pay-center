package com.tfjt.pay.external.unionpay.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.ipaynow.jiaxin.*;
import com.ipaynow.jiaxin.domain.*;
import com.ipaynow.jiaxin.request.ContractH5Request;
import com.ipaynow.jiaxin.request.PictureUploadRequest;
import com.ipaynow.jiaxin.request.PresignRequest;
import com.ipaynow.jiaxin.request.QueryPresignRequest;
import com.ipaynow.jiaxin.response.ContractH5Response;
import com.ipaynow.jiaxin.response.PictureUploadResponse;
import com.ipaynow.jiaxin.response.PresignResponse;
import com.ipaynow.jiaxin.response.QueryPresignResponse;
import com.ipaynow.jiaxin.util.Base64;
import com.pingan.openbank.api.sdk.client.ApiClient;
import com.tfjt.pay.external.unionpay.dto.IncomingSubmitMessageDTO;
import com.tfjt.tfcommon.core.exception.TfException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.net.ssl.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import static com.ipaynow.jiaxin.TangConstants.DEV_HOST;
import static com.ipaynow.jiaxin.TangConstants.SECRET_KEY_ERROR;

/**
 * @author Du Penglun
 * @version 1.0
 * @date 2024/3/19 15:44
 * @description
 */
@Slf4j
@Component
public class TtqfApiUtil {

    @Value("${ttqf.mchPrivateKey}")
    private String mchPrivateKeyValue;

    @Value("${ttqf.platformPublicKey}")
    private String platformPublicKeyValue;

    @Value("${ttqf.mchId}")
    private String mchIdValue;

    @Value("${ttqf.host}")
    private String host;

    private static Decryptor decryptor;

//    private static Signer signer;

    public static  String mchPrivateKey;// = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDXsdBOhA3fuo8+ilcZsbxl3/FI0ISP0DraWD5+DDtZHphJum+LkDjluqekDPVMZkz9B/a9lZT3Uhk2wSQ2lBS+IVwJEQB6h3ejcpKcaYG/F0s3lx8ArxEbJD8mGy1sId7yjg13fBgIzBbAJ4AvQw4rzbNKRdNox8pzRA6swKIy8ovT3df7B5utoNHKGOZTrwNQcrjGGQuIlvJxktpxWQfHdhy5m+MDNLYJAytLTNoiclp2Erdej8BQPbfcpqqr6CCGOvBm+zZ7VN4wbKQHaONwuPOE+iaYuJbR0O74bTmPGGSKwdNf86VJp5DWxPmwlYxgBLsm/BA71kxxNgcQ9L4NAgMBAAECggEAJb/7IyK9guCV3W483ERdIr9U7VeBO+JHypsTJOBjKq/OG2lDyHtPWfWXDYEvFVX6N3bDWl7pmXeG7ixTeDHmh3gzMWsGWGuJJtQQkMtuZ9Fd2rbaODZrmgJ4AuVSx9ANwwnVlYnkcvW1LaA8MQldW5E+xcr+uyHsbswEfXuhZ6T1RFZnDBBYQbAAYTMAlS5cUqlVFF71GYMS3jpR4Dp7ddMPXcpOFZ/6t65vGrQkYgGHUHBIKzLh1BBioG/RaimmZPeRDUrED0ENuZkP6fPuAunPcdrIAHOueZde4AgTEiQCDYZjtT9KCoBpKQxwDAQpJVVjbvCZUk+CULy5i2+2QQKBgQDyojIMue3BJEZeh5Cyu9gBGGlhUegssJ/CcIQG1HvI18OY4z2T0o7LVvgNqn7sJ4nJHfn5hrfkVk2a+SwEd22WCNtXc+cU3JNnHyenX3xwbLDY8oUS6SXmv4GqQXy4O8LxKoZAtBVbteDs00ybptA/1PH2mf1BMBBsVQEcaQlumQKBgQDjk7QzHfCOYhDij4v0vQwMxnwRSg03ZTJp9chXMaKkkhRV11ApbX0QsK23EZ82vK9P58BYKz+ewZmLQq6zX6ooUQxvo9mUm4/Czc25f1nbt9aXkQYcDN+2bSbcRkvPcAMWwxiu2J4/XBofI0L0oySTWcfkdbGIw+MOCZHB/Ki3lQKBgE5F0MuWN4L1RtY7+YwPWTGRRH2mxLWWDRPW5GCnr38tS+ZIeJb1eCdU2RTfia55WtPMndXHhg9dYA+8jlyqgmnCVbAyrbtnJQ5BWLN/9E74YVy7b/6XxChaY2QfWDK+5LaUN1csQS+izvrXd4FHIStWcPBS4QZkkQfBrGgT4n0RAoGAWW1cPU4BjmHY0r6nt5dmGcsRS9X2lZMNmXe8rw7RI6xHk2fVl0HwCFP1H2AsWV0R4utluP/dsyvOg1vDvLbV7ZvTsDcJtAzhFCK9OaxiiiWWjN6vMOyaKFyCDcMN3Ujd3d7yXxq+LJWrqhq4H9vP7ps0IoFhLyYSnhyCi2EWh3UCgYEAgEmImF2I6BnQnhKz/5tdzr95ZXGiHDeO7UpFJTJrOmJIyLMgCOy2jIK8TjdE3yzWXics46/ZLicumwsjTzyByoALhuUrfFnCvdYeD+POHFFDcKCkFROQsKosQKWkREc2J2fF18E9m0D46LpeqtuWWxI4aRjgfKjQrYQaxU5lcO8=";
    // 平台方公钥
    public static  String platformPublicKey;// = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAo/PmfNo3v8+VIT2oSUMXtZJGtGiBfqjVQAeKjppXkZ9jgzNjVbRAnWdTXKifyQQa5QhGeFCjJ9RS5WwLyZMBdwbhJJ2YkVDDXDc284CuXf5J3TDodkexfDwyPf3SvSVemRej5muhju1DOzLAyvx4GRKbDOEQqNiFxmn7HnQ2U6HsnQKa/LkB4ydogauI81q/taOcLyZsxz+uiIxYHQ2kujDBYncH3ncBeNQ7rWh7trWvCZ2sbtmUjbU2FC/cpp1oFBQbiQAuVA+4NBjobT/Ig7hGT9NiJ0elzOX5vWKvZelu6zD8z/frqXKSeyDWdjE9TB53pvCCuCiZSyOEwLJ0uwIDAQAB";
    public static  String mch_id;// = "000000100543062";

    static TangClient client;//new DefaultTangClient(DEV_HOST, mchPrivateKey, platformPublicKey, mch_id);

    @PostConstruct
    public void init() {
        mchPrivateKey = mchPrivateKeyValue;
        platformPublicKey = platformPublicKeyValue;
        mch_id = mchIdValue;
        client = new DefaultTangClient(host, mchPrivateKeyValue, platformPublicKeyValue, mchIdValue);
        decryptor = new DefaultDecryptor(mchPrivateKeyValue);
//        this.signer = new DefaultSigner(mchPrivateKey);
    }

    DateFormat format = new SimpleDateFormat("yyyyMMdd");

    DateFormat format2 = new SimpleDateFormat("yyyy-MM-dd");

    public static void main(String[] args) throws IOException {
        JSONObject json = new JSONObject();
        json.put("aes","eJp0qRu33liGJqHEPEX28FwVhQ4fgVv0GcHLPcbhOf9Vi128wzZf/7deGu393IE077S5kZfqbiBJ3C4mGRW2YjKR1PrjtkfnBKAf3WY/XpquBw6g66vsZ5CI3wZGfnQ/XbbkzQp6CMNEyFGU75LtTzSPg1k65e3YQQpx+996s+0FKqkvT0qizXxpFkdLN2KOq87Sp4zIrS3B14Okf+6TN0vZ2Ph6KOmjgAdJkSwNEqhkg+PRBmQgLvafP5j4pOFuGKB8nJIQJlsMcc1LjTaSXSLzjukV6qwvRYevsFCabRpeHBDX9rZgGMhce49YG8i+MNmU2qnJe2IzRMIl916jww==");
        json.put("reqData", "pc9VyUD1eybyN7vq7/jTgwMPxID47Ydpz6OiFVmh23YjSpLunIXOJZvnRtGAam+3x2h8paBvuzZuBerILx7jRm+lYzNi7+tTQ/Al2BqPE97KyJtSftp6nzUqrzuOSfTp3Rb/D8LtqDwY0gXr5WsbWaJfctEkZ11oSKmYDcEmBawzEFOnVsBo8xStAmkMC35cwZjIx+BZl/TxnRnNljujjIGLssKUqz0+PPElLg4r/DEG4/gbmq3gWM/D9wtMNjmIKe0gnZI7MxJ7WBOR5xz4+aI+OyZdFJnyAffMQ7Qep55kZNg232nXluT3zSOuUHuZ");
        TtqfApiUtil api = new TtqfApiUtil();
        api.decodeReq(json);
        // todo 3.1 用户预签约接口
//        testPresign();
        // todo 3.2 用户预签约结果查询接口
//		 testQueryPresign();
        // todo 3.3 合同签约唤起接口
//		 testContractH5();
        // todo 2.1 打款订单新增接口
        // testOrderAdd();
        // todo 2.3 订单取消接口
        // testOrderCancel();
        // todo 2.2 打款订单查询接口
        // testOrderQuery();
        // todo 2.5 确认打款接口
        // testConfirm();
        // todo 3.4 用户协议预览地址查询接口
        // testPreview();
        // todo 2.4 商户余额查询接口
//		 testBalance();
        // todo 2.7 电子回执单瞎子啊接口
        // testReceiptDownload();
        // todo 4 账单下载接口
        // testBillDownload();
        // todo 3.6 图片上传接口
//         testPictureUpload();
        // todo 2.6 打款结果回调
        // testOrderNotify();
    }

    public static PresignResultModel presign(PresignModel model) {
        log.info("TtqfApiUtil>>presign, reqModel:{}", JSONObject.toJSONString(model));
        PresignRequest request = new PresignRequest();
        request.setBizModel(model);
        request.setRequestId(String.valueOf(System.currentTimeMillis()));
        PresignResponse response = client.execute(request);
        if (!response.isSuccess()) {
            log.error("ttqfApi--presign, error:{}", JSONObject.toJSONString(response));
            throw new TfException(response.getBizMsg());
        }
        log.info("ttqfApi--presign, result:{}", JSONObject.toJSONString(response.getResultModel()));
        return response.getResultModel();
    }

    public static String pictureUpload(String url)  {
        PictureUploadRequest request = new PictureUploadRequest();
        //2 - 2020-10
        PictureUploadModel model = PictureUploadModel.builder().picture(Base64.encode(getFileStream(url))).build();
        request.setBizModel(model);
        request.setRequestId(System.currentTimeMillis() + "");
        log.info("ttqfApi--pictureUpload, request:{}", JSONObject.toJSONString(request));
        PictureUploadResponse response = client.execute(request);
        if (!response.isSuccess()) {
            log.error("ttqfApi--pictureUpload, error:{}", JSONObject.toJSONString(response));
            throw new TfException(response.getBizMsg());
        }
        log.info("ttqfApi--pictureUpload, result:{}", JSONObject.toJSONString(response));
        return response.getResultModel().getFileId();
    }

    public static String contractH5(String idCardNo, String returnUrl) {
        ContractH5Response response = client.execute(ContractH5Request.builder()
                .requestId(System.currentTimeMillis() + "").bizModel(ContractH5Model.builder()
                        .idCardNo(idCardNo).mchReturnUrl(returnUrl).build())
                .build());
        if (!response.isSuccess()) {
            log.error("ttqfApi--contractH5, error:{}", JSONObject.toJSONString(response));
            throw new TfException(response.getBizMsg());
        }
        log.info("ttqfApi--contractH5, result:{}", JSONObject.toJSONString(response.getResultModel()));
        return response.getResultModel().getSignUrl();
    }

    public static QueryPresignResultModel queryPresign(String idCardNo) {
        log.info("TtqfApiUtil>>queryPresign, idCardNo:{}", idCardNo);
        QueryPresignResponse response = client.execute(QueryPresignRequest.builder().requestId(System.currentTimeMillis() + "")
                .bizModel(new QueryPresignModel(idCardNo)).build());
        if (!response.isSuccess()) {
            log.error("ttqfApi--queryPresign, error:{}", JSONObject.toJSONString(response));
            throw new TfException(response.getBizMsg());
        }
        log.info("ttqfApi--queryPresign, result:{}", JSONObject.toJSONString(response.getResultModel()));
        return response.getResultModel();
    }

    public static byte[] getFileStream(String url){
        try {
            URL httpUrl = new URL(url);
            if("https".equalsIgnoreCase(httpUrl.getProtocol())){
                ignoreSsl();
            }
            HttpURLConnection conn = (HttpURLConnection)httpUrl.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5 * 1000);
            InputStream inStream = conn.getInputStream();//通过输入流获取图片数据
            byte[] btImg = readInputStream(inStream);//得到图片的二进制数据
            return btImg;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] readInputStream(InputStream inStream) throws Exception{
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while( (len=inStream.read(buffer)) != -1 ){
            outStream.write(buffer, 0, len);
        }
        inStream.close();
        return outStream.toByteArray();
    }

    private static void trustAllHttpsCertificates() throws Exception {
        TrustManager[] trustAllCerts = new TrustManager[1];
        TrustManager tm = new miTM();
        trustAllCerts[0] = tm;
        SSLContext sc = SSLContext.getInstance("SSL");
        sc.init(null, trustAllCerts, null);
        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
    }
    static class miTM implements TrustManager, X509TrustManager {
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }
        public boolean isServerTrusted(X509Certificate[] certs) {
            return true;
        }
        public boolean isClientTrusted(X509Certificate[] certs) {
            return true;
        }
        public void checkServerTrusted(X509Certificate[] certs, String authType)
                throws CertificateException {
            return;
        }
        public void checkClientTrusted(X509Certificate[] certs, String authType)
                throws CertificateException {
            return;
        }
    }
    /**
     * 忽略HTTPS请求的SSL证书，必须在openConnection之前调用
     * @throws Exception
     */
    public static void ignoreSsl() throws Exception{
        HostnameVerifier hv = new HostnameVerifier() {
            public boolean verify(String urlHostName, SSLSession session) {
                System.out.println("Warning: URL Host: " + urlHostName + " vs. " + session.getPeerHost());
                return true;
            }
        };
        trustAllHttpsCertificates();
        HttpsURLConnection.setDefaultHostnameVerifier(hv);
    }

    public static JSONObject decodeReq (JSONObject jsonObject) {
        log.error("天天企赋API => decodeReq:{}", jsonObject.toJSONString());
        Map responseMap = JSON.parseObject(jsonObject.toJSONString(), Map.class);
        System.out.println("responseMap:" + JSONObject.toJSONString(responseMap));
        String decAES = null;
        Decryptor aesDecryptor = null;
        try {
            decAES = decryptor.decrypt((String) responseMap.get("aes"));
            aesDecryptor = new AESDecryptor(decAES);
        } catch (Exception e) {
            log.error("天天企赋API => 秘钥错误，解密回调失败", e);
            throw e;
        }
        String bizResponse = null;
        try {
            bizResponse = aesDecryptor.decrypt((String) responseMap.get("reqData"));
        } catch (Exception e) {
            log.error("天天企赋API => 秘钥错误，解密回调业务报文失败", e);
            throw e;
        }
        return JSONObject.parseObject(bizResponse);
    }

}
