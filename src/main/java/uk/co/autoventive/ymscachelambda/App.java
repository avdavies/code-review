package uk.co.autoventive.ymscachelambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import lombok.extern.slf4j.Slf4j;

import java.io.InputStream;
import java.io.OutputStream;


@Slf4j
public class App implements RequestStreamHandler {

    public App() {
        log.info("In App constructor");
        System.out.println("Println in constructor");
    }

    @Override
    public void handleRequest(InputStream inputStream, OutputStream outputStream, Context context) {
        log.info("App handleRequest");
        System.out.println("Println in handlerequest");
    }
}
