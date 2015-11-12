package ibclj;

import com.ib.controller.*;

import java.util.ArrayList;

/**
 * This class is used as an illustration of the IB Api connection and tick subscription methods calls.
 * It's not intend to be used for anything useful other than simple example
 *
 * Created by mshaulskiy on 04/11/2015.
 *
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
        controller.connect(host, port, clientId);
    }

    public void disconnect() {
        controller.disconnect();
    }



    private NewContract createContract() {
        //"VXX", Types.SecType.STK, "", "0.0", Types.Right.None, "", "SMART", "USD", "", ""}
        final NewContract contract = new NewContract();
        contract.symbol("VXX");
        contract.secType(Types.SecType.STK);
        contract.expiry("");
        contract.strike(0.0d);
        contract.right(Types.Right.None);
        contract.multiplier("");
        contract.exchange("SMART");
        contract.currency("USD");
        contract.localSymbol("");
        contract.tradingClass("");

        return contract;

    }

    public void reqTopMktData(NewContract contract, String string, boolean b, ApiController.ITopMktDataHandler row) {
        Object[] params = {contract, string, b, row};
        System.out.println("reqTopMktData Contract:{}, String:{}, Boolean:{}, Row:{}" + params);
        controller.reqTopMktData( contract,  string,  b,  row);
    }

    public void cancelTopMktData(ApiController.ITopMktDataHandler row) {
        System.out.println("CancelTopMktData Row:{}" + row);
        controller.cancelTopMktData( row);

    }


    public class DataRow extends ApiController.TopMktDataAdapter {

        @Override public void tickPrice(NewTickType tickType, double price, int canAutoExecute) {
            switch( tickType) {
                case BID:
                    System.out.println("Bid: " + price);
                    break;
                case ASK:
                    System.out.println("Ask: " + price);
                    break;
                case LAST:
                    System.out.println("Last " + price);
                    break;
                case CLOSE:
                    System.out.println("Close " + price);
                    break;
            }
        }

        @Override public void tickSize( NewTickType tickType, int size) {
            switch( tickType) {
                case BID_SIZE:
                    System.out.println("BidSize: " + size);
                    break;
                case ASK_SIZE:
                    System.out.println("AskSize: " + size);
                    break;
                case VOLUME:
                    System.out.println("Volume: " + size);
                    break;
            }
        }

        @Override public void tickString(NewTickType tickType, String value) {
            switch( tickType) {
                case LAST_TIMESTAMP:
                    System.out.println("LastTime: " + Long.parseLong( value) * 1000);
                    break;
            }
        }

        @Override public void marketDataType(Types.MktDataType marketDataType) {
            System.out.println("Frozen: " + (marketDataType == Types.MktDataType.Frozen));
        }


    }


    public static void main(String[] args){
        MyIbController c = new MyIbController();
        c.connect("localhost", 7497, 5);

        NewContract contract = c.createContract();
        DataRow row = c.new DataRow();

        c.reqTopMktData(contract, "", false, row);

        try {
            Thread.sleep(10000000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        c.cancelTopMktData(row);

        c.disconnect();

    }


}
