package com.enigma.wms.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/hello")
@PreAuthorize("hasRole('ADMIN')")
public class HelloController {

    @GetMapping
    public String hello() {
        return "Hello";
    }
}
