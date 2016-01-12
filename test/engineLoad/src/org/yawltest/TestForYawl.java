package org.yawltest;
import org.apache.log4j.Logger;
import org.jdom2.Element;
import org.yawlfoundation.yawl.elements.data.YParameter;
import org.yawlfoundation.yawl.engine.YSpecificationID;
import org.yawlfoundation.yawl.engine.interfce.WorkItemRecord;
import org.yawlfoundation.yawl.engine.interfce.interfaceB.InterfaceBWebsideController;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class TestForYawl extends InterfaceBWebsideController {
    private String _handle = null;


    private static final Logger _logger = Logger.getLogger(TestForYawl.class);


    static Timer timer = null;
    static int lastDistributeCount=0;
    static int distributeCount=0;
    static {
        timer = new Timer();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                System.out.println("engine load: "+String.valueOf(distributeCount-lastDistributeCount));
                lastDistributeCount=distributeCount;

            }
        };
        timer.schedule(task,0, 1000);
    }

    public TestForYawl() {
    }

    public void handleEnabledWorkItemEvent(WorkItemRecord wir) {
        try {
            if(!this.connected()) {
                this._handle = this.connect(this.engineLogonName, this.engineLogonPassword);
            }
            distributeCount++;
            wir = this.checkOut(wir.getID(), this._handle);

            String ioe = this.updateStatus(wir);

            this.checkInWorkItem(wir.getID(), wir.getDataList(), this.getOutputData(wir.getTaskID(), ioe), (String)null, this._handle);
        } catch (Exception var3) {
            var3.printStackTrace();
        }

    }

    public void handleCancelledWorkItemEvent(WorkItemRecord workItemRecord) {
    }

    public YParameter[] describeRequiredParams() {
    //    YParameter[] params = new YParameter[]{new YParameter((YDecomposition)null, 0), null};
        YParameter[] params = new YParameter[2];


        params[0]=new YParameter(null,YParameter._INPUT_PARAM_TYPE);
        params[0].setDataTypeAndName("unsignedLong","count",XSD_NAMESPACE);
        params[0].setDocumentation("count");

        params[1]=new YParameter(null,YParameter._OUTPUT_PARAM_TYPE);
        params[1].setDataTypeAndName("unsignedLong","count",XSD_NAMESPACE);
        params[1].setDocumentation("count");


        return params;
    }

    private String updateStatus(WorkItemRecord wir) {
        String msg = this.getCountMsg(wir);
        String result;
        if(msg != null) {
            result = this.updateStatus(msg);
        } else {
            result = "";
        }

        return result;
    }

    private String updateStatus(String msg) {

        String result;
        try {
            result = String.valueOf(Integer.parseInt(msg)+1);
        } catch (Exception var5) {
            result = var5.getMessage();
        }

        return result;
    }

    private String getCountMsg(WorkItemRecord wir) {
        String result = null;
        String status = this.getDataValue(wir.getDataList(), "count");
        if(status != null) {
            result=status;
        }

        return result;
    }

    private String getDataValue(Element data, String name) {
        return data != null?data.getChildText(name):null;
    }

    private Element getOutputData(String taskName, String data) {
        Element output = new Element(taskName);
        Element result = new Element("count");
        result.setText(data);
        output.addContent(result);
        return output;
    }

    private boolean connected() throws IOException {
        return this._handle != null && this.checkConnection(this._handle);
    }

    private String launchCase(YSpecificationID specID,String caseData)throws IOException{

        String caseID=_interfaceBClient.launchCase(specID,caseData,null,_handle);
        if(!successful(caseID)){
            return "heheda!";
        }
        return caseID;
    }


}
