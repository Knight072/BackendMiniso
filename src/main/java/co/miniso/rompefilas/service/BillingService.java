package co.miniso.rompefilas.service;

import co.miniso.rompefilas.db1.model.BillApp;
import co.miniso.rompefilas.db1.model.InvoiceDetailApp;
import co.miniso.rompefilas.db1.repository.BillAppRepository;
import co.miniso.rompefilas.db1.repository.InvoiceDetailAppRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BillingService {

    private final BillAppRepository billAppRepository;
    private final InvoiceDetailAppRepository invoiceDetailAppRepository;

    @Autowired
    public BillingService(InvoiceDetailAppRepository invoiceDetailAppRepository, BillAppRepository billAppRepository) {

        this.invoiceDetailAppRepository = invoiceDetailAppRepository;
        this.billAppRepository = billAppRepository;
    }

    public void saveBill(BillApp details){
        System.out.println(details.getTipoDocumento());
        billAppRepository.save(details);
    }

}
