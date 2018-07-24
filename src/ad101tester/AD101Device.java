/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ad101tester;

import com.sun.jna.Callback;
import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Structure;
import java.util.Arrays;
import java.util.List;


/**
 *
 * @author anwar
 */
public interface AD101Device extends Library {
    AD101Device INSTANCE = (AD101Device) Native.loadLibrary("AD101Device", AD101Device.class);
    
    interface EVENTCALLBACKPROC extends Callback{
        void invoke(int iLine, int iEvent, int iParam);
    }
    
    //structure decalration
    public static class AD101DEVICEPARAMETER extends Structure {

        public int nRingOn;
        public int nRingOff;
        public int nHookOn;
        public int nHookOff;
        public int nStopCID;
        public int nNoLine;

        // Add this parameter in new AD101(MCU Version is 6.0)
        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("nRingOn", "nRingOff", "nHookOn", "nHookOff", "nStopCID", "nNoLine");
        }
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
    int AD101_GetCallerID(int nLine, char[] callerID, char[] name, char[] time);
  
    int AD101_GetCPUVersion(int nLine, byte[] szCPUVersion);

    // Start reading cpu id of device 
    int AD101_ReadCPUID(int nLine);

    // Get readed cpu id of device 
    int AD101_GetCPUID(int nLine, char[] szCPUID);

    // Get dialed number 
    int AD101_GetDialDigit(int nLine, String szDialDigitBuffer);

    // Get collateral phone dialed number 
    int AD101_GetCollateralDialDigit(int nLine, String szDialDigitBuffer);

    // Get last line state 
    int AD101_GetState(int nLine);

    // Get ring count
    int AD101_GetRingIndex(int nLine);

    // Get talking time
    int AD101_GetTalkTime(int nLine);

    int AD101_GetParameter(int nLine, AD101DEVICEPARAMETER tagParameter);

    int AD101_ReadParameter(int nLine);

    // Set systematic parameter  
    int AD101_SetParameter(int nLine, AD101DEVICEPARAMETER tagParameter);

    // Change handle of window that uses to receive message
    int AD101_ChangeWindowHandle(int hWnd);

    // Show or don't show collateral phone dialed number
    void AD101_ShowCollateralPhoneDialed(boolean bShow);

    // Control led 
    int AD101_SetLED(int nLine, int enumLed);

    // Control line connected with ad101 device to busy or idel
    int AD101_SetBusy(int nLine, int enumLineBusy);

    // Set line to start talking than start timer
    int AD101_SetLineStartTalk(int nLine);

    // Set time to start talking after dialed number 
    int AD101_SetDialOutStartTalkingTime(int nSecond);

    // Set ring end time
    int AD101_SetRingOffTime(int nSecond);
    
}
