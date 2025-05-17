package Proiect1.services;

import Proiect1.domain.Bill;
import Proiect1.domain.User;
import Proiect1.dtos.BillDTO;
import Proiect1.exceptions.ItemNotFound;
import Proiect1.repositories.BillRepository;
import Proiect1.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BillServiceImpl implements BillService {

    private final UserRepository userRepository;
    private final BillRepository billRepository;

    BillServiceImpl(UserRepository userRepository, BillRepository billRepository){
        this.userRepository = userRepository;
        this.billRepository = billRepository;
    }

    @Override
    public BillDTO createBill(Long userId, BillDTO billDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ItemNotFound("User"));

        Bill bill = new Bill();
        bill.setBillName(billDTO.getBillName());
        bill.setAmount(billDTO.getAmount());
        bill.setNextDueDate(billDTO.getNextDueDate());
        bill.setDescription(billDTO.getDescription());
        bill.setUser(user);

        billRepository.save(bill);
        return convertToDTO(bill);
    }

    @Override
    public List<BillDTO> getUserBills(Long userId) {
        return billRepository.findByUserId(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public BillDTO updateBill(Long billId, BillDTO billDTO) {
        Bill bill = billRepository.findById(billId)
                .orElseThrow(() -> new ItemNotFound("Bill"));

        bill.setBillName(billDTO.getBillName());
        bill.setAmount(billDTO.getAmount());
        bill.setNextDueDate(billDTO.getNextDueDate());
        bill.setDescription(billDTO.getDescription());

        billRepository.save(bill);

        return convertToDTO(bill);
    }

    @Override
    public void deleteBill(Long billId) {
        Bill bill = billRepository.findById(billId)
                .orElseThrow(() -> new ItemNotFound("Bill"));
        billRepository.delete(bill);
    }

    private BillDTO convertToDTO(Bill bill) {
        BillDTO dto = new BillDTO();
        dto.setId(bill.getId());
        dto.setBillName(bill.getBillName());
        dto.setAmount(bill.getAmount());
        dto.setNextDueDate(bill.getNextDueDate());
        dto.setDescription(bill.getDescription());
        dto.setUserId(bill.getUser().getId());
        return dto;
    }
}
