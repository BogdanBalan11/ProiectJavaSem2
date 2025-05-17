package Proiect1.services;

import Proiect1.dtos.BillDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface BillService {
    BillDTO createBill(Long userId, BillDTO billDTO);
    List<BillDTO> getUserBills(Long userId);
    BillDTO updateBill(Long billId, BillDTO billDTO);
    void deleteBill(Long billId);
}
