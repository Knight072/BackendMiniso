package co.miniso.rompefilas.service;

import co.miniso.rompefilas.db1.model.BillApp;
import co.miniso.rompefilas.db3.model.Client;

public class BillRequest {
    private BillApp bill;
    private Client client;

    public BillApp getBill() {
        return bill;
    }

    public Client getClient() {
        return client;
    }
}
