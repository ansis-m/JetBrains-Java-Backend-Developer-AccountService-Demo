package com.example.demo.SecurityEvents;

import com.example.demo.payslip.PaySlip;

import java.util.List;

public interface EventService {

    List<Event> getAll();
    void save(Event event);
    Event findById(Long id);
    void deleteById(Long id);
    void deleteAll();
}
