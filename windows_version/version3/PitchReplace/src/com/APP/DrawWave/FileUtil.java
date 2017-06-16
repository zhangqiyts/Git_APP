package com.APP.DrawWave;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
 
 
public class FileUtil {
    private String voxfreq = "6000";
 
    public FileUtil() {
    }
     
    public void long2Byte(byte[] output, long[] input, int len) {
        int i, j;
        for (i = 0, j = 0; j < len; i++, j += 4) {
            output[j] = (byte) (input[i] & 0xffL);
            output[j + 1] = (byte) ((input[i] >>> 8) & 0xffL);
            output[j + 2] = (byte) ((input[i] >>> 16) & 0xffL);
            output[j + 3] = (byte) ((input[i] >>> 24) & 0xffL);
        }
    }
 
    public void short2Byte(byte[] output, short sh) {
        output[0] = (byte) (sh & 0xffL);
        output[1] = (byte) ((sh >>> 8) & 0xffL);
    }
 
    public short byte2Short(byte b) {
        return b < 0 ? (short) (b & 0x7F + 128) : b;
    }
 
    public void convertV2W(String v3File , String wavFile) throws Exception{
        BufferedInputStream fileInput = null;
        BufferedOutputStream fileOut = null;
        int[] indsft = { -1, -1, -1,-1, 2, 4, 6, 8 };
 
        int[] stpsz = { 16, 17, 19, 21, 23, 25, 28, 31, 34, 37, 41, 45, 50, 55,
                60, 66, 73, 80, 88, 97, 107, 118, 130, 143, 157, 173, 190, 209,
                230, 253, 279, 307, 337, 371, 408, 449, 494, 544, 598, 658,
                724, 796, 876, 963, 1060, 1166, 1282, 1411, 1552, 1707, 1878,
                2066, 2272, 2499, 2749, 3024, 3327, 3660, 4026, 4428, 4871,
                5358, 5894, 6484, 7132, 7845, 8630, 9493, 10442, 11487, 12635,
                13899, 15289, 16818, 18500, 20350, 22385, 24623, 27086, 29794 };
 
        /* nibble to bit map */
        int nbl2bit[][] = { { 0, 0, 0, 0 }, { 0, 0, 0, 1 }, { 0, 0, 1, 0 },
                { 0, 0, 1, 1 }, { 0, 1, 0, 0 }, { 0, 1, 0, 1 }, { 0, 1, 1, 0 },
                { 0, 1, 1, 1 }, { 1, 0, 0, 0 }, { 1, 0, 0, 1 }, { 1, 0, 1, 0 },
                { 1, 0, 1, 1 }, { 1, 1, 0, 0 }, { 1, 1, 0, 1 }, { 1, 1, 1, 0 },
                { 1, 1, 1, 1 } };
 
        /* sign table */
        int sgns[] = { 1, -1 };
        File file = new File(v3File);
        /* step size index */
        int ssindex = 0;
            if(file == null) {
                System.out.println("没有找到文件:" + v3File);
                return ;
            }
             
            fileInput = new BufferedInputStream(new FileInputStream(file));
            fileOut = new BufferedOutputStream (new FileOutputStream(wavFile));
 
            long lPCMHDR[] = { 0x46464952, 0, 0x45564157, 0x20746d66, 16,
                    0x10001, 0, 0, 0x100002, 0x61746164, 0 };
            lPCMHDR[1] = 4 * file.length() + 0x30; // 文件长度
            lPCMHDR[6] = Integer.parseInt(voxfreq);
            lPCMHDR[7] = 2 * Integer.parseInt(voxfreq);
            lPCMHDR[10] = 4 * file.length();
 
            // 先写一个头
            byte[] b = new byte[44];
            long2Byte(b, lPCMHDR, 44);
            fileOut.write(b, (int) 0, b.length);
 
            short iVal = 0;
            int pos = 0;
            byte[] szBuf = new byte[128 * 1024];
            int diff;
            short incoded;
            byte[] szDesBuf = new byte[4 * 128 * 1024];
            int dwRead = 0;
             
            while ((dwRead = fileInput.read(szBuf, 0, 128 * 1024))!= -1) {
                for (int idx = 0; idx < dwRead - 1; idx++) {
                    incoded = (short) (byte2Short(szBuf[idx]) / 16);
                    diff = sgns[nbl2bit[incoded][0]]
                            * (stpsz[ssindex] * nbl2bit[incoded][1]
                                    + (stpsz[ssindex] / 2)
                                    * nbl2bit[incoded][2]
                                    + (stpsz[ssindex] / 4)
                                    * nbl2bit[incoded][3] + (stpsz[ssindex] / 8));
 
                    ssindex = ssindex + indsft[(incoded % 8)];
 
                    if (ssindex < 0)
                        ssindex = 0;
                    if (ssindex > 48)
                        ssindex = 48;
                    iVal += diff;
 
                    if (iVal > 2047)
                        iVal = 2047;
                    else if (iVal < -2047)
                        iVal = -2047;
                     
                    byte[] b2 = new byte[2];
                    short2Byte(b2, (short) (iVal * 16));
 
                    szDesBuf[pos] = b2[0];
                    pos++;
                    szDesBuf[pos] = b2[1];
                    pos++;
                     
                    // /////////////////////////////////////////////////
                    incoded = (short) (byte2Short(szBuf[idx]) % 16);
 
                    diff = sgns[nbl2bit[incoded][0]]
                            * (stpsz[ssindex] * nbl2bit[incoded][1]
                                    + (stpsz[ssindex] / 2)
                                    * nbl2bit[incoded][2]
                                    + (stpsz[ssindex] / 4)
                                    * nbl2bit[incoded][3] + (stpsz[ssindex] / 8));
 
                    ssindex = ssindex + indsft[(incoded % 8)];
 
                    if (ssindex < 0)
                        ssindex = 0;
                    if (ssindex > 48)
                        ssindex = 48;
                    iVal += diff;
                    //波长
                    if (iVal > 2047)
                        iVal = 2047;
                    else if (iVal < -2047)
                        iVal = -2047;
 
                    byte[] b3 = new byte[2];
                    short2Byte(b3, (short) (iVal * 16));
                    szDesBuf[pos] = b3[0];
                    pos++;
                    szDesBuf[pos] = b3[1];
                    pos++;
                }
                try {
                    fileOut.write(szDesBuf, 0,  dwRead * 4 );
                    fileOut.flush();
                } catch (Exception e) {
                    System.out.println("转换文件失败！"+e);
                }
                pos = 0;
            }
            System.out.println("转换文件到wav成功.......................");
            try {
                if (fileInput != null) fileInput.close();
                if (fileOut != null) fileOut.close();
            } catch (IOException e) {}
            return ;
    }
 
}