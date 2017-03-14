package applica.aj.ui;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by bimbobruno on 14/03/2017.
 */

public class StoreDefinitions {

    private List<String> stores = new ArrayList<>();

    public StoreDefinitions addStore(String store) {
        stores.add(store);
        return this;
    }

    public StoreDefinitions removeStore(String store) {
        stores.remove(store);
        return this;
    }

    public List<String> getStores() {
        return stores;
    }

    public void setStores(List<String> stores) {
        this.stores = stores;
    }
}
