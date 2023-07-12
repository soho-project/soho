package work.soho.longlink.biz.util;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;
import lombok.extern.log4j.Log4j2;

import javax.net.ssl.SSLException;
import java.net.InetSocketAddress;
import java.security.cert.CertificateException;

/**
 * Some useful methods for server side.
 */
@Log4j2
public final class ServerUtil {

    private static final boolean SSL = System.getProperty("ssl") != null;

    private ServerUtil() {
    }

    public static SslContext buildSslContext() throws CertificateException, SSLException {
        if (!SSL) {
            return null;
        }
        SelfSignedCertificate ssc = new SelfSignedCertificate();
        return SslContextBuilder
                .forServer(ssc.certificate(), ssc.privateKey())
                .build();
    }

    /**
     * 根据连接信息获取连接ID
     *
     * @param clientAddress
     * @param localAddress
     * @return
     */
    public static String getConnectId(InetSocketAddress clientAddress, InetSocketAddress localAddress) {
//        log.info("local address info: {}", localAddress);
//        log.info("client address info：{}", clientAddress);
//        System.out.println(clientAddress.getAddress().getHostAddress());
//        System.out.println(clientAddress.getAddress().getCanonicalHostName());
//        System.out.println(clientAddress.getPort());
        return clientAddress.getAddress().getHostAddress() + "/" + clientAddress.getPort() +"/"+ localAddress.getAddress().getHostAddress() + "/" + localAddress.getPort();
    }

    /**
     * 根据上下文获取连接ID
     *
     * @param ctx
     * @return
     */
    public static String getConnectId(ChannelHandlerContext ctx) {
        InetSocketAddress ipSocket = (InetSocketAddress) ctx.channel().remoteAddress();
        InetSocketAddress localIpSocket = (InetSocketAddress) ctx.channel().localAddress();
        return getConnectId(ipSocket, localIpSocket);
    }
}
