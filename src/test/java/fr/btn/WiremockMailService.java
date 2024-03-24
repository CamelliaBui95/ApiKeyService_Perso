package fr.btn;

import com.github.tomakehurst.wiremock.WireMockServer;
import io.quarkus.test.common.QuarkusTestResourceLifecycleManager;

import java.util.Collections;
import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class WiremockMailService implements QuarkusTestResourceLifecycleManager {

    private WireMockServer wireMockServer;
    @Override
    public Map<String, String> start() {
        wireMockServer = new WireMockServer(8089);
        wireMockServer.start();
        configureFor(8089);


        stubFor(
            post(urlEqualTo("/mail"))
                    .withHeader("x-api-key", equalTo("TEwLHA9MSQ2dE5CY0VhdU81QnBhNUtTb0lWd2lRJGlUNlgyWHFsN3g5VTNR"))
                    .willReturn(
                            aResponse()
                                    .withStatus(200)
                    )
        );

        return Collections.singletonMap("%test.quarkus.rest-client.mail-service.url", wireMockServer.baseUrl());
    }

    @Override
    public void stop() {
        if(wireMockServer != null) {
            wireMockServer.stop();
        }
    }
}
