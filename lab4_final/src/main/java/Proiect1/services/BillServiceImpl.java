package Proiect1.services;

import Proiect1.domain.Bill;
import Proiect1.domain.User;
import Proiect1.dtos.BillDTO;
import Proiect1.exceptions.ItemNotFound;
import Proiect1.repositories.BillRepository;
import Proiect1.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BillServiceImpl implements BillService {

    private static final Logger logger = LoggerFactory.getLogger(BillServiceImpl.class);

    private final UserRepository userRepository;
    private final BillRepository billRepository;

    BillServiceImpl(UserRepository userRepository, BillRepository billRepository){
        this.userRepository = userRepository;
        this.billRepository = billRepository;
    }

    @Override
    public BillDTO createBill(Long userId, BillDTO billDTO) {
        logger.debug("Attempting to create bill for userId={}", userId);
        User user = userRepository.findById(userId)
                .orElseThrow(()  -> {
                    logger.warn("User with id={} not found", userId);
                    return new ItemNotFound("User");
                });

        Bill bill = new Bill();
        bill.setBillName(billDTO.getBillName());
        bill.setAmount(billDTO.getAmount());
        bill.setNextDueDate(billDTO.getNextDueDate());
        bill.setDescription(billDTO.getDescription());
        bill.setUser(user);

        billRepository.save(bill);
        logger.info("Bill created successfully with id={} for userId={}", bill.getId(), userId);
        return convertToDTO(bill);
    }

    @Override
    public List<BillDTO> getUserBills(Long userId) {
        logger.debug("Retrieving bills for userId={}", userId);
        return billRepository.findByUserId(userId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public BillDTO updateBill(Long billId, BillDTO billDTO) {
        logger.debug("Updating bill with id={}", billId);
        Bill bill = billRepository.findById(billId)
                .orElseThrow(() -> {
                    logger.warn("Bill with id={} not found", billId);
                    return new ItemNotFound("Bill");
                });

        bill.setBillName(billDTO.getBillName());
        bill.setAmount(billDTO.getAmount());
        bill.setNextDueDate(billDTO.getNextDueDate());
        bill.setDescription(billDTO.getDescription());

        billRepository.save(bill);
        logger.info("Bill with id={} updated successfully", billId);
        return convertToDTO(bill);
    }

    @Override
    public void deleteBill(Long billId) {
        logger.debug("Deleting bill with id={}", billId);
        Bill bill = billRepository.findById(billId)
                .orElseThrow(() -> {
                    logger.warn("Bill with id={} not found", billId);
                    return new ItemNotFound("Bill");
                });
        billRepository.delete(bill);
        logger.info("Deleted bill with id={}", billId);
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
