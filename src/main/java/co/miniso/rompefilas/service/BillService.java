package co.miniso.rompefilas.service;

import java.util.List;

import co.miniso.rompefilas.db2.model.Bill;
import org.springframework.stereotype.Service;

import co.miniso.rompefilas.db2.repository.BillRepository;

@Service
public class BillService {

	private final BillRepository billRepository;

	public BillService(BillRepository billRepository) {

        this.billRepository = billRepository;
    }

	public List<Object[]> buscarPorId(String numAtCard) {
		return billRepository.findByNumAtCard(numAtCard);
	}

}
