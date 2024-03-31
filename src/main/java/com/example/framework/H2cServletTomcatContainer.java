package com.example.framework;

import org.apache.coyote.http2.Http2Protocol;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.tomcat.TomcatProtocolHandlerCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.stereotype.Component;

@Component
public class H2cServletTomcatContainer implements WebServerFactoryCustomizer<TomcatServletWebServerFactory> {
    @Value("${server.http2.timeout:20000}")
    private int timeout;

    @Override
    public void customize(TomcatServletWebServerFactory factory) {
        Http2Protocol http2Protocols = new Http2Protocol();
        http2Protocols.setKeepAliveTimeout(timeout);
        http2Protocols.setStreamReadTimeout(timeout);
        http2Protocols.setStreamWriteTimeout(timeout);
        TomcatProtocolHandlerCustomizer handler = s -> s.addUpgradeProtocol(http2Protocols);
        factory.addProtocolHandlerCustomizers(handler);
    }

}
