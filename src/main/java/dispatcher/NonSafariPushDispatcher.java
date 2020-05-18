package dispatcher;

import model.PushPayload;
import model.Subscription;
import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;
import nl.martijndwars.webpush.Utils;
import org.apache.http.HttpResponse;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.jose4j.lang.JoseException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.*;
import java.security.spec.InvalidKeySpecException;

public class NonSafariPushDispatcher extends HttpServlet {

    public void service(HttpServletRequest reqq , HttpServletResponse ress) throws IOException {
        if (Security.getProvider(BouncyCastleProvider.PROVIDER_NAME) == null) {
            Security.addProvider(new BouncyCastleProvider());
        }

        Subscription subscription = new Subscription(reqq.getParameter("data"));

        String endpoint = subscription.getEndpoint();
        PublicKey publicKey = null;
        try {
            publicKey = Utils.loadPublicKey(subscription.getP256dh());
        } catch (NoSuchProviderException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }

        PushPayload payloadObj = new PushPayload("Test Message title", "Hello everyone!");
        byte[] auth = Utils.base64Decode(subscription.getAuth());
        byte[] payload = payloadObj.toString().getBytes();
        // Construct notification
        Notification notification = new Notification(endpoint,publicKey , auth, payload);
        // Construct push service
        PushService pushService = new PushService();
        pushService.setSubject("mailto:puranjay@celevrtap.com");
        try {
            pushService.setPublicKey(Utils.loadPublicKey("BFygpPBmFuCSAXq1UDxA-LNBM2gzYHbp6Xld16N0xXp962u7oVu4BMG0qoafzHXFR43aAJi51JpmboG5v8idtbQ"));
            pushService.setPrivateKey(Utils.loadPrivateKey("aw1dlnXIa96WBePS8tl-9eo5yEYImu-7FWfm4xf3Rpc"));
        } catch (NoSuchProviderException | NoSuchAlgorithmException | InvalidKeySpecException e) {
            e.printStackTrace();
        }

        // Send notification!
        HttpResponse httpResponse = null;
        try {
            httpResponse = pushService.send(notification);
        } catch (GeneralSecurityException | JoseException e) {
            e.printStackTrace();
        }
        System.out.println(httpResponse.getStatusLine().getStatusCode());
    }
}
