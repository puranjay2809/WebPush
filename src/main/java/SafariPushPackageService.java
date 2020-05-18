
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/*
 * Version 1 Safari push package service
 */

public class SafariPushPackageService extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Log log = LogFactory.getLog(SafariPushPackageService.class);

    private void serve(HttpServletRequest req, HttpServletResponse resp)  {
        log.info(String.format("-------->| New request: { path:%s, time:%s, host:%s, ip:%s }",req.getPathInfo(), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()),req.getServerName(), req.getRemoteAddr()));
        resp.setContentType("text/plain");
        String clientOrigin = req.getHeader("origin");
        resp.setHeader("Access-Control-Allow-Origin", clientOrigin);

        // webServiceURL/version/pushPackages/websitePushID
        if(req.getPathInfo().contains("/pushPackages/")) {
            String site = req.getPathInfo().split("/pushPackages/")[1];
            log.info("<--------| Serving push package for websitePushID " + site);
            getZip(resp);
            // webServiceURL/version/devices/deviceToken/registrations/websitePushID

        } else if (req.getPathInfo().endsWith("/log")){
            // webServiceURL/version/log
            try {
                log.info("---------| Log request received. "+readBody(req));
            } catch (Exception e){
                log.info("Error in logging request", e);
            }
        }
    }

    private String readBody(HttpServletRequest request) throws IOException {
        // Read from request
        StringBuilder buffer = new StringBuilder();
        BufferedReader reader = request.getReader();
        String line;
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }
        return buffer.toString();
    }

    private void getZip(HttpServletResponse response){
        String fileName = "/Users/puranjay/Documents/WebPush/WebPushClient/safariPackage/pushPackage.zip";
        response.setContentType("application/zip");
        response.setHeader("Content-Disposition","attachment;filename=\"pushPackage.bin\"");
        FileInputStream is = null;
        try{
            File f = new File(fileName);
            int length = (int)f.length();
            byte[] arBytes = new byte[length];
            response.setContentLength(length);
            is = new FileInputStream(f);
            is.read(arBytes);
            is.close();
            ServletOutputStream op = response.getOutputStream();
            op.write(arBytes);
            op.flush();
            response.flushBuffer();
        }catch(IOException ioe)
        {
            log.info("Error while loading" + fileName + "zip",ioe);
        }finally {
            try {
                if(is!=null){
                    is.close();
                }
            } catch (IOException e) {
                log.info("Error while closing stream " + fileName + "zip", e);
            }
        }
    }

    @Override
    protected void doHead(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        serve(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        serve(req, resp);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        serve(req, resp);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        serve(req, resp);
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        serve(req, resp);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        serve(req, resp);
    }

}
