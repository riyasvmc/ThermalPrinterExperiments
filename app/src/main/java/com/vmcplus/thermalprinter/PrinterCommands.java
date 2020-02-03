package com.vmcplus.thermalprinter;

public class PrinterCommands {
    public static final byte[] INIT = {27, 64};
    public static byte[] FEED_LINE = {10};

    public static byte[] SELECT_FONT_A = {27, 77, 0};
    public static byte[] SELECT_FONT_B = {27, 77, 1};
    public static byte[] SELECT_FONT_C = {27, 77, 2};

    public static byte[] SET_BAR_CODE_HEIGHT = {29, 104, 100};
    public static byte[] PRINT_BAR_CODE_1 = {29, 107, 2};
    public static byte[] SEND_NULL_BYTE = {0x00};

    public static byte[] SELECT_PRINT_SHEET = {0x1B, 0x63, 0x30, 0x02};
    public static byte[] FEED_PAPER_AND_CUT = {0x1D, 0x56, 66, 0x00};

    public static byte[] SELECT_CYRILLIC_CHARACTER_CODE_TABLE = {0x1B, 0x74, 0x11};

    public static byte[] SELECT_BIT_IMAGE_MODE = {0x1B, 0x2A, 33, -128, 0}; // refer pdf document 1st & 2nd param are common, third density, 4th and 5th are width of the image.

    public static byte[] SET_LINE_SPACING_DEFAULT = {0x1B, 0x32};
    public static byte[] SET_LINE_SPACING_0 = {0x1B, 0x33, 0};
    public static byte[] SET_LINE_SPACING_10 = {0x1B, 0x33, 10};
    public static byte[] SET_LINE_SPACING_24 = {0x1B, 0x33, 24}; // HEX 1B 33 n            here n line spacing in n dots
    public static byte[] SET_LINE_SPACING_30 = {0x1B, 0x33, 30};

    public static byte[] TRANSMIT_DLE_PRINTER_STATUS = {0x10, 0x04, 0x01};
    public static byte[] TRANSMIT_DLE_OFFLINE_PRINTER_STATUS = {0x10, 0x04, 0x02};
    public static byte[] TRANSMIT_DLE_ERROR_STATUS = {0x10, 0x04, 0x03};
    public static byte[] TRANSMIT_DLE_ROLL_PAPER_SENSOR_STATUS = {0x10, 0x04, 0x04};

    public static byte[] SET_DOUBLE_HEIGHT = {0x1B, 0x21, 0x16};
    public static byte[] SET_DOUBLE_WIDTH = {0x1B, 0x21, 0x32};

    public static byte[] EMPHASIZE_ON = {0x1B, 0x45, 1};
    public static byte[] EMPHASIZE_OFF = {0X1B, 0X45, 0};

    public static final byte[] ESC_ALIGN_LEFT = new byte[] { 0x1b, 'a', 0x00 };
    public static final byte[] ESC_ALIGN_RIGHT = new byte[] { 0x1b, 'a', 0x02 };
    public static final byte[] ESC_ALIGN_CENTER = new byte[] { 0x1b, 'a', 0x01 };

    public static final byte[] PRINTE_TEST = new byte[] { 0x1D, 0x28, 0x41 };
}