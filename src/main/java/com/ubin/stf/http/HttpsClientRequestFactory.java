package com.ubin.stf.http;

import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.ssl.SSLContexts;
import org.springframework.http.client.SimpleClientHttpRequestFactory;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * @author 70432
 */
public class HttpsClientRequestFactory extends SimpleClientHttpRequestFactory {
    @Override
    protected void prepareConnection(HttpURLConnection connection, String httpMethod) throws IOException {
        try{
            if(!(connection instanceof HttpsURLConnection)){
                super.prepareConnection(connection, httpMethod);
            }
            if (connection instanceof HttpsURLConnection){
                KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
                // 信任任何链接
                TrustStrategy anyTrustStrategy = new TrustStrategy() {
                    @Override
                    public boolean isTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
                        return true;
                    }
                };
                SSLContext ctx = SSLContexts.custom().loadTrustMaterial(trustStore,anyTrustStrategy).build();
                ((HttpsURLConnection) connection).setSSLSocketFactory(ctx.getSocketFactory());
                HttpsURLConnection httpsConnection = (HttpsURLConnection) connection;
                super.prepareConnection(httpsConnection, httpMethod);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }
}
