package com.bla.laa.web;

import com.bla.laa.Common.MyCustException;
import com.bla.laa.Storage;
import com.bla.laa.StorageFactory;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public class ListQuestionsServlet extends HttpServlet {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(ListQuestionsServlet.class);
    public static Storage apacheDerbyClient = null;
    public static StorageFactory sf = null;

    public ListQuestionsServlet() throws MyCustException {
        apacheDerbyClient = new Storage();
        sf = new StorageFactory(apacheDerbyClient);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //apacheDerbyClient.getConn().isValid(100000);

        List<Integer> questionList = null;
        try {
            questionList = sf.getQuestionList();
        } catch (MyCustException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        if (questionList.size() == 0) {
            response.sendError(response.SC_BAD_REQUEST, "no questions");
            return;
        }

        StringBuffer sb = new StringBuffer();
        sb.append("{ \"Questions\": {");
        for (Integer id : questionList)
            sb.append("\"questionId\": \"" + id + "\",");
        sb.replace(sb.length() - 1, sb.length(), "");
        sb.append("}");
        sb.append("}");
        response.setContentType("text/plain");
        response.getWriter().write(sb.toString());
    }

    @Override
    public void init() throws ServletException {
        logger.debug("init ListQuestionsServlet");
        super.init();
    }
}
