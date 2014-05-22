package org.motechproject.ananya.support.diagnostics;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class MachineDiagnostic {

    public Map<String, String> collect() {
        Map<String, String> results = new LinkedHashMap<String, String>();
        try {
            Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
            while (networkInterfaces.hasMoreElements()) {
                Enumeration<InetAddress> addresses = networkInterfaces.nextElement().getInetAddresses();
                while (addresses.hasMoreElements()) {
                    InetAddress ia = addresses.nextElement();
                    if (!ia.isLinkLocalAddress())
                        results.put(ia.getHostName(), ia.getHostAddress());
                }
            }
        } catch (Exception e) {
            results.put("error", "IP fetch failed: " + ExceptionUtils.getFullStackTrace(e));
        }
        return results;
    }
}
