package com.laan.wordfinder.controller;

import com.laan.wordfinder.util.PathUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RestController
@RequestMapping(PathUtil.WELCOME)
@Slf4j
public class IndexController {

    @GetMapping
    public ResponseEntity<Object> welcome() {
        String message = "Welcome to Word Finder";
        log.info(message);
        return new ResponseEntity<>(Collections.singletonMap("message", message), HttpStatus.OK);
    }
}
