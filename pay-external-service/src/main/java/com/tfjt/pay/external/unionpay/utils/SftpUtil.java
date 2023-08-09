package com.tfjt.pay.external.unionpay.utils;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import com.alibaba.fastjson.JSON;
import com.google.common.base.Throwables;
import com.jcraft.jsch.*;
import com.tfjt.pay.external.unionpay.config.UnionPayCertificateConfig;
import com.tfjt.pay.external.unionpay.constants.SftpConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.input.BOMInputStream;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.*;

/**
 * @Author: suxiaochang
 * @date: 2022/11/5
 */
@Slf4j
@Component
public class SftpUtil {

    @Value("${sftp.tfJtNo}")
    public String tfJtNo;
    @Value("${sftp.userName}")
    private String userName;
    @Value("${sftp.password}")
    private String password;
    @Value("${sftp.host}")
    private String host;
    @Value("${sftp.uploadRoot}")
    public String uploadRoot;
    public static final String SFTP_LOCAL_UPLOAD_PATH = "/data/tf-pay/sftp/upload/";
    public static final String SFTP_LOCAL_DOWNLOAD_PATH = "/data/tf-pay/sftp/download/";
    /**
     * 签名验签算法，值为{@value}
     */
    public final static String ALGORITHM_SHA256WITHRSA = "SHA256withRSA";


    /**
     * 回盘数据读取
     *
     * @param noneCallback  从未回盘数据文件地址集合
     * @param firstCallback first回盘数据文件地址集合
     * @return
     */
    public Map<String, String> findCallbackStream(Set<String> noneCallback, Set<String> firstCallback) throws Exception {
        log.info("回盘数据读取,noneCallback：" + JSON.toJSONString(noneCallback));
        log.info("回盘数据读取,firstCallback：" + JSON.toJSONString(firstCallback));
        Map<String, String> hasCallbackMap = downloadFromRemote(noneCallback, firstCallback);
        if (MapUtil.isEmpty(hasCallbackMap)) {
            return hasCallbackMap;
        }
        //验签
        log.info("验签：" + JSON.toJSONString(hasCallbackMap));
        verifySign(hasCallbackMap);
        return hasCallbackMap;
    }


    public Map<String, String> findCallbackStreamSingle(Set<String> noneCallback) throws Exception {
        log.info("回盘数据读取,noneCallback：" + JSON.toJSONString(noneCallback));
        Map<String, String> hasCallbackMap = downloadFromRemoteSingle(noneCallback);
        if (MapUtil.isEmpty(hasCallbackMap)) {
            return hasCallbackMap;
        }
        //验签
        log.info("验签：" + JSON.toJSONString(hasCallbackMap));
        verifySign(hasCallbackMap);
        return hasCallbackMap;
    }

    /**
     * 回盘数据验签
     *
     * @param hasCallbackMap 回盘map
     * @throws Exception
     */
    private void verifySign(Map<String, String> hasCallbackMap) throws Exception {
        List<String> verFailList = new ArrayList<>();
        for (String key : hasCallbackMap.keySet()) {
            String downPath = hasCallbackMap.get(key);
            File file = new File(downPath);
            String retMd5 = MD5Util.getFileMD5String(file);
            //获取签名文件中的签名
            File verRetChkFile = new File(downPath + SftpConstant.SFTP_SIGN_SUFFIX);
            Boolean verResult = null;
            try (BufferedReader verBr = new BufferedReader(new InputStreamReader(new BOMInputStream(Files.newInputStream(verRetChkFile.toPath()))))) {
                String verSignature = verBr.readLine();
                verResult = UnionPaySignUtil.verify(retMd5.getBytes(StandardCharsets.UTF_8), UnionPaySignUtil.hexString2ByteArr(verSignature)
                        , UnionPayCertificateConfig.countPubKey, ALGORITHM_SHA256WITHRSA);
                verRetChkFile.delete();
                if (verResult != null && !verResult) {
                    //验签失败数据
                    verFailList.add(key);
                    file.delete();
                }
            }

        }
        //验签失败数据去除
        if (CollectionUtil.isNotEmpty(verFailList)) {
            verFailList.forEach(hasCallbackMap::remove);
        }

    }

    /**
     * 回盘数据读取
     *
     * @param noneCallback
     * @param firstCallback
     * @return
     */
    public Map<String, String> downloadFromRemote(Set<String> noneCallback, Set<String> firstCallback) {
        Map<String, String> hasCallbackMap = new HashMap<>();
        ChannelSftp sftp = null;
        JSch jsch = new JSch();
        try {
            Session session = jsch.getSession(userName, host, 22);
            if (null == session) {
                log.error("sftp主机无法连接");
                throw new JSchException("sftp主机无法连接");
            }
            session.setPassword(password);
            // skip key checking
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();
            Channel channel = session.openChannel("sftp");
            channel.connect();
            sftp = (ChannelSftp) channel;
            //未回盘： 先查询final，final不存在再查询first
            findFinalCallbackStream(sftp, noneCallback, hasCallbackMap);
            findFirstCallbackStream(sftp, noneCallback, hasCallbackMap);
            //first回盘只需查询final
            findFinalCallbackStream(sftp, firstCallback, hasCallbackMap);

        } catch (Exception e) {
            log.error("sftp 主机异常 ", e);
            Throwables.propagate(e);
        } finally {
            if (sftp != null && sftp.isConnected()) {
                sftp.disconnect();
            }
        }
        return hasCallbackMap;
    }


    public Map<String, String> downloadFromRemoteSingle(Set<String> noneCallback) {
        Map<String, String> hasCallbackMap = new HashMap<>();
        ChannelSftp sftp = null;
        JSch jsch = new JSch();
        try {
            Session session = jsch.getSession(userName, host, 22);
            if (null == session) {
                log.error("sftp主机无法连接");
                throw new JSchException("sftp主机无法连接");
            }
            session.setPassword(password);
            // skip key checking
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();
            Channel channel = session.openChannel("sftp");
            channel.connect();
            sftp = (ChannelSftp) channel;
            for (String path : noneCallback) {
                String[] arr = path.split("\\|");
                String dir = arr[0];
                String fileName = arr[1];
                String uploadPath = uploadRoot + dir + "/" + fileName + ".ret";
                boolean b = isFileAndSignExist(sftp, uploadPath);
                if (b) {
                    sftp.get(uploadPath, SFTP_LOCAL_DOWNLOAD_PATH);
                    sftp.get(uploadPath + ".chk", SFTP_LOCAL_DOWNLOAD_PATH);
                    hasCallbackMap.put(fileName, SFTP_LOCAL_DOWNLOAD_PATH + fileName + ".ret");
                }
            }

        } catch (Exception e) {
            log.error("sftp 主机异常 ", e);
            Throwables.propagate(e);
        } finally {
            if (sftp != null && sftp.isConnected()) {
                sftp.disconnect();
            }
        }
        return hasCallbackMap;
    }


    /**
     * 首次回盘读取
     *
     * @param sftp
     * @param noneCallback
     * @param hasCallbackMap
     * @throws SftpException
     */
    private void findFirstCallbackStream(ChannelSftp sftp, Set<String> noneCallback, Map<String, String> hasCallbackMap) throws SftpException {
        if (CollectionUtil.isEmpty(noneCallback)) {
            return;
        }
        for (String path : noneCallback) {
            //存在final回盘就不再需要first回盘
            if (!hasCallbackMap.containsKey(path)) {
                String[] arr = path.split("\\|");
                String dir = arr[0];
                String fileName = arr[1];
                String uploadPath = uploadRoot + dir + "/first_" + fileName + ".ret";
                if (isFileAndSignExist(sftp, uploadPath)) {
                    sftp.get(uploadPath, SFTP_LOCAL_DOWNLOAD_PATH);
                    sftp.get(uploadPath + ".chk", SFTP_LOCAL_DOWNLOAD_PATH);
                    hasCallbackMap.put(fileName, SFTP_LOCAL_DOWNLOAD_PATH + "first_" + fileName + ".ret");
                }
            }
        }
    }

    /**
     * final回盘读取
     *
     * @param sftp
     * @param noneCallback
     * @param hasCallbackMap
     * @throws SftpException
     */
    private void findFinalCallbackStream(ChannelSftp sftp, Set<String> noneCallback, Map<String, String> hasCallbackMap) throws SftpException {
        if (CollectionUtil.isEmpty(noneCallback)) {
            return;
        }
        for (String path : noneCallback) {
            String[] arr = path.split("\\|");
            String dir = arr[0];
            String fileName = arr[1];
            String uploadPath = uploadRoot + dir + "/final_" + fileName + ".ret";
            boolean b = isFileAndSignExist(sftp, uploadPath);
            if (b) {
                sftp.get(uploadPath, SFTP_LOCAL_DOWNLOAD_PATH);
                sftp.get(uploadPath + ".chk", SFTP_LOCAL_DOWNLOAD_PATH);
                hasCallbackMap.put(fileName, SFTP_LOCAL_DOWNLOAD_PATH + "final_" + fileName + ".ret");
            }
        }
    }


    /**
     * 上传文件
     *
     * @param fileName  上传文件名称
     * @param dirStr    文件夹名称
     * @param collect   上传数据
     * @param firstLine 上传文件首行数据
     */
    public void uploadToRemote(String fileName, String dirStr, Set<String> collect, String firstLine) throws IOException {
        File fileRoot = new File(SFTP_LOCAL_UPLOAD_PATH);
        if (!fileRoot.exists()) {
            fileRoot.mkdir();
        }
        File file = new File(SFTP_LOCAL_UPLOAD_PATH + fileName);
        if (file.exists()) {
            log.info("--------------------------文件名称已经存在: {}", file.getName());
            throw new RuntimeException("文件名称已经存在！");
        }

        writeInsertToTxt(file, collect, firstLine);
        String sign = getSign(file);
        File chkFile = new File(SFTP_LOCAL_UPLOAD_PATH + fileName + ".chk");
        writeInsertToTxt(chkFile, null, sign);
        upload(file, dirStr);
        upload(chkFile, dirStr);
        file.delete();
        chkFile.delete();
    }

    /**
     * 数据写入文件
     *
     * @param file      写入文件
     * @param list      数据正文
     * @param firstLine 数据标题
     * @throws IOException
     */
    private void writeInsertToTxt(File file, Set<String> list, String firstLine) throws IOException {
        file.createNewFile();
        try (BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(Files.newOutputStream(file.toPath())))) {
            if (StringUtil.isNotBlank(firstLine)) {
                bw.write(firstLine);
                bw.newLine();
            }
            if (CollectionUtil.isEmpty(list)) {
                bw.close();
                return;
            }
            for (String s : list) {
                bw.write(s);
                bw.newLine();
            }
            bw.flush();
        }
    }


    /**
     * 获取
     *
     * @param file
     * @return
     */
    public String getSign(File file) throws IOException {
        String md5 = MD5Util.getFileMD5String(file);
        byte[] signB = UnionPaySignUtil.sign(md5.getBytes(), UnionPayCertificateConfig.batchPrivateKey, ALGORITHM_SHA256WITHRSA);
        return byteArr2HexString(signB);
    }

    /**
     * 字节数组转换为十六进制字符串
     *
     * @param byteArr 字节数组
     * @return 十六进制字符串
     */
    public static String byteArr2HexString(byte[] byteArr) {
        if (byteArr == null) {
            return "null";
        }
        StringBuilder sb = new StringBuilder();

        for (byte b : byteArr) {
            if ((b & 0xFF) < 16) {
                sb.append("0");
            }
            sb.append(Integer.toString(b & 0xFF, 16));
        }
        return sb.toString();
    }


    private void upload(File file, String dirName) {
        ChannelSftp sftp = null;
        JSch jsch = new JSch();
        try {
            Session session = jsch.getSession(userName, host, 22);
            if (null == session) {
                log.error("sftp主机无法连接");
                throw new JSchException("sftp主机无法连接");
            }
            session.setPassword(password);
            // skip key checking
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();
            Channel channel = session.openChannel("sftp");
            channel.connect();
            sftp = (ChannelSftp) channel;
            //如果不存在则创建远程文件夹
            createRemoteIfNotExist(sftp, dirName);
            try (FileInputStream input = new FileInputStream(file)) {
                sftp.cd(uploadRoot + dirName);
                sftp.put(file.getPath(), file.getName());
            } catch (SftpException e) {
                throw new RuntimeException("上传文件失败");
            }
        } catch (Exception e) {
            log.error("sftp 主机异常 ", e);
            Throwables.propagate(e);
        } finally {
            if (sftp != null && sftp.isConnected()) {
                sftp.disconnect();
            }
        }
    }

    private void createRemoteIfNotExist(ChannelSftp sftp, String dirName) throws SftpException {
        try {
            sftp.stat(uploadRoot + dirName);
        } catch (SftpException e) {
            sftp.mkdir(uploadRoot + dirName);
        }
    }

    /**
     * 读取数据
     *
     * @param path 下载地址
     */
    public Boolean download(String path, String downloadPath) {
        Boolean hasReturn = false;
        ChannelSftp sftp = null;
        JSch jsch = new JSch();
        try {
            Session session = jsch.getSession(userName, host, 22);
            if (null == session) {
                log.error("sftp主机无法连接");
                throw new JSchException("sftp主机无法连接");
            }
            session.setPassword(password);
            // skip key checking
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();
            Channel channel = session.openChannel("sftp");
            channel.connect();
            sftp = (ChannelSftp) channel;
            boolean b = isFileExist(sftp, path);
            if (b) {
                sftp.get(path, StringUtil.isBlank(downloadPath) ? SFTP_LOCAL_DOWNLOAD_PATH : downloadPath);
                hasReturn = true;
            }

        } catch (Exception e) {
            log.error("sftp 主机异常 ", e);
            Throwables.propagate(e);
        } finally {
            if (sftp != null && sftp.isConnected()) {
                sftp.disconnect();
            }
        }
        return hasReturn;
    }

    /**
     * 回盘文件及验签文件是否存在
     *
     * @param sftp
     * @param uploadPath
     * @return
     */
    private boolean isFileAndSignExist(ChannelSftp sftp, String uploadPath) {
        try {
            sftp.stat(uploadPath);
            sftp.stat(uploadPath + ".chk");
        } catch (SftpException e) {
            return false;
        }
        return true;
    }

    /**
     * 读取文件是否存在
     *
     * @param sftp
     * @param path 文件路径
     * @return
     */
    private boolean isFileExist(ChannelSftp sftp, String path) {
        try {
            sftp.stat(path);
        } catch (SftpException e) {
            return false;
        }
        return true;
    }




    public List<String> downloadJymx(List<String> fileNameList) {
        List<String> resultFilePath = new ArrayList<>();

        ChannelSftp sftp = null;
        JSch jsch = new JSch();
        try {
            Session session = jsch.getSession(userName, host, 22);
            if (null == session) {
                log.error("sftp主机无法连接");
                throw new JSchException("sftp主机无法连接");
            }
            session.setPassword(password);
            // skip key checking
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();
            Channel channel = session.openChannel("sftp");
            channel.connect();
            sftp = (ChannelSftp) channel;
            for (String fileName : fileNameList) {
                String uploadPath = uploadRoot + "JYMX/HBTFYS_settleJournal_" + fileName + ".txt";
                boolean b = isFileExist(sftp, uploadPath);
                if (b) {
                    sftp.get(uploadPath, SFTP_LOCAL_DOWNLOAD_PATH);
                    resultFilePath.add(SFTP_LOCAL_DOWNLOAD_PATH+"HBTFYS_settleJournal_" + fileName + ".txt");
                }
            }
            return resultFilePath;
        } catch (Exception e) {
            log.error("sftp 主机异常 ", e);
            Throwables.propagate(e);
        } finally {
            if (sftp != null && sftp.isConnected()) {
                sftp.disconnect();
            }
            return resultFilePath;
        }
    }
}
