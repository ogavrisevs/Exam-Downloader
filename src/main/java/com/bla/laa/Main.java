package com.bla.laa;

import com.bla.laa.Common.MyCustException;
import org.slf4j.LoggerFactory;

public class Main {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(CsddBkat.class);
    static final String[] startOpt = {"StartCrawler", "StartDdbServ", "PrintOneQusetion", "PrintAll"};

    static {
        logger.info("version : " + Main.class.getPackage().getImplementationVersion());
    }

    public static void main(String[] args) throws MyCustException {
        if (args.length != 0) {
            if (args[0].contains(startOpt[0]))
                new ThreadRunner();
            else if (args[0].contains(startOpt[1]))
                new Storage(true);
            else if ((args[0].contains(startOpt[2])) && (args.length >= 2))
                Repo.printOneQuestion(args);
            else if (args[0].contains(startOpt[3]))
                Repo.printAllQuestions();
        } else

            System.out.println("Start prg. with : \n" +
                    " " + startOpt[0] + " - Start Crawler \n" +
                    " " + startOpt[1] + " - Start Derby Network Server \n" +
                    " " + startOpt[2] + " - Print One Test Case \n" +
                    " " + startOpt[3] + " - Print All Questions \n"
            );

    }
}
