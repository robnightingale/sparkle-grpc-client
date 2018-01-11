package com.reposit.helloworld;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.eclipse.jetty.websocket.api.Session;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.ModelAndView;

import static spark.Spark.*;

public class HelloWorldRestClient {

    private final static Logger logger = LoggerFactory.getLogger(HelloWorldRestClient.class);

    // this map is shared between sessions and threads, so it needs to be thread-safe (http://stackoverflow.com/a/2688817)
    static Map<Session, String> userUsernameMap = new ConcurrentHashMap<>();
    static int nextUserNumber = 1; //Assign to username for next connecting user

    public static void main(String[] args) {
        staticFiles.location("/public"); //index.html is served at localhost:4567 (default port)
        staticFiles.expireTime(600);

        get("/grpc-greeting", (rq, rs) -> {
            HelloWorldClient client = new HelloWorldClient("localhost", 50051);
            try {
                /* Access a service running on the local machine on port 50051 */
                String user = "grpc world";
                if (args.length > 0) {
                    user = args[0]; /* Use the arg as the name to greet if provided */
                }
                client.greet(user);
                return rs.body();
            } finally {
                client.shutdown();
            }
        });

        get("/rest-greeting", (rq, rs) -> {
            HelloWorldClient client = new HelloWorldClient("localhost", 50051);
            try {
                /* Access a service running on the local machine on port 50051 */
                String user = "rest world";
                if (args.length > 0) {
                    user = args[0]; /* Use the arg as the name to greet if provided */
                }
                client.greet(user);
                return rs.body();
            } finally {
                client.shutdown();
            }
        });

        exception(Exception.class, (e, request, response) -> {
            logger.error("Unexpected exception", e);
            response.body("Error 500");
        });

    }

}
