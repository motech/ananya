package org.motechproject.ananya.domain;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Service
public class PeerServer {

    private String host;
    private Integer port;
    private String context;

    @Autowired
    public PeerServer(@Value("#{ananyaProperties['peer.host']}") String host,
                      @Value("#{ananyaProperties['peer.port']}") Integer port,
                      @Value("#{ananyaProperties['peer.context']}") String context) {
        this.host = host;
        this.port = port;
        this.context = context;
    }

    public void copyResponse(String url, HttpServletResponse response) throws IOException {
        HttpMethod method = new GetMethod("http://" + host + ":" + port + "/" + context + "/" + url);
        new HttpClient().executeMethod(method);
        response.setContentType("text/html");
        IOUtils.copy(method.getResponseBodyAsStream(), response.getWriter());
    }

}
