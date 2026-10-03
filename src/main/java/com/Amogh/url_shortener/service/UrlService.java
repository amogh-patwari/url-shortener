package com.Amogh.url_shortener.service;


import com.Amogh.url_shortener.entity.Url;
import com.Amogh.url_shortener.exception.InvalidShortCodeException;
import com.Amogh.url_shortener.exception.UrlNotFoundException;
import com.Amogh.url_shortener.repository.UrlRepository;
import com.Amogh.url_shortener.util.Base62Encoder;
import org.springframework.stereotype.Service;

@Service
public class UrlService {

    private final UrlRepository urlRepository;

    public UrlService(UrlRepository urlRepository){
        this.urlRepository=urlRepository;
    }

    public String shorten(String originalUrl){
        Url url=new Url();
        url.setOriginalUrl(originalUrl);
        url=urlRepository.save(url);
        long savedId=url.getId();

        String shortenCode= Base62Encoder.encode(savedId);

        url.setShortCode(shortenCode);
        url=urlRepository.save(url);
        return shortenCode;

    }

    public String resolve(String shortCode){
        if(!Base62Encoder.isValidBase62(shortCode)){
            throw new InvalidShortCodeException("invalid shortCode format : "+shortCode);

        }
        Url url=urlRepository.findByShortCode(shortCode).orElseThrow(()-> new UrlNotFoundException("No URL for short code : "+shortCode));
        return url.getOriginalUrl();

    }




}
