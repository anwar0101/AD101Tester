/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ad101tester;

/**
 *
 * @author anwar
 */
public final class ByteUtils { 
 
    private ByteUtils() { 
    } 
 
    public static int hiWord(final int i) 
    { 
        return i >>> 16; 
    } 
 
    public static int loWord(final int i) { 
        return i & 0xFFFF; 
    } 
}
