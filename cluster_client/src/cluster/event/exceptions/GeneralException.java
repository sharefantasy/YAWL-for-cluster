package cluster.event.exceptions;

/**
 * Created by fantasy on 2015/8/22.
 */
public class GeneralException extends Exception {
    private String msg;
    public GeneralException(String msg){
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }
}
