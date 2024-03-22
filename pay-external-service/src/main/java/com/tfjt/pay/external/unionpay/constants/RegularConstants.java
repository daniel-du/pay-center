package com.tfjt.pay.external.unionpay.constants;

/**
 * @author tony
 * 正则表表达式常量
 */
public class RegularConstants {

    //校验商户简称
    public static final String  CHARACTER_CHECK="^[\\u4E00-\\u9FA5A-Za-z0-9&()]+$";
    //校验身份证号
    public static final String  IDCARD_CHECK= "^[1-9][0-9]{5}(18|19|20)[0-9]{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)[0-9]{3}([0-9]|(X|x))|^[1-9][0-9]{5}[0-9]{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)[0-9]{2}[0-9]";
    //邮箱
    public static final String  IDCARD_EMAIL="^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$";
    //支持的特殊字符
    public static final String SPECIAL_CHAR_PATTERN="^.*[/^/$/.//,;:'!@#%&/*/|/?/+/(/)/[/]/{/}]+.*$";
    //不支持的特殊字符
    public static final String NO_SPECIAL_CHAR_PATTERN="[^/^/$/.//,;:'!@#%&/*/|/?/+/(/)/[/]/{/}\\\\]+$";
    //营业执照号码校验（15位、18位数字或字母大写）
    public static final String LICENSE_PATTERN="(^(?:(?![IOZSV])[\\dA-Z]){2}\\d{6}(?:(?![IOZSV])[\\dA-Z]){10}$)|(^\\d{15}$)";

    /**
     * 手机号正则表达式
     */
    public static final String MOBILE = "^[1][2,3,4,5,6,7,8,9][0-9]{9}$";

    /**
     * 18位身份证号正则表达式
     */
    public static final String ID_CARD_NEW = "^[1-9][0-9]{5}(18|19|20)[0-9]{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)[0-9]{3}([0-9]|(X))";

    /**
     * 15位身份证号正则表达式
     */
    public static final String ID_CARD_OLD = "^[1-9]\\d{5}\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{2}[0-9Xx]$";

    /**
     * 邮箱正则表达式
     */
    public static final String EMAIL = "^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?(?:\\.[a-zA-Z0-9](?:[a-zA-Z0-9-]{0,61}[a-zA-Z0-9])?)*$";

    /**
     * 统一社会信用码正则表达式
     */
    public static final String SOCIAL_CREDIT_CODE = "[0-9A-Z]{18}";

}
