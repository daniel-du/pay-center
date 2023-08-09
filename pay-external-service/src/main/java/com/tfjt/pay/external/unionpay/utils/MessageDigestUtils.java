package com.tfjt.pay.external.unionpay.utils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;

public class MessageDigestUtils {

    public static String sha256(MultipartFile file) {
        try {
            return getHash(file, "SHA-256");
        } catch (Exception var2) {
            var2.printStackTrace();
            return "";
        }
    }


    public static String sha256(File file) {
        try {
            return getHash(file, "SHA-256");
        } catch (Exception var2) {
            var2.printStackTrace();
            return "";
        }
    }

    private static String getHash(MultipartFile file, String hashType) throws Exception {
        try {
            InputStream fis = file.getInputStream();
            Throwable var3 = null;

            try {
                byte[] buffer = new byte[1024];
                MessageDigest md5 = MessageDigest.getInstance(hashType);
                boolean var6 = false;

                int numRead;
                while((numRead = fis.read(buffer)) > 0) {
                    md5.update(buffer, 0, numRead);
                }

                String var20 = byte2Hex(md5.digest());
                return var20;
            } catch (Throwable var16) {
                var3 = var16;
                throw var16;
            } finally {
                if (var3 != null) {
                    try {
                        fis.close();
                    } catch (Throwable var15) {
                        var3.addSuppressed(var15);
                    }
                } else {
                    fis.close();
                }

            }
        } catch (Exception var18) {
            throw var18;
        }
    }
    private static String getHash(File file, String hashType) throws Exception {
        try {
            InputStream fis = new FileInputStream(file);
            Throwable var3 = null;

            try {
                byte[] buffer = new byte[1024];
                MessageDigest md5 = MessageDigest.getInstance(hashType);
                boolean var6 = false;

                int numRead;
                while((numRead = fis.read(buffer)) > 0) {
                    md5.update(buffer, 0, numRead);
                }

                String var20 = byte2Hex(md5.digest());
                return var20;
            } catch (Throwable var16) {
                var3 = var16;
                throw var16;
            } finally {
                if (var3 != null) {
                    try {
                        fis.close();
                    } catch (Throwable var15) {
                        var3.addSuppressed(var15);
                    }
                } else {
                    fis.close();
                }

            }
        } catch (Exception var18) {
            throw var18;
        }
    }

    private static String byte2Hex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        String temp = null;

        for (byte aByte : bytes) {
            temp = Integer.toHexString(aByte & 255);
            if (temp.length() == 1) {
                sb.append("0");
            }

            sb.append(temp);
        }

        return sb.toString();
    }
}
