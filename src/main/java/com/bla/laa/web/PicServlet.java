package com.bla.laa.web;

import com.bla.laa.Common.CommonS;
import com.bla.laa.Common.MyCustException;
import com.bla.laa.Container.TCase;
import com.bla.laa.Storage;
import com.bla.laa.StorageFactory;

import javax.imageio.ImageIO;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.OutputStream;

public class PicServlet extends HttpServlet {
    public static Storage apacheDerbyClient = null;
    public static StorageFactory sf = null;

    public PicServlet() throws MyCustException {
        apacheDerbyClient = new Storage();
        sf = new StorageFactory(apacheDerbyClient);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        final String[] args = {"picL", "picS"};
        String idLtxt = request.getParameter(args[0]);
        String idStxt = request.getParameter(args[1]);

        if ((idLtxt == null) && (idStxt == null)) {
            String arrayAsStr = "";
            for (String str : args)
                arrayAsStr += " " + str;
            response.sendError(response.SC_BAD_REQUEST, "param not set, set as : \n domain/getpic?[" + arrayAsStr + "]=123 ");
            return;
        }

        Integer id = 0;
        if ((idLtxt != null) && (idLtxt.length() != 0))
            id = CommonS.parseInt(idLtxt);
        if ((idStxt != null) && (idStxt.length() != 0))
            id = CommonS.parseInt(idStxt);

        if (id == 0) {
            response.sendError(response.SC_BAD_REQUEST, "id not found ");
            return;
        }

        TCase tc = null;
        try {
            tc = sf.loadTicketFromDb(Integer.valueOf(id)).get(0);
        } catch (MyCustException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        if ((tc == null) || (!tc.isQuestionAnswerOK())) {
            response.sendError(response.SC_BAD_REQUEST, "cant load Pic !");
            return;
        }

        response.setContentType("image/jpeg");
        BufferedImage bi = null;
        if ((idLtxt != null) && (idLtxt.length() != 0))
            bi = tc.getPics().getImageLarge();
        if ((idStxt != null) && (idStxt.length() != 0))
            bi = tc.getPics().getImageSmall();

        OutputStream outputStream = response.getOutputStream();
        ImageIO.write(bi, "jpeg", outputStream);
        outputStream.close();
    }
}
