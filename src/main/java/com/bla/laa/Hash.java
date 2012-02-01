package com.bla.laa;

import com.bla.laa.Common.CommonS;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.List;

//import org.apache.derby.iapi.services.io.ArrayUtil;


public class Hash {
    private static final Logger logger = LoggerFactory.getLogger(Hash.class);
    public static final int HASH_LENGHT = 32;

    /**
     * Gen from string
     *
     * @return text.length = 32
     */
    public static String getHash(String str) {
        return Hash.getHashFromByteArray(str.getBytes());
    }

    public static String getHash(List<String> strList) {
        String longStr = "";
        for (String str : strList)
            longStr += str;
        return Hash.getHashFromByteArray(longStr.getBytes());
    }


    /**
     * Gen from File
     */
    public static String getHash(File file) {
        byte[] byteArray = getBytesFromFile(file);
        return Hash.getHashFromByteArray(byteArray);
    }

    /**
     * Gen from BufferedImage
     */
    public static String getHash(BufferedImage img) {
        String hash = "";
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(img, CommonS.PICEXT, baos);
            baos.toByteArray();

            hash = Hash.getHashFromByteArray(baos.toByteArray());
        } catch (Exception e) {
            logger.error("",e);
        }

        return hash;
    }


    private static String getHashFromByteArray(byte[] byteArray) {
        String hashtext = "";
        try {
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(byteArray);
            byte[] hash = digest.digest();

            BigInteger bigInt = new BigInteger(1, hash);
            hashtext = bigInt.toString(16);
            // Now we need to zero pad it if you actually want the full 32 chars.
            while (hashtext.length() < 32)
                hashtext = "0" + hashtext;
        } catch (Exception ex) {
            logger.error("",ex);
        }
        return hashtext;
    }

    private static byte[] getBytesFromFile(File file) {
        try {
            InputStream is = new FileInputStream(file);

            long length = file.length();

            if (length > Integer.MAX_VALUE) {
                // File is too large
            }

            byte[] bytes = new byte[(int) length];

            int offset = 0;
            int numRead = 0;
            while (offset < bytes.length
                    && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
                offset += numRead;
            }

            if (offset < bytes.length) {
                throw new IOException("Could not completely read file " + file.getName());
            }

            is.close();

            return bytes;
        } catch (Exception e) {
            logger.error("",e);
        }
        return null;
    }

}
 