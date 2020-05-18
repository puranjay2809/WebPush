package dispatcher;

import com.clevertap.apns.ApnsClient;
import com.clevertap.apns.Notification;
import com.clevertap.apns.NotificationResponse;
import com.clevertap.apns.clients.ApnsClientBuilder;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.IOException;

public class SafariPushDispatcher extends HttpServlet {

    public void service(HttpServletRequest reqq , HttpServletResponse ress) throws IOException {
        String token = reqq.getParameter("data");
        FileInputStream cert = new FileInputStream("/Users/puranjay/Documents/Safari/safari_push_package_creator/Certificates.p12");
        try {
            //Using Clevertap http2 APNS
            final ApnsClient client = new ApnsClientBuilder()
                    .inSynchronousMode()
                    .withProductionGateway(true)
                    .withCertificate(cert)
                    .withPassword("tap1234")
                    .withDefaultTopic("")
                    .withPort(2197)
                    .build();

            Notification n = new Notification.Builder(token)
                    .alertBody("This is a Safari Notification!").alertTitle("Eureka").urlArgs(new String[]{"www.google.com"}).build();
            NotificationResponse result = client.push(n);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
