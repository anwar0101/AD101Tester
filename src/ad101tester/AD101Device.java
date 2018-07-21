/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ad101tester;

import com.sun.jna.Callback;
import com.sun.jna.Library;
import com.sun.jna.Native;


/**
 *
 * @author anwar
 */
public interface AD101Device extends Library {
    AD101Device INSTANCE = (AD101Device) Native.loadLibrary("AD101Device", AD101Device.class);
    
    interface EVENTCALLBACKPROC extends Callback{
        void invoke(int iLine, int iEvent, int iParam);
    }
    
    //methods init
    boolean AD101_InitDevice(int hwnd);
    //open all device and get count
    int AD101_GetDevice();
    //free device and resources
    void AD101_FreeDevice();
    //get current device count
    int AD101_GetCurDevCount();
    //set callback function for any event in device
    void AD101_SetEventCallbackFun(EVENTCALLBACKPROC fun);
    //get caller id
    int AD101_GetCallerID(int nLine, String callerID, String name, String time);
}
