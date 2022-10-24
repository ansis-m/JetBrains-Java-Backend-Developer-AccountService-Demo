package com.example.demo.payslip;

import com.example.demo.user.User;

import java.util.List;

public interface PaySlipService {

    List<PaySlip> getAll();
    void save(PaySlip paySlip);
    PaySlip findById(Long id);
    void deleteById(Long id);
    void deleteAll();
}
