package com.Amogh.url_shortener.controller;


import com.Amogh.url_shortener.dto.UrlRequest;
import com.Amogh.url_shortener.dto.UrlResponse;
import com.Amogh.url_shortener.service.UrlService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api")
public class UrlController{

    private final UrlService urlService;

    public UrlController(UrlService urlService){
        this.urlService=urlService;
    }

    @PostMapping("/shorten")
    public ResponseEntity<?> shorten(@RequestBody UrlRequest request){
        String shortCode=urlService.shorten(request.getUrl());
        UrlResponse response=new UrlResponse(shortCode);
        return new ResponseEntity<>(response , HttpStatus.CREATED);
    }

    @GetMapping("/{code}")
    public ResponseEntity<?> redirect(@PathVariable String code){
        String originalUrl=urlService.resolve(code);
        URI uri=URI.create(originalUrl);

        return ResponseEntity.status(HttpStatus.FOUND).location(uri).build();

    }

}
