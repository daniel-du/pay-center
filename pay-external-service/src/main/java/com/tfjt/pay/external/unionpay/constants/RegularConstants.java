package com.tfjt.pay.external.unionpay.constants;

/**
 * @author tony
 * 正则表表达式常量
 */
public class RegularConstants {

    public static final String  CHARACTER_CHECK="^[\\u4E00-\\u9FA5A-Za-z0-9&()]+$"; //校验商户简称

    public static final String  IDCARD_CHECK= "^[1-9][0-9]{5}(18|19|20)[0-9]{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)[0-9]{3}([0-9]|(X|x))|^[1-9][0-9]{5}[0-9]{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)[0-9]{2}[0-9]"; //校验身份证号

    public static final String  IDCARD_EMAIL="^\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*$"; //邮箱

    public static final String SPECIAL_CHAR_PATTERN="^.*[/^/$/.//,;:'!@#%&/*/|/?/+/(/)/[/]/{/}]+.*$";//支持的特殊字符

    public static final String NO_SPECIAL_CHAR_PATTERN="[^/^/$/.//,;:'!@#%&/*/|/?/+/(/)/[/]/{/}\\\\]+$";//不支持的特殊字符

    public static final String LICENSE_PATTERN="(^(?:(?![IOZSV])[\\dA-Z]){2}\\d{6}(?:(?![IOZSV])[\\dA-Z]){10}$)|(^\\d{15}$)"; //营业执照号码校验（15位、18位数字或字母大写）

    /**
     * 手机号正则表达式
     */
    public static final String MOBILE = "/1\\d{10}/";

    /**
     * 18位身份证号正则表达式
     */
    public static final String ID_CARD_NEW = "/^[1-9]\\d{5}(19|20)\\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\\d{3}[Xx\\d]$/";

    /**
     * 15位身份证号正则表达式
     */
    public static final String ID_CARD_OLD = "/^[1-9]\\d{7}((0\\d)|(1[0-2]))(([0|1|2]\\d)|3[0-1])\\d{3}$/";

    /**
     * 邮箱正则表达式
     */
    public static final String EMAIL = "/^([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5})$/";

    /**
     * 统一社会信用码正则表达式
     */
    public static final String SOCIAL_CREDIT_CODE = "^[^IOSVZ\\d][\\da-zA-Z]{16}[A-HJ-NP-RT-UW-Y]$\n";

}
