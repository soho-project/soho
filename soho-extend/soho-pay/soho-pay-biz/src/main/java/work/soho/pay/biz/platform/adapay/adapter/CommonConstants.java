package work.soho.pay.biz.platform.adapay.adapter;

/**
 * @author zhikang.yang
 * @description 接口参数常量
 * @date 2023/4/3
 **/
public class CommonConstants {

    /**
     * 签名类型
     */
    public static final String SIGN_TYPE = "rsa2";

    public static final String CHARSET_DEFAULT = "UTF-8";

    public static final String CONTENT_TYPE_DEFAULT = "application/json";

    public static final String CONTENT_TYPE_FROM = "x-www-form-urlencoded";

    public static final String HTTP_ENVIRNMENT_URL = "https://finance.chinapnr.com/npay/merchantRequest";
    public static final String CMD_ID = "cmd_id";
    public static final String VERSION = "version";
    public static final String MER_CUST_ID = "mer_cust_id";
    public static final String USER_CUST_ID = "user_cust_id";
    public static final String ORDER_ID = "order_id";
    public static final String ORDER_DATE = "order_date";
    public static final String SIGN = "sign";
    public static final String EXTENSION = "extension";
    public static final String IN_CUST_ID = "in_cust_id";
    public static final String IN_ACCT_ID = "in_acct_id";
    public static final String GOODS_DESC = "goods_desc";
    public static final String TRANS_AMT = "trans_amt";
    public static final String PFX_PWD = "pfx_pwd";
    public static final String DEVICE_INFO = "device_info";
    public static final String RET_URL = "ret_url";
    public static final String BG_RET_URL = "bg_ret_url";
    public static final String MER_PRIV = "mer_priv";
    public static final String ORDER_EXPIRE_TIME = "order_expire_time";
    public static final String APP_PAY_TYPE = "app_pay_type";
    public static final String ORGINAL_PLATFORM_SEQ_ID = "orginal_platform_seq_id";
    public static final String QUICKPAY_PAGE_FLAG = "quickpay_page_flag";
    public static final String DIV_DETAIL = "div_detail";
    public static final String DIV_DETAILS = "div_details";
    public static final String TRANS_TYPE = "trans_type";
    public static final String SELF_PARAM_INFO = "self_param_info";
    public static final String APP_ID = "app_id";
    public static final String BUYER_LOGON_ID = "buyer_logon_id";
    public static final String BUYER_ID = "buyer_id";
    public static final String IP_ADDR = "ip_addr";
    public static final String LOCATION_VAL = "location_val";
    public static final String DEV_INFO_JSON = "dev_info_json";
    public static final String PAY_URL = "pay_url";
    public static final String PAY_INFO = "pay_info";
    public static final String SCREEN_TYPE = "screen_type";
    public static final String STYLE_VALUES = "style_values";
    public static final String PAY_METHOD = "pay_method";
    public static final String PLATFORM_SEQ_ID = "platform_seq_id";
    public static final String RESP_CODE = "resp_code";
    public static final String RESP_DESC = "resp_desc";
    public static final String ACCT_ID = "acct_id";
    public static final String BANK_ID = "bank_id";
    public static final String CARD_NO = "card_no";
    public static final String BIND_CARD_ID = "bind_card_id";
    public static final String FEE_AMT = "fee_amt";
    public static final String FEE_CUST_ID = "fee_cust_id";
    public static final String FEE_ACCT_ID = "fee_acct_id";
    public static final String TRANS_STAT = "trans_stat";
    public static final String URL = "url";
    public static final String BIZ_TRANS_TYPE = "biz_trans_type";
    /**
     * 版本号
     */
    public static final String VERSION_VALUE = "10";
    /**
     * 代扣签约号
     */
    public static final String SIGN_SEQ_ID = "sign_seq_id";
    /**
     * 文件日期
     */
    public static final String FILE_DATE = "file_date";
    /**
     * 文件类型
     */
    public static final String FILE_TYPE = "file_type";
    /**
     * 汇付支付+编号
     */
    public static final String NPAY_MERID = "100001";
    /**
     * 文件上传编号
     */
    public static final String ATTACH_NO = "attach_no";
    /**
     * 上传文件类型
     */
    public static final String ATTACH_TYPE = "trans_type";
    /**
     * 卡号
     */
    public static final String CARD_NUM = "card_num";
    public static final String ID_CARD = "id_card";
    public static final String USER_NAME = "user_name";
    public static final String USER_MOBILE = "user_mobile";
    public static final String CARD_TYPE = "card_type";
    public static final String CARD_PROV = "card_prov";
    public static final String CARD_AREA = "card_area";
    public static final String BRANCH_NAME = "branch_name";
    public static final String DEFAULT_CASH_FLAG = "default_cash_flag";
    public static final String OUT_CUST_ID = "out_cust_id";
    public static final String OUT_ACCT_ID = "out_acct_id";
    public static final String CASH_TYPE = "cash_type";
    public static final String CARD_BUSS_TYPE = "card_buss_type";
    public static final String MOBILE = "mobile";
    public static final String TRANS_MODE = "trans_mode";
    public static final String PURPOSE = "purpose";
    public static final String USER_EMAIL = "user_email";

    public static final String GATE_ID = "gate_id";

    public static final String STAT_FLAG = "stat_flag";

    public static final String OPERATE_TYPE = "operate_type";
    public static final String SOLO_NAME = "solo_name";
    public static final String LEGAL_NAME = "legal_name";
    public static final String LEGAL_ID_CARD_TYPE = "legal_id_card_type";
    public static final String LEGAL_ID_CARD = "legal_id_card";
    /**
     * 个体户开户
     */
    public static final String LEGAL_MOBILE = "legal_mobile";
    public static final String CONTACT_NAME = "contact_name";
    public static final String CONTACT_MOBILE = "contact_mobile";
    public static final String CONTACT_EMAIL = "contact_email";
    public static final String LICENSE_START_DATE = "license_start_date";
    public static final String LICENSE_END_DATE = "license_end_date";
    public static final String SOLO_BUSINESS_ADDRESS = "solo_business_address";
    public static final String SOLO_FIXED_TELEPHONE = "solo_fixed_telephone";
    public static final String LEGAL_ID_CARD_START_DATE = "legal_id_card_start_date";
    public static final String LEGAL_ID_CARD_END_DATE = "legal_id_card_end_date";
    public static final String BUSINESS_SCOPE = "business_scope";
    public static final String ATTACH_NOS = "attach_nos";
    public static final String APPLY_ID = "apply_id";

    /**
     * 企业开户 start
     **/
    public static final String CORP_LICENSE_TYPE = "corp_license_type";
    public static final String CORP_TYPE = "corp_type";
    public static final String CORP_NAME = "corp_name";
    public static final String BUSINESS_CODE = "business_code";
    public static final String INSTITUTION_CODE = "institution_code";
    public static final String TAX_CODE = "tax_code";
    public static final String SOCIAL_CREDIT_CODE = "social_credit_code";
    public static final String BANK_ACCT_NAME = "bank_acct_name";
    public static final String BANK_ACCT_NUM = "bank_acct_no";
    public static final String BANK_BRANCH = "bank_branch";
    public static final String BANK_PROV = "bank_prov";
    public static final String BANK_AREA = "bank_area";
    public static final String LEGAL_CERT_START_DATE = "legal_cert_start_date";
    public static final String LEGAL_CERT_END_DATE = "legal_cert_end_date";
    public static final String CORP_BUSINESS_ADDRESS = "corp_business_address";
    public static final String CORP_REG_ADDRESS = "corp_reg_address";
    public static final String CORP_FIXED_TELEPHONE = "corp_fixed_telephone";
    public static final String CONTROLLING_SHAREHOLDER = "controlling_shareholder";
    public static final String AUDIT_STATUS = "audit_status";
    public static final String AUDIT_DESC = "audit_desc";
    public static final String AUDIT_COMMENT = "audit_comment";
    public static final String INDUSTRY = "industry";
    /**
     * 企业开户 end
     **/

    public static final String ACCT_TYPE = "acct_type";
    public static final String ACCT_NAME = "acct_name";
    public static final String TRANSFER_AMT = "transfer_amt";
    public static final String CARD_NAME = "card_name";
    public static final String VERIFY_TYPE = "verify_type";

    public static final String PARAM_DATA = "data";
    public static final String PARAM_SIGN_TYPE = "sign_type";
    public static final String PARAM_SIGN = "sign";
    public static final String COMMA = ",";
    public static final String DIV_TYPE = "div_type";
    public static final String OBJECT_INFO = "object_info";
}
