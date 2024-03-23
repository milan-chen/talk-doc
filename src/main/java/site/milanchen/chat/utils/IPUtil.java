package site.milanchen.chat.utils;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * @author milan
 * @description
 */
public class IPUtil {

    public static String getClientIp(HttpServletRequest request) {
        // 尝试从HTTP请求的"X-Forwarded-For"头部获取IP地址。
        // "X-Forwarded-For"头通常由代理服务器设置，包含了原始请求的IP地址。
        String ip = request.getHeader("X-Forwarded-For");

        if (ip != null) {
            // 如果"X-Forwarded-For"头包含多个IP地址（通常由于多级代理造成），则取第一个非未知的IP地址。
            ip = ip.split(",")[0].trim();
        } else {
            // 如果"X-Forwarded-For"头不存在，尝试从"Proxy-Client-IP"头获取IP地址。
            // "Proxy-Client-IP"头也是由某些代理服务器设置的。
            ip = request.getHeader("Proxy-Client-IP");
        }

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            // 如果"Proxy-Client-IP"头不可用，尝试从"WL-Proxy-Client-IP"头获取IP地址。
            // "WL-Proxy-Client-IP"是WebLogic服务器设置的类似头部。
            ip = request.getHeader("WL-Proxy-Client-IP");
        }

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            // 如果"WL-Proxy-Client-IP"头也不可用，尝试从"X-Real-IP"头获取IP地址。
            // "X-Real-IP"头通常被用来记录原始的客户端IP地址。
            ip = request.getHeader("X-Real-IP");
        }

        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            // 如果以上所有方法都失败，使用request.getRemoteAddr()获取发起请求的客户端IP地址。
            // 这通常是直接连接到服务器的客户端IP。
            ip = request.getRemoteAddr();
            if ("127.0.0.1".equals(ip) || "0:0:0:0:0:0:0:1".equals(ip)) {
                // 如果获取到的IP是本地回环地址，尝试获取本机的真实IP地址。
                try {
                    ip = InetAddress.getLocalHost().getHostAddress();
                } catch (UnknownHostException e) {
                    // 如果本机IP地址获取失败，将IP地址设置为"unknown"。
                    ip = "unknown";
                }
            }
        }
        return ip; // 返回获取到的IP地址。
    }

}
