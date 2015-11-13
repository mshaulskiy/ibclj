package ibclj;

import com.ib.controller.ApiController;
import com.ib.controller.NewTickType;
import com.ib.controller.Types;

/**
 * Created by mathieu on 12/11/2015.
 */
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
