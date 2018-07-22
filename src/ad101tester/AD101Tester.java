/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ad101tester;

import ad101tester.AD101Device.AD101DEVICEPARAMETER;
import static java.lang.System.out;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 *
 * @author anwar
 */
public class AD101Tester extends Application {

    private AD101Device cid = AD101Device.INSTANCE;

    //params
    final int MCU_BACKID = 0x07;	// Return Device ID
    final int MCU_BACKSTATE = 0x08;	// Return Device State
    final int MCU_BACKCID = 0x09;		// Return Device CallerID
    final int MCU_BACKDIGIT = 0x0A;	// Return Device Dial Digit
    final int MCU_BACKDEVICE = 0x0B;	// Return Device Back Device ID
    final int MCU_BACKPARAM = 0x0C;	// Return Device Paramter
    final int MCU_BACKCPUID = 0x0D;	// Return Device CPU ID
    final int MCU_BACKCOLLATERAL = 0x0E;		// Return Collateral phone dialed
    final int MCU_BACKDISABLE = 0xFF;		// Return Device Init
    final int MCU_BACKENABLE = 0xEE;
    final int MCU_BACKMISSED = 0xAA;		// Missed call 
    final int MCU_BACKTALK = 0xBB;		// Start Talk

    final int HKONSTATEPRA = 0x01; // hook on pr+  HOOKON_PRA
    final int HKONSTATEPRB = 0x02;  // hook on pr-  HOOKON_PRR
    final int HKONSTATENOPR = 0x03;  // have pr  HAVE_PR
    final int HKOFFSTATEPRA = 0x04;   // hook off pr+  HOOKOFF_PRA
    final int HKOFFSTATEPRB = 0x05;  // hook off pr-  HOOKOFF_PRR
    final int NO_LINE = 0x06; // no line  NULL_LINE
    final int RINGONSTATE = 0x07;  // ring on  RING_ON
    final int RINGOFFSTATE = 0x08;  // ring off RING_OFF
    final int NOHKPRA = 0x09; // NOHOOKPRA= 0x09, // no hook pr+
    final int NOHKPRB = 0x0a; // NOHOOKPRR= 0x0a, // no hook pr-
    final int NOHKNOPR = 0x0b; // NOHOOKNPR= 0x0b, // no hook no pr

    int WM_USBLINEMSG = 1024 + 180;

    private static String[] msgs = {
        "LED Off", "LED Off", "Red LED On", "Red LED Flash Slowly",
        "Red LED Flash Quickly", "Green LED On", "Green LED Flash Slowly",
        "Green LED Flash Quickly", "Yellow LED On", "Yellow LED Flash Slowly",
        "Yellow LED Flash Quickly"
    };

    @Override
    public void start(Stage primaryStage) {

        Label total = new Label();

        Button btnCount = new Button();
        btnCount.setText("check device");
        btnCount.setOnAction((ActionEvent event) -> {
            int to = cid.AD101_GetCurDevCount();
            total.setText("Total : " + to);
            System.out.println("Total: " + to);
        });

        Button btn = new Button();
        btn.setText("Init");
        btn.setOnAction((ActionEvent event) -> {
            if (cid.AD101_InitDevice(0)) {
                System.out.println("success");
                btn.setText("Initialized");
                btn.setDisable(true);

                //open all connected devices
                total.setText("Total: " + cid.AD101_GetDevice());

                //setting call back function
                Thread t = new Thread(() -> {
                    cid.AD101_SetEventCallbackFun((int iLine, int iEvent, int iParam) -> {
                        System.out.println("Line: " + iLine + " event: " + Integer.toHexString(iEvent)
                                + " param: " + Integer.toHexString(iParam));
                        
                        OnDeviceMsg(iLine, iEvent, iParam);
                        
                    });
                });

                t.start();

            } else {
                System.out.println("Port not initilized or used someone else");
            }
        });

        Button btnLeds = new Button();
        btnLeds.setText("Led Checks");
        btnLeds.setOnAction((ActionEvent t) -> {
            btnLeds.setDisable(true);

            Thread t1 = new Thread(() -> {
                int i = 0;
                for (String msg : msgs) {
                    cid.AD101_SetLED(0, i++);
                    out.println(msg);
                    btnLeds.setText(msg);
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(AD101Tester.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }

                Platform.runLater(() -> {
                    btnLeds.setDisable(false);
                });

            });

        });

        StackPane root = new StackPane();
        VBox vb = new VBox();
        vb.getChildren().add(btn);
        vb.getChildren().add(btnCount);
        vb.getChildren().add(btnLeds);
        vb.getChildren().add(total);
        root.getChildren().add(vb);

        Scene scene = new Scene(root, 300, 250);

        primaryStage.setTitle("CID Tester");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void OnDeviceMsg(int nLine, int iEvent, int iParam) {
        switch (iEvent) {
            case MCU_BACKDISABLE:
                //
                break;

            case MCU_BACKENABLE:
                //
                break;

            case MCU_BACKID: {
                String cpuVersion = "";
                cid.AD101_GetCPUVersion(nLine, cpuVersion);
            }
            break;

            case MCU_BACKCID: {
                String szCallerID = new String();
                String szName = new String();
                String szTime = new String();

                int nLen = cid.AD101_GetCallerID(nLine, szCallerID, szName, szTime);
                out.println("Caller ID:" + szCallerID + ", Name: " + szName);
            }
            break;

            case MCU_BACKSTATE: {
                switch (iParam) {
                    case HKONSTATEPRA:
                        //
                        out.println("HOOK ON PR+");
                        break;

                    case HKONSTATEPRB:
                        out.println("HOOK ON PR-");
                        break;

                    case HKONSTATENOPR:
                        out.println("HOOK ON NOPR");
                        break;

                    case HKOFFSTATEPRA: {
                        out.println("HOOK OFF PR+");
                        String szCallerID = new String();
                        String szName = new String();
                        String szTime = new String();

                        if (cid.AD101_GetCallerID(nLine, szCallerID, szName, szTime) < 1 || cid.AD101_GetRingIndex(nLine) < 1) {
                            //set null for phone id
                        }

                        //null phone id
                    }
                    break;

                    case HKOFFSTATEPRB: {
                        out.println("HOOK OFF PR-");

                        String szCallerID = new String();
                        String szName = new String();
                        String szTime = new String();

                        if (cid.AD101_GetCallerID(nLine, szCallerID, szName, szTime) < 1 || cid.AD101_GetRingIndex(nLine) < 1) {
                            //set null phone id
                        }

                    }
                    break;

                    case NO_LINE: {
                        out.println("No Line");

                        String szCallerID = new String();
                        String szName = new String();
                        String szTime = new String();

                        if (cid.AD101_GetCallerID(nLine, szCallerID, szName, szTime) < 1 || cid.AD101_GetRingIndex(nLine) < 1) {
                            //no line
                        }

                        //no line
                    }
                    break;

                    case RINGONSTATE: {
                        String szRing = "Ring:" + String.format("{0:D2}", cid.AD101_GetRingIndex(nLine));
                        out.println(szRing);
                    }
                    break;

                    case RINGOFFSTATE:
                        out.println("Ring Off");
                        break;

                    case NOHKPRA:
                        out.println("NO HOOK PR+");
                        break;

                    case NOHKPRB:
                        out.println("NO HOOK PR-");
                        break;

                    case NOHKNOPR: {
                        out.println("NO HOOK NOPR");

                        String szCallerID = new String();
                        String szName = new String();
                        String szTime = new String();

                        if (cid.AD101_GetCallerID(nLine, szCallerID, szName, szTime) < 1 || cid.AD101_GetRingIndex(nLine) < 1) {
                            //not found
                        }

                        //set null
                    }
                    break;

                    default:
                        break;
                }
            }
            break;

            case MCU_BACKDIGIT: {
                String szDialDigit = new String();
                cid.AD101_GetDialDigit(nLine, szDialDigit);
                out.println("Dial digit: " + szDialDigit);
            }
            break;

            case MCU_BACKCOLLATERAL: {
                String szDialDigit = new String();

                cid.AD101_GetCollateralDialDigit(nLine, szDialDigit);
                out.println("DialDigit : " + szDialDigit);
            }
            break;

            case MCU_BACKDEVICE: {
                String szCPUVersion = new String();

                //enable
                cid.AD101_GetCPUVersion(nLine, szCPUVersion);
                out.println("Version: " + szCPUVersion);

            }
            break;

            case MCU_BACKPARAM: {
                AD101DEVICEPARAMETER tagParameter = new AD101DEVICEPARAMETER();

                cid.AD101_GetParameter(nLine, tagParameter);

                out.println(tagParameter.nRingOn);
                out.println(tagParameter.nRingOff);
                out.println(tagParameter.nHookOn);
                out.println(tagParameter.nHookOff);
                out.println(tagParameter.nStopCID);
                out.println(tagParameter.nNoLine);
            }
            break;

            case MCU_BACKCPUID: {
                String szCPUID = new String();

                cid.AD101_GetCPUID(nLine, szCPUID);

                //cpu id
                out.println("CPUID : " + szCPUID);

            }
            break;

            case MCU_BACKMISSED: {
                out.println("Missed Call");
            }
            break;

            case MCU_BACKTALK: {
                String strTalk;
                strTalk = String.format("{0:D2}", iParam) + "S";
                out.println(strTalk);
            }
            break;

            default:
                break;
        }
    }

    @Override
    public void stop() throws Exception {
        super.stop(); //To change body of generated methods, choose Tools | Templates.
        cid.AD101_FreeDevice();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

}
