package com.example.demo.Controllers;

import com.example.demo.SecurityEvents.EventService;
import com.example.demo.payslip.PaySlip;
import com.example.demo.payslip.PaySlipService;
import com.example.demo.payslip.PaySlipServiceImp;
import com.example.demo.securityConfig.pCheck;
import com.example.demo.user.Salary;
import com.example.demo.user.User;
import com.example.demo.user.UserService;
import com.example.demo.user.UserServiceImp;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;

@RestController
public class AccountantRestController {

    @Autowired
    UserService userService;

    @Autowired
    EventService eventService;

    @Autowired
    PaySlipService paySlipService;

    @Secured({ "ROLE_USER", "ROLE_ACCOUNTANT"})
    @GetMapping("api/empl/payment")
    public ResponseEntity Payment(@RequestParam(required=false) String period, Authentication auth){


        System.out.println("\n\n******PAYMENT********\n" + auth.getName());

        User user = userService.findByEmail(auth.getName());
        if(user != null) {
            ArrayList<PaySlip> payslips = new ArrayList<>(user.getPaySlips());
            Collections.reverse(payslips);
            if(period == null)
                return  new ResponseEntity(payslips, HttpStatus.OK);
            else {
                for(PaySlip p : payslips){
                    if(p.getDate().equals(period))
                        return new ResponseEntity(p, HttpStatus.OK);
                }
            }
            return  new ResponseEntity(Map.of("timestamp", LocalDate.now(), "status", 400, "error", "Bad Request", "path", "/api/empl/payment", "message", "message"), HttpStatus.BAD_REQUEST);
        }
        else
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }

    @Secured({"ROLE_ACCOUNTANT"})
    @PostMapping ("api/acct/payments")
    public ResponseEntity Payments(@RequestBody (required = false) ArrayList<Salary> salary){

        System.out.println("\n\n********INSIDE POST api/acct/payments*******\n\n");

        if(salary == null)
            System.out.println("\n\ns == null\n\n");

        String message = Salary.parsePayments(salary);
        if (message.length() > 5)
            return new ResponseEntity(Map.of("timestamp",LocalDate.now(), "error", "Bad Request", "path", "/api/acct/payments", "message", message, "status", 400), HttpStatus.BAD_REQUEST);

        for(Salary s : salary) {
            try{
                User user = userService.findByEmail(s.getEmployee());
                if(user == null) {
                    System.out.println("No such user found!!!!");
                    return new ResponseEntity(Map.of("timestamp",LocalDate.now(), "error", "Bad Request", "path", "/api/acct/payments", "message", "no such user", "status", 400), HttpStatus.BAD_REQUEST);
                }

                PaySlip paySlip = new PaySlip(s, user);
                if (user.getMonths().contains(paySlip.getPeriod()))
                    return new ResponseEntity(Map.of("timestamp",LocalDate.now(), "error", "Bad Request", "path", "/api/acct/payments", "message", "Duplicated entry in payment list", "status", 400), HttpStatus.BAD_REQUEST);
                paySlipService.save(paySlip);

                user.addPayslip(paySlip);
                user.addMonth(paySlip.getPeriod());
                userService.save(user);
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
        return new ResponseEntity(Map.of("status", "Added successfully!"), HttpStatus.OK);
    }

    @Secured({"ROLE_ACCOUNTANT"})
    @PutMapping ("api/acct/payments")
    public ResponseEntity EditPayments(@RequestBody (required = false) Salary s){

        System.out.println("\n\n****INSIDE PUT api/acct/payments******\n\n");

        if(s == null)
            System.out.println("\n\ns == null\n\n");

        String error = Salary.parsePayments(s);
        User user = userService.findByEmail(s.getEmployee());

        if(error.length() > 0 || user == null) {
            return new ResponseEntity(Map.of("timestamp",LocalDate.now(), "error", "Bad Request", "path", "/api/acct/payments", "message", error, "status", 400), HttpStatus.BAD_REQUEST);
        }
        PaySlip newPaySlip = new PaySlip(s, user);

        List<PaySlip> allPaySlips = user.getPaySlips();
        for(PaySlip p : allPaySlips) {
            if(p.getPeriod().equals(newPaySlip.getPeriod())) {
                newPaySlip.setId(p.getId());
                paySlipService.save(newPaySlip);
                return new ResponseEntity(Map.of("status", "Updated successfully!"), HttpStatus.OK);

            }
        }
        user.addPayslip(newPaySlip);
        paySlipService.save(newPaySlip);
        userService.save(user);
        return new ResponseEntity(Map.of("status", "Updated successfully!"), HttpStatus.OK);
    }

    private boolean uniqueEmail(String email) {
        return !userService.exists(email);
    }

}
