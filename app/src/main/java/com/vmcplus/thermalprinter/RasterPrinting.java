package com.vmcplus.thermalprinter;

import android.graphics.Bitmap;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by imrankst1221@gmail.com
 *
 */

public class RasterPrinting {

    private static String hexStr = "0123456789ABCDEF";
    private static String[] binaryArray = { "0000", "0001", "0010", "0011",
            "0100", "0101", "0110", "0111", "1000", "1001", "1010", "1011",
            "1100", "1101", "1110", "1111" };

    public static byte[] decodeBitmap(Bitmap bitmap){
        int width = 384;    String widthHex = "30" + "00";
        int height = 512;   String heightHex = "00" + "02";

        List<String> list = new ArrayList<String>(); //binaryString list
        StringBuffer stringBuffer;

        for (int i = 0; i < height; i++) {
            stringBuffer = new StringBuffer();
            for (int j = 0; j < width; j++) {
                int color = bitmap.getPixel(j, i);

                int r = (color >> 16) & 0xff;
                int g = (color >> 8) & 0xff;
                int b = color & 0xff;

                // if color close to white，bit='0', else bit='1'
                if (r > 160 && g > 160 && b > 160)
                    stringBuffer.append("0");
                else
                    stringBuffer.append("1");
            }
            list.add(stringBuffer.toString());
        }

        List<String> bmpHexList = binaryListToHexStringList(list);
        String commandHexString = "1D" + "76" + "30" + "00" + widthHex + heightHex; // GS v 0 m xL xH yL yH d1...dk

        List<String> commandList = new ArrayList<String>();
        commandList.add(commandHexString);
        commandList.addAll(bmpHexList);

        return hexList2Byte(commandList);
    }

    public static List<String> binaryListToHexStringList(List<String> list) {
        List<String> hexList = new ArrayList<String>();
        for (String binaryStr : list) {
            StringBuffer stringBuffer = new StringBuffer();
            for (int i = 0; i < binaryStr.length(); i += 8) {
                String string = binaryStr.substring(i, i + 8);

                String hexString = myBinaryStrToHexString(string);
                stringBuffer.append(hexString);
            }
            hexList.add(stringBuffer.toString());
        }
        return hexList;
    }

    public static String myBinaryStrToHexString(String binaryStr) {
        //int decimal = Integer.parseInt(binaryStr,2);
        //return Integer.toString(decimal,16);

        String hex = "";
        String f4 = binaryStr.substring(0, 4);
        String b4 = binaryStr.substring(4, 8);
        for (int i = 0; i < binaryArray.length; i++) {
            if (f4.equals(binaryArray[i]))
                hex += hexStr.substring(i, i + 1);
        }
        for (int i = 0; i < binaryArray.length; i++) {
            if (b4.equals(binaryArray[i]))
                hex += hexStr.substring(i, i + 1);
        }

        return hex;
    }

    public static byte[] hexList2Byte(List<String> list) {
        List<byte[]> commandList = new ArrayList<byte[]>();

        for (String hexStr : list) {
            commandList.add(hexStringToBytes(hexStr));
        }
        byte[] bytes = sysCopy(commandList);
        return bytes;
    }

    public static byte[] hexStringToBytes(String hexString) {
        if (hexString == null || hexString.equals("")) {
            return null;
        }
        hexString = hexString.toUpperCase();
        int length = hexString.length() / 2;
        char[] hexChars = hexString.toCharArray();
        byte[] d = new byte[length];
        for (int i = 0; i < length; i++) {
            int pos = i * 2;
            d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
        }
        return d;
    }

    public static byte[] sysCopy(List<byte[]> srcArrays) {
        int len = 0;
        for (byte[] srcArray : srcArrays) {
            len += srcArray.length;
        }
        byte[] destArray = new byte[len];
        int destLen = 0;
        for (byte[] srcArray : srcArrays) {
            System.arraycopy(srcArray, 0, destArray, destLen, srcArray.length);
            destLen += srcArray.length;
        }
        return destArray;
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }
}