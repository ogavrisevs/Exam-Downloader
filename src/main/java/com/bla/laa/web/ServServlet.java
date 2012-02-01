package com.bla.laa.web;

import com.bla.laa.Common.MyCustException;
import com.bla.laa.Container.TCase;
import com.bla.laa.Storage;
import com.bla.laa.StorageFactory;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServlet;
import java.io.IOException;


public class ServServlet extends HttpServlet {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ServServlet.class);
    public static Storage apacheDerbyClient = null;
    public static StorageFactory sf = null;

    public ServServlet() throws MyCustException {
        apacheDerbyClient = new Storage();
        sf = new StorageFactory(apacheDerbyClient);
    }

    protected void doGet(javax.servlet.http.HttpServletRequest request, javax.servlet.http.HttpServletResponse response) throws javax.servlet.ServletException, IOException {
        String idTxt = request.getParameter("id");
        if ((idTxt == null) || (idTxt.length() == 0)) {
            response.sendError(response.SC_BAD_REQUEST, "id not set : domain/serv?id=123");
            return;
        }

        Integer idInt = 0;
        try {
            idInt = Integer.parseInt(idTxt);
        } catch (NumberFormatException nfe) {
            logger.error("", nfe);
        }

        TCase tc = null;
        try {
            tc = sf.loadTicketFromDb(idInt).get(0);
        } catch (MyCustException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        if ((tc == null) || (!tc.isQuestionAnswerOK())) {
            response.sendError(response.SC_BAD_REQUEST, "cant load TestCase !");
            return;
        }

        response.setContentType("text/plain;charset=UTF-8");
        response.getWriter().write(tc.toJSON());
    }
}
