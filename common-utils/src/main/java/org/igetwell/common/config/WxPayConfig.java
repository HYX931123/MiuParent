package org.igetwell.common.config;

import lombok.Data;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.ssl.SSLContexts;

import javax.net.ssl.SSLContext;
import java.io.*;
import java.security.KeyStore;

/**
 * @ClassName: WxPayConfig
 * @ProjectName MiuParent
 * @Description: 微信支付配置
 * @Author Yixuan He
 * @Date 2018/12/27 16:40
 * @Version 1.0
 */
@Data
public class WxPayConfig {

    /**
     * http请求连接超时时间.
     */
    private int httpConnectionTimeout = 5000;

    /**
     * http请求数据读取等待时间.
     */
    private int httpTimeout = 10000;

    /**
     * 公众号appid.
     */
    private String appId;
    /**
     * 服务商模式下的子商户公众账号ID.
     */
    private String subAppId;
    /**
     * 商户号.
     */
    private String mchId;
    /**
     * 商户密钥.
     */
    private String mchKey;
    /**
     * 服务商模式下的子商户号.
     */
    private String subMchId;
    /**
     * 微信支付异步回掉地址，通知url必须为直接可访问的url，不能携带参数.
     */
    private String notifyUrl;
    /**
     * 交易类型.
     * <pre>
     * JSAPI--公众号支付
     * NATIVE--原生扫码支付
     * APP--app支付
     * </pre>
     */
    private String tradeType;
    /**
     * 签名方式.
     * 有两种HMAC_SHA256 和MD5
     */
    private String signType;
    private SSLContext sslContext;
    /**
     * p12证书文件的绝对路径或者以classpath:开头的类路径.
     */
    private String keyPath;

    /**
     * p12证书文件内容的字节数组.
     */
    private byte[] keyContent;
    /**
     * 微信支付是否使用仿真测试环境.
     * 默认不使用
     */
    private boolean useSandboxEnv = false;

    /**
     * 是否将接口请求日志信息保存到threadLocal中.
     * 默认不保存
     */
    private boolean ifSaveApiData = false;

    private String httpProxyHost;
    private Integer httpProxyPort;
    private String httpProxyUsername;
    private String httpProxyPassword;

    /**
     * 初始化ssl.
     *
     * @return the ssl context
     */
    public SSLContext initSSLContext() throws Exception {
        if (StringUtils.isBlank(this.getMchId())) {
            throw new Exception("请确保商户号mchId已设置");
        }

        InputStream inputStream;
        if (this.keyContent != null) {
            inputStream = new ByteArrayInputStream(this.keyContent);
        } else {
            if (StringUtils.isBlank(this.getKeyPath())) {
                throw new Exception("请确保证书文件地址keyPath已配置");
            }

            final String prefix = "classpath:";
            String fileHasProblemMsg = "证书文件【" + this.getKeyPath() + "】有问题，请核实！";
            String fileNotFoundMsg = "证书文件【" + this.getKeyPath() + "】不存在，请核实！";
            if (this.getKeyPath().startsWith(prefix)) {
                String path = StringUtils.removeFirst(this.getKeyPath(), prefix);
                if (!path.startsWith("/")) {
                    path = "/" + path;
                }
                inputStream = WxPayConfig.class.getResourceAsStream(path);
                if (inputStream == null) {
                    throw new Exception(fileNotFoundMsg);
                }
            } else {
                try {
                    File file = new File(this.getKeyPath());
                    if (!file.exists()) {
                        throw new Exception(fileNotFoundMsg);
                    }

                    inputStream = new FileInputStream(file);
                } catch (IOException e) {
                    throw new IOException(fileHasProblemMsg, e);
                }
            }
        }

        try {
            KeyStore keystore = KeyStore.getInstance("PKCS12");
            char[] partnerId2charArray = this.getMchId().toCharArray();
            keystore.load(inputStream, partnerId2charArray);
            this.sslContext = SSLContexts.custom().loadKeyMaterial(keystore, partnerId2charArray).build();
            return this.sslContext;
        } catch (Exception e) {
            throw new Exception("证书文件有问题，请核实！", e);
        } finally {
            IOUtils.closeQuietly(inputStream);
        }
    }
}
