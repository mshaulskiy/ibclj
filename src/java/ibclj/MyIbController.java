package ibclj;

import com.ib.controller.ApiConnection;
import com.ib.controller.ApiController;

import java.util.ArrayList;

/**
 * Created by mshaulskiy on 04/11/2015.
 */
public class MyIbController implements ApiController.IConnectionHandler {

    private static ApiController controller;

    public MyIbController() {

        controller = new ApiController( this, new ApiConnection.ILogger() {
            public void log(String s) {  // inLogger
                System.out.print(s);
            }
        }, new ApiConnection.ILogger() {
            public void log(String s) {  // outLogger
                System.out.print(s);
            }
        } );
    }

    @Override
    public void connected() {
        System.out.println("We are connected!");
    }

    @Override
    public void disconnected() {
        System.out.println("We are disconnected!");
    }

    @Override
    public void accountList(ArrayList<String> list) {

    }

    @Override
    public void error(Exception e) {

    }

    @Override
    public void message(int id, int errorCode, String errorMsg) {

    }

    @Override
    public void show(String string) {

    }

    public void connect(String host, int port, int clientId){
        controller.connect(host, port, clientId,"");
    }

    public void disconnect() {
        controller.disconnect();
    }

    public static void main(String[] args){
        MyIbController c = new MyIbController();
        c.connect("localhost", 7497, 5);

        try {
            Thread.sleep(10000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        c.disconnect();

    }


}
