package com.foodciti.foodcitipartener.utils;

import java.nio.charset.Charset;
import java.util.List;

public class PrintUtils {
    public static final String ACTION_PRINT_REQUEST = "print_req";
    public static final String PRINT_DATA = "print_data";
    public static final byte HT = 0x9;
    public static final byte LF = 0x0A;
    public static final byte CR = 0x0D;
    public static final byte ESC = 0x1B;
    public static final byte DLE = 0x10;
    public static final byte GS = 0x1D;
    public static final byte FS = 0x1C;
    public static final byte STX = 0x02;
    public static final byte US = 0x1F;
    public static final byte CAN = 0x18;
    public static final byte CLR = 0x0C;
    public static final byte EOT = 0x04;

    public static final byte[] INIT = {27, 64};
    public static byte[] FEED_LINE = {10};

    public static byte[] SELECT_FONT_A = {20, 33, 0};

    public static byte[] SET_BAR_CODE_HEIGHT = {29, 104, 100};
    public static byte[] PRINT_BAR_CODE_1 = {29, 107, 2};
    public static byte[] SEND_NULL_BYTE = {0x00};

    public static byte[] SELECT_PRINT_SHEET = {0x1B, 0x63, 0x30, 0x02};
    public static byte[] FEED_PAPER_AND_CUT = {0x1D, 0x56, 66, 0x00};

    public static byte[] SELECT_CYRILLIC_CHARACTER_CODE_TABLE = {0x1B, 0x74, 0x11};

    public static byte[] SELECT_BIT_IMAGE_MODE = {0x1B, 0x2A, 33, -128, 0};
    public static byte[] SET_LINE_SPACING_24 = {0x1B, 0x33, 24};
    public static byte[] SET_LINE_SPACING_30 = {0x1B, 0x33, 30};

    public static byte[] TRANSMIT_DLE_PRINTER_STATUS = {0x10, 0x04, 0x01};
    public static byte[] TRANSMIT_DLE_OFFLINE_PRINTER_STATUS = {0x10, 0x04, 0x02};
    public static byte[] TRANSMIT_DLE_ERROR_STATUS = {0x10, 0x04, 0x03};
    public static byte[] TRANSMIT_DLE_ROLL_PAPER_SENSOR_STATUS = {0x10, 0x04, 0x04};

    public static final byte[] ESC_FONT_COLOR_DEFAULT = new byte[] { 0x1B, 'r',0x00 };
    public static final byte[] FS_FONT_ALIGN = new byte[] { 0x1C, 0x21, 1, 0x1B,
            0x21, 1 };
    public static final byte[] ESC_ALIGN_LEFT = new byte[] { 0x1b, 'a', 0x00 };
    public static final byte[] ESC_ALIGN_RIGHT = new byte[] { 0x1b, 'a', 0x02 };
    public static final byte[] ESC_ALIGN_CENTER = new byte[] { 0x1b, 'a', 0x01 };
    public static final byte[] ESC_CANCEL_BOLD = new byte[] { 0x1B, 0x45, 0 };


    /*********************************************/
    public static final byte[] ESC_HORIZONTAL_CENTERS = new byte[] { 0x1B, 0x44, 20, 28, 00};
    public static final byte[] ESC_CANCLE_HORIZONTAL_CENTERS = new byte[] { 0x1B, 0x44, 00 };
    /*********************************************/

    public static final byte[] ESC_ENTER = new byte[] { 0x1B, 0x4A, 0x40 };
    public static final byte[] PRINTE_TEST = new byte[] { 0x1D, 0x28, 0x41 };

    public static byte[] setAlignment(char paramChar) {
        byte[] arrayOfByte = new byte[3];
        arrayOfByte[0] = 0x1B;
        arrayOfByte[1] = 0x61;

        switch (paramChar) {
            case '1':                   // Center
                arrayOfByte[2] = 0x01;
                break;
            case '2':                   // Right
                arrayOfByte[2] = 0x02;
                break;
            default:                    // Left
                arrayOfByte[2] = 0x00;
                break;
        }
        return arrayOfByte;
    }

    // Character size (Width)
    public static byte[] setWH(char paramChar) {
        byte[] arrayOfByte = new byte[3];
        arrayOfByte[0] = 0x1D;
        arrayOfByte[1] = 0x21;

        switch (paramChar) {
            case '1':
                arrayOfByte[2] = 0x0F;

                break;
            case '2':
                arrayOfByte[2] = 0x10;
                break;
            case '3':
                arrayOfByte[2] = 0x01;
                break;
            case '4':
                arrayOfByte[2] = 0x11;
                break;
            default:
                arrayOfByte[2] = 0x00;
                break;
        }

        return arrayOfByte;
    }

    public static byte[] setHT(int...colPositions) {
        int length = colPositions.length;
        byte[] bytes = new byte[3+length];
        bytes[0] = 0x1B;
        bytes[1] = 0x44;
        int j=2;
        for(int i: colPositions) {
            bytes[j]=(byte)i;
            j++;
        }
        bytes[j]=0;

        return bytes;
    }

    public static byte[] resetTabPosition() {
        byte[] bytes=new byte[1];
        bytes[0] = 0x0D;
        return bytes;
    }

    public static byte[] setInternationalCharcters(char paramChar) {
        byte[] arrayOfByte = new byte[3];
        arrayOfByte[0] = 0x1B;
        arrayOfByte[1] = 0x52;
        switch (paramChar) {
            case '1':
                arrayOfByte[2] = 0x01;//France charactrer set
                break;
            case '2':
                arrayOfByte[2] = 0x02;//Germany charactrer set
                break;
            case '3':
                arrayOfByte[2] = 0x03;//UK charactrer set
                break;
            default:
                arrayOfByte[2] = 0x00;//USA charactrer set
                break;
        }

        return arrayOfByte;
    }

    public static byte[] getBytes(String data) {
        byte[] arrayOfByte = null;
        try {
            arrayOfByte = data.getBytes(Charset.defaultCharset());
        } catch (Exception ex) {

        }
        return arrayOfByte;
    }

    private static byte[] getGbk(String paramString) {
        byte[] arrayOfByte = null;
        try {
            arrayOfByte = paramString.getBytes("GBK");
        } catch (Exception ex) {

        }
        return arrayOfByte;

    }

    public static byte[] setCursorPosition(int position) {
        byte[] returnText = new byte[4];
        returnText[0] = 0x1B;
        returnText[1] = 0x24;
        returnText[2] = (byte) (position % 256);
        returnText[3] = (byte) (position / 256);
        return returnText;
    }

    public static byte[] setBold(boolean paramBoolean) {
        byte[] arrayOfByte = new byte[3];
        arrayOfByte[0] = 0x1B;
        arrayOfByte[1] = 0x45;
        if (paramBoolean) {
            arrayOfByte[2] = 0x01;
        } else {
            arrayOfByte[2] = 0x00;
        }
        return arrayOfByte;
    }


    public static byte[] cutPaper()   //切纸； GS V 66D 0D
    {
        byte[] arrayOfByte = new byte[]{0x1D, 0x56, 0x42, 0x00};
        return arrayOfByte;
    }

    public static byte[] openCash()   //开钱箱； DLE DC4 n m t
    {
        byte[] arrayOfByte = new byte[]{0x1B, 0x70, 0x00, (byte) 0xC0, (byte) 0xC0};
        return arrayOfByte;
    }

    public static void copyBytesToList(List<Byte> byteList, byte[]... bytes) {
        for(byte[] byteArray: bytes) {
            for (byte b : byteArray) {
                byteList.add(b);
            }
        }
    }

    public static byte[] getTabs(int n) {
        byte[] tabs = new byte[n];
        for(int i=0; i<n; i++) {
            tabs[i]=HT;
        }
        return tabs;
    }

    public static byte[] getLF(int n) {
        byte[] lineFeeds = new byte[n];
        for(int i=0; i<n; i++) {
            lineFeeds[i]=LF;
        }
        return lineFeeds;
    }

    public static byte[] toPrimitiveBytes(List<Byte> bytes) {
        byte[] byteArray = new byte[bytes.size()];
        int j=0;
        for(Byte b: bytes) {
            byteArray[j]=b.byteValue();
            j++;
        }
        return byteArray;
    }

}
