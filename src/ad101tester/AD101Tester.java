/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ad101tester;

import javafx.application.Application;
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
    int MCU_BACKID = 0x07;	// Return Device ID
    int MCU_BACKSTATE = 0x08;	// Return Device State
    int MCU_BACKCID = 0x09;		// Return Device CallerID
    int MCU_BACKDIGIT = 0x0A;	// Return Device Dial Digit
    int MCU_BACKDEVICE = 0x0B;	// Return Device Back Device ID
    int MCU_BACKPARAM = 0x0C;	// Return Device Paramter
    int MCU_BACKCPUID = 0x0D;	// Return Device CPU ID
    int MCU_BACKCOLLATERAL = 0x0E;		// Return Collateral phone dialed
    int MCU_BACKDISABLE = 0xFF;		// Return Device Init
    int MCU_BACKENABLE = 0xEE;
    int MCU_BACKMISSED = 0xAA;		// Missed call 
    int MCU_BACKTALK = 0xBB;		// Start Talk
    
    int HKONSTATEPRA = 0x01; // hook on pr+  HOOKON_PRA
    int HKONSTATEPRB = 0x02;  // hook on pr-  HOOKON_PRR
    int HKONSTATENOPR = 0x03;  // have pr  HAVE_PR
    int HKOFFSTATEPRA = 0x04;   // hook off pr+  HOOKOFF_PRA
    int HKOFFSTATEPRB = 0x05;  // hook off pr-  HOOKOFF_PRR
    int NO_LINE = 0x06; // no line  NULL_LINE
    int RINGONSTATE = 0x07;  // ring on  RING_ON
    int RINGOFFSTATE = 0x08;  // ring off RING_OFF
    int NOHKPRA = 0x09; // NOHOOKPRA= 0x09, // no hook pr+
    int NOHKPRB = 0x0a; // NOHOOKPRR= 0x0a, // no hook pr-
    int NOHKNOPR = 0x0b; // NOHOOKNPR= 0x0b, // no hook no pr
    
    int WM_USBLINEMSG = 1024 + 180;
    
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
            if(cid.AD101_InitDevice(0)){
                System.out.println("success");
                btn.setText("Initialized");
                              
                //open all connected devices
                total.setText("Total: " + cid.AD101_GetDevice());
                
                //setting call back function
                cid.AD101_SetEventCallbackFun((int iLine, int iEvent, int iParam) -> {
                    System.out.println("Incoming from line: " + iLine + " event: " + Integer.toHexString(iEvent) + " param: " + Integer.toHexString(iParam));
                });
            } else {
                System.out.println("Try again.");
            }
        });
        
        StackPane root = new StackPane();
        VBox vb = new VBox();
        vb.getChildren().add(btn);
        vb.getChildren().add(btnCount);
        vb.getChildren().add(total);
        root.getChildren().add(vb);
        
        Scene scene = new Scene(root, 300, 250);
        
        primaryStage.setTitle("CID Tester");
        primaryStage.setScene(scene);
        primaryStage.show();
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
