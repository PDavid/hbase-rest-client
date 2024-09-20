package hu.paksyd.hbase.restclient;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.hadoop.hbase.rest.model.VersionModel;
import org.apache.hbase.thirdparty.com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import org.apache.hbase.thirdparty.javax.ws.rs.core.MediaType;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpRequest;
import org.apache.hc.core5.http.HttpEntity;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.support.ClassicRequestBuilder;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.IOException;
import java.io.InputStream;

public class Main {

    public static void main(String[] args) throws Exception {
        checkProtobuf();
        checkXml();
        checkJson();
    }

    private static void checkProtobuf() throws IOException {

        System.out.println("Protobuf:");

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            ClassicHttpRequest httpGet = ClassicRequestBuilder.get("http://localhost:8080/version/rest")
                    .setHeader("Accept", "application/x-protobuf")
                    .build();

            System.out.println("GET " + httpGet.getRequestUri());

            httpClient.execute(httpGet, response -> {
                System.out.println(response.getCode() + " " + response.getReasonPhrase() + "\n");
                final HttpEntity httpEntity = response.getEntity();

                VersionModel versionModel = fromProtobuf(httpEntity.getContent());
                System.out.println("VersionModel: " + versionModel);

                // and ensure it is fully consumed
                EntityUtils.consume(httpEntity);
                return null;
            });
        }
    }

    private static VersionModel fromProtobuf(InputStream inputStream) throws IOException {
        VersionModel model = new VersionModel();
        model.getObjectFromMessage(inputStream);
        return model;
    }

    private static void checkXml() throws Exception {

        System.out.println("XML:");

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            ClassicHttpRequest httpGet = ClassicRequestBuilder.get("http://localhost:8080/version/rest")
                    .setHeader("Accept", "text/xml")
                    .build();

            System.out.println("GET " + httpGet.getRequestUri());

            httpClient.execute(httpGet, response -> {
                System.out.println(response.getCode() + " " + response.getReasonPhrase() + "\n");
                final HttpEntity httpEntity = response.getEntity();

                VersionModel versionModel = fromXml(httpEntity.getContent());
                System.out.println("VersionModel: " + versionModel);

                // and ensure it is fully consumed
                EntityUtils.consume(httpEntity);
                return null;
            });
        }
    }

    private static VersionModel fromXml(InputStream inputStream) throws IOException {
        VersionModel versionModel;
        try {
            JAXBContext context = JAXBContext.newInstance(VersionModel.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            versionModel = (VersionModel) unmarshaller.unmarshal(inputStream);
        } catch (JAXBException e) {
            throw new RuntimeException(e);
        }
        return versionModel;
    }

    private static void checkJson() throws Exception {

        System.out.println("JSON:");

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            ClassicHttpRequest httpGet = ClassicRequestBuilder.get("http://localhost:8080/version/rest")
                    .setHeader("Accept", "application/json")
                    .build();

            System.out.println("GET " + httpGet.getRequestUri());

            httpClient.execute(httpGet, response -> {
                System.out.println(response.getCode() + " " + response.getReasonPhrase() + "\n");
                final HttpEntity httpEntity = response.getEntity();

                VersionModel versionModel = fromJson(httpEntity);
                System.out.println("VersionModel: " + versionModel);

                // and ensure it is fully consumed
                EntityUtils.consume(httpEntity);
                return null;
            });
        }
    }

    private static VersionModel fromJson(HttpEntity httpEntity) throws IOException {
        ObjectMapper mapper = new JacksonJaxbJsonProvider().locateMapper(VersionModel.class,
                MediaType.APPLICATION_JSON_TYPE);
        // Have to configure ObjectMapper to not fail on unknown properties
        // as older versions of VersionModel does not have newer fields.
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        return mapper.readValue(httpEntity.getContent(), VersionModel.class);
    }
}