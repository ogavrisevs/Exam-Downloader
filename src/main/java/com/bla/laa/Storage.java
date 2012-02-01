package com.bla.laa;

import com.bla.laa.Common.CommonS;
import com.bla.laa.Common.MyCustException;
import org.apache.derby.drda.NetworkServerControl;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Blob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Properties;

public class Storage {
    private static final org.slf4j.Logger logger = LoggerFactory.getLogger(CsddBkat.class);
    // Network Server adr.
    public static final InetAddress curIP = getLocIpAdrr();
    public static String dbaseDir = "dbDir";
    public static Boolean traceOn = true;
    public static String traceDir = "trace";
    // ip for connecting to Network Db Server
    public static String servIP = "localhost";
    public static int servPort = 1528;
    public static String dbaseName = "dbCSDD";
    public static String schemName = "CSDD5";
    private static final String clientDriver = "org.apache.derby.jdbc.ClientDriver";
    private static final String dbOptions = "create=true" /*user=user1;password=secret4me*/;
    private static final String tTypes[] = {"TABLE"};
    private static final Integer HASH_LENGTH = 32;
    private static final Integer SQL_DB_TIME_OUT = 5; /*sec*/

    // Table names
    public static final String TABLE_PICTURES_SMALL = "PICS";
    public static final String TABLE_PICTURES_LARGE = "PICL";
    public static final String TABLE_QUESTIONS = "QUESTIONS";
    public static final String TABLE_ANSWERS = "ANSWERS";
    public static final String TABLE_LOG = "LOG";
    public static final Integer FIELD_LOGMSG = 1024;
    public static final String TABLE_QUESTION_ANSWERS_LINKER = "QUESTIONANSWERS";
    public static final String TABLE_QUESTION_PICTURES_LINKER = "QUESTIONPICTURES";

    private Connection sqlConn = null;

    static {
        CommonS.loadProps();
    }

    public Storage() throws MyCustException {
        logger.warn(".Entry point : Start up as Derby Network client");
        this.sqlConn = getSqlConn();
        setSchema();
        setUpDb();
        setTestConn();
    }

    Storage(Boolean startServ) {
        logger.warn(".Entry point : Start up as Derby Network Server");
        if (!startServ)
            return;

        setNetWorkServerProp();
        NetworkServerControl serverControl = startNetworkServer();
        printServInfo(serverControl);
        while (isServerStarted(serverControl))
            CommonS.sleepThread(100000);
    }

    public Connection getSqlConn() throws MyCustException {
        if (sqlConn == null)
            sqlConn = crtSqlConnection();

        try {
            if (!sqlConn.isValid(SQL_DB_TIME_OUT/*sec*/))
                sqlConn = crtSqlConnection();
        } catch (Exception e) {
            logger.error("", e);
        }

        return sqlConn;
    }

    public void setSqlConn(Connection sqlConn) {
        this.sqlConn = sqlConn;
    }

    /**
     * Client server mode
     * connection example : jdbc:derby://192.168.1.100:1527/db
     */
    public Connection crtSqlConnection() throws MyCustException {
        String conUrl = getServerUrl() + "/" + dbaseName + ";" + dbOptions;
        logger.info("crt connectin jdbc : " + conUrl);
        Connection sqlConectLoc = null;
        try {
            Class.forName(clientDriver);
            sqlConectLoc = DriverManager.getConnection(conUrl);
            sqlConectLoc.setAutoCommit(true);
        } catch (Exception except) {
            logger.error("", except);
            throw new MyCustException();
        }
        return sqlConectLoc;
    }

    private void setSchema() throws MyCustException {
        if (chkSchemaExists(this.schemName) != true)
            execSQL("create schema " + schemName);
        execSQL("set schema " + schemName);
    }

    /**
     * Embedded db
     */
    /*
    public void connectEmbeddded() {
        try {
            Class.forName("embeddedDriver");
            sqlCon = DriverManager.getConnection("jdbc:derby:" + this.dbaseName + ";" + dbOptions);

        } catch (Exception ex) {
            logger.error("", ex);
        }
    } */

    void setUpDb() {
        PreparedStatement ps;
        try {
            if (chkTableExists(Storage.TABLE_PICTURES_LARGE) != true) {
                ps = this.getSqlConn().prepareStatement(
                        "create table " + schemName + "." + Storage.TABLE_PICTURES_LARGE + " (" +
                                "hash char(" + String.valueOf(HASH_LENGTH) + ") , " +
                                "CSDDid integer , " +
                                "pic blob ,  " +
                                "dt timestamp default current timestamp  " +
                                " )");
                ps.execute();
            }

            if (chkTableExists(Storage.TABLE_PICTURES_SMALL) != true) {
                ps = this.getSqlConn().prepareStatement(
                        "create table " + schemName + "." + Storage.TABLE_PICTURES_SMALL + " (" +
                                "hash char(" + String.valueOf(HASH_LENGTH) + ") , " +
                                "CSDDid integer , " +
                                "pic blob ,  " +
                                "dt timestamp default current timestamp  " +
                                " )");
                ps.execute();
            }

            if (chkTableExists(Storage.TABLE_QUESTIONS) != true) {
                ps = this.getSqlConn().prepareStatement(
                        "create table " + schemName + "." + Storage.TABLE_QUESTIONS + " (" +
                                "hash   char(" + String.valueOf(HASH_LENGTH) + ") , " +
                                "CSDDid integer , " +
                                "questionText varchar( 1024) , " +
                                " dt timestamp default current timestamp  " +
                                " )");
                ps.execute();
            }

            if (chkTableExists(Storage.TABLE_ANSWERS) != true) {
                ps = this.getSqlConn().prepareStatement(
                        "create table " + schemName + "." + Storage.TABLE_ANSWERS + " (" +
                                "hash    char(" + String.valueOf(HASH_LENGTH) + ") , " +
                                "CSDDid  integer , " +
                                "answtxt varchar (1024) , " +
                                "answcor BOOLEAN , " +
                                "dt timestamp default current timestamp  " +
                                " )");
                ps.execute();
            }

            if (chkTableExists(Storage.TABLE_QUESTION_ANSWERS_LINKER) != true) {
                ps = this.getSqlConn().prepareStatement(
                        "create table " + schemName + "." + Storage.TABLE_QUESTION_ANSWERS_LINKER + " (" +
                                "answer char(" + String.valueOf(HASH_LENGTH) + ") , " +
                                "answerCSDDid integer , " +
                                "question char(" + String.valueOf(HASH_LENGTH) + ") , " +
                                "questionCSDDid integer , " +
                                "dt timestamp default current timestamp  " +
                                " )");
                ps.execute();
            }

            if (chkTableExists(Storage.TABLE_QUESTION_PICTURES_LINKER) != true) {
                ps = this.getSqlConn().prepareStatement(
                        "create table " + schemName + "." + Storage.TABLE_QUESTION_PICTURES_LINKER + " (" +
                                "questionCSDDid integer , " +
                                "question char(" + String.valueOf(HASH_LENGTH) + ") , " +
                                "picCSDDid integer , " +
                                "picL char(" + String.valueOf(HASH_LENGTH) + ") , " +
                                "picS char(" + String.valueOf(HASH_LENGTH) + ") ,  " +
                                "dt timestamp default current timestamp " +
                                " )");
                ps.execute();
            }


            if (chkTableExists(Storage.TABLE_LOG) != true) {
                ps = this.getSqlConn().prepareStatement(
                        "create table " + schemName + "." + Storage.TABLE_LOG + " (" +
                                "dt timestamp default current timestamp  , " +
                                "msg varchar (" + Storage.FIELD_LOGMSG + ")  " +
                                " )");
                ps.execute();
            }

        } catch (Exception e) {
            logger.error("", e);
            System.exit(1);
        }
    }

    void setTestConn() throws MyCustException {
        PreparedStatement ps;
        try {
            if (chkTableExists("AA") != true) {
                ps = this.getSqlConn().prepareStatement("create table " + schemName + ".aa (a1 char(10))");
                ps.execute();
            }
            ps = this.getSqlConn().prepareStatement("insert into " + schemName + ".aa (a1) values ('aaa1') ");
            ps.execute();

            ps = this.getSqlConn().prepareStatement("select * from aa  ");
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                rs.getString(1);

            ps = this.getSqlConn().prepareStatement("drop table " + schemName + ".aa ");
            ps.execute();

            //System.out.print(rs.getString(1) + " ");
        } catch (SQLException e) {
            logger.error("", e);
        }
    }

    /**
     * @return true - exists
     */
    public boolean chkTableExists(String tableName) {
        tableName = tableName.toUpperCase();
        try {
            DatabaseMetaData dm = getSqlConn().getMetaData();
            ResultSet rs = dm.getTables(null, schemName, tableName, tTypes);
            if (rs.next())
                return true;

        } catch (Exception e) {
            logger.error("table not exists !", e);
        }
        return false;
    }

    /**
     * @return true - exists
     */
    public boolean chkSchemaExists(String thisschemaName) {
        thisschemaName = thisschemaName.toUpperCase();
        try {
            DatabaseMetaData dm = getSqlConn().getMetaData();
            ResultSet rs = dm.getSchemas(null, thisschemaName);
            if (rs.next())
                return true;

        } catch (Exception e) {
            logger.error("", e);
        }
        return false;
    }

    Blob getPics(Integer picCsddId, String picHash, String tableName) {
        Blob photo = null;
        try {
            String sql = "Select pic from " + Storage.schemName + "." + tableName + " " +
                    "where CSDDid = " + picCsddId + " and hash =  '" + picHash + "'";
            PreparedStatement ps = this.getSqlConn().prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                photo = rs.getBlob(1);
            }

            //rs.close();
        } catch (Exception ex) {
            logger.error("cant get pic !", ex);
        }
        return photo;
    }

    public void execSQL(String sql) throws MyCustException {
        try {
            Statement stat = getSqlConn().createStatement();
            stat.execute(sql);
            stat.close();
        } catch (SQLException sqlExcept) {
            logger.error("", sqlExcept);
        }
    }

    public void logToDb(Exception e) throws MyCustException {
        StackTraceElement[] stack = e.getStackTrace();
        for (StackTraceElement ste : stack) {
            logOneLine(ste.toString());
        }
    }

    public void logToDb(String logMsg) throws MyCustException {
        String msgArr[] = logMsg.split("\n");
        for (String msg : msgArr)
            logOneLine(msg);
    }

    public void logToDb(String[] logMsg) throws MyCustException {
        for (String msg : logMsg)
            logOneLine(msg);
    }

    public void logToDb(List<String> logMsg) throws MyCustException {
        for (String msg : logMsg)
            logOneLine(msg);
    }


    private void logOneLine(String logStr) throws MyCustException {
        if ((logStr != null) && (logStr.length() > Storage.FIELD_LOGMSG))
            logToDb(CommonS.splitEqually(logStr, (Storage.FIELD_LOGMSG - 1)));
        this.execSQL("insert into " + TABLE_LOG + " ( msg ) values ('" + logStr + "')");
    }

    public ResultSet execQuery(String sql) throws MyCustException {
        try {
            Statement stat = getSqlConn().createStatement();
            ResultSet rs = stat.executeQuery(sql);
            //stat.close();
            return rs;
        } catch (SQLException sqlExcept) {
            logger.debug(sql);
            logger.error("", sqlExcept);
        }
        return null;
    }

    public void shutDownServer() throws MyCustException {
        try {
            if (getSqlConn() != null) {
                DriverManager.getConnection(getServerUrl() + "/" + dbaseName + ";shutdown=true");
                getSqlConn().close();
            }
        } catch (SQLException sqlExcept) {
            logger.error("", sqlExcept);
        }
    }

    public static NetworkServerControl startNetworkServer() {
        NetworkServerControl serverControl = null;
        logger.info("connecting : " + curIP + " , " + servPort);
        try {
            serverControl = new NetworkServerControl(curIP, servPort);
            PrintWriter pw = new PrintWriter(System.out, true);
            serverControl.start(pw);
            serverControl.logConnections(true);
            if (traceOn) {
                serverControl.trace(true);
                serverControl.setTraceDirectory(dbaseDir + "\\" + traceDir);
            }

        } catch (Exception e) {
            logger.error("", e);
        }
        return serverControl;
    }

    public static InetAddress getLocIpAdrr() {
        InetAddress addr = null;
        try {
            //InetAddress [] addrs = InetAddress.getAllByName("oskars-HP");
            addr = InetAddress.getLocalHost();
            logger.info("Set curIP : " + addr.getHostAddress());
        } catch (UnknownHostException e) {
            logger.error("", e);
        }

        return addr;
    }

    private static boolean isServerStarted(NetworkServerControl server) {
        try {
            server.ping();
        } catch (Exception e) {
            logger.error("", e);
            return false;
        }
        return true;
    }

    private static void printServInfo(NetworkServerControl server) {
        try {
            logger.debug(server.getSysinfo());
        } catch (Exception e) {
            logger.error("", e);
            // TODO: handle exception
        }
    }

    private static void setNetWorkServerProp() {
        //CommonS.loadProps();
        Properties p = System.getProperties();
        p.setProperty("derby.system.home", dbaseDir);

    }

    public String getServerUrl() {
        return "jdbc:derby://" + servIP + ":" + servPort + "/";
    }


}