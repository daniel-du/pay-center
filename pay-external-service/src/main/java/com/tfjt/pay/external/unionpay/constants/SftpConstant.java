package com.tfjt.pay.external.unionpay.constants;

/**
 * @Author: suxiaochang
 * @date: 2022/11/7
 */
public class SftpConstant {

    /**
     * sftp上传文件，代付标识
     */
    public static final String SFTP_DF = "08_";
    /**
     * sftp上传文件，补贴标识
     */
    public static final String SFTP_BT = "09_";

    /**
     * sftp上传文件，划付标识
     */
    public static final String SFTP_HF = "02_";

    /**
     * sftp上传文件，划付标识
     */
    public static final String SFTP_FZ = "04_";

    /**
     * sftp上传文件后缀
     */
    public static final String SFTP_FILE_SUFFIX = ".txt";

    /**
     * sftp公钥验签文件后缀
     */
    public static final String SFTP_SIGN_SUFFIX = ".chk";



    /**
     * sftp 首次回盘文件前缀
     */
    public static final String SFTP_FIRST_CALLBACK_PREFIX = "first_";
    /**
     * sftp 二次回盘文件前缀
     */
    public static final String SFTP_FINAL_CALLBACK_PREFIX = "final_";
    /**
     * sftp 回盘状态：未回盘
     */
    public static final String SFTP_CALLBACK_STATE_NONE = "0";
    /**
     * sftp 回盘状态：首次回盘
     */
    public static final String SFTP_CALLBACK_STATE_FIRST = "1";
    /**
     * sftp 回盘状态：二次回盘
     */
    public static final String SFTP_CALLBACK_STATE_FINAL = "2";
    /**
     * sftp 回盘状态：回盘明细全部失败
     */
    public static final String SFTP_CALLBACK_STATE_FAIL = "3";




    /**
     * sftp 回调状态：初始状态
     */
    public static final String URL_CALLBACK_STATE_NONE = "0";
    /**
     * sftp 回调状态：待回调
     */
    public static final String URL_CALLBACK_STATE_WAIT = "1";
    /**
     * sftp 回调状态：回调成功
     */
    public static final String URL_CALLBACK_STATE_SUCCESS = "2";
    /**
     * sftp 回调状态：回调成功
     */
    public static final String URL_CALLBACK_STATE_FAIL = "3";



    /**
     * sftp 交易明细
     */
    public static final String SFTP_JYMX_PATH = "JYMX/";
    /**
     * sftp 交易明细文件名
     */
    public static final String SFTP_JYMX_FILE = "_settleJournal_";
    /**
     * sftp 退票明细
     */
    public static final String SFTP_TPMX_PATH = "TPMX/";
    /**
     * sftp 退票文件
     */
    public static final String SFTP_TPWJ_PATH = "TPWJ/";
    /**
     * sftp 退票明细文件名
     */
    public static final String SFTP_TPMX_FILE = "_refundInfo_";
    /**
     * sftp 退票文件名
     */
    public static final String SFTP_TPWJ_FILE = "_refundfile_";


    /**
     * sftp 银联回盘状态
     * 失败
     */
    public static final String YL_CALLBACK_STATE_FAIL = "0";
    /**
     * sftp 银联回盘状态
     * 成功
     */
    public static final String YL_CALLBACK_STATE_SUCCESS = "1";
    /**
     * sftp 银联回盘状态
     * 部分成功
     */
    public static final String YL_CALLBACK_STATE_PART_SUCCESS = "2";
    /**
     * sftp 银联回盘状态
     * 处理中
     */
    public static final String YL_CALLBACK_STATE_DOING = "3";


    /**
     * 回调成功返回值
     */
    public static final String PAY_CALLBACK_SUCCESS = "SUCCESS";




}
