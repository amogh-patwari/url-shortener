package com.Amogh.url_shortener.service;


import com.Amogh.url_shortener.entity.Url;
import com.Amogh.url_shortener.exception.InvalidShortCodeException;
import com.Amogh.url_shortener.exception.UrlNotFoundException;
import com.Amogh.url_shortener.repository.UrlRepository;
import jakarta.annotation.PostConstruct;
import org.hashids.Hashids;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class UrlService {

    private final UrlRepository urlRepository;

    @Value("${hashids.salt}")
    private String salt;

    private Hashids hashids;

    public UrlService(UrlRepository urlRepository){
        this.urlRepository=urlRepository;
    }

    @PostConstruct
    public void init(){
        hashids=new Hashids(salt,6);
    }

    public String shorten(String originalUrl){
        Url url=new Url();
        url.setOriginalUrl(originalUrl);
        url=urlRepository.save(url);
        long savedId=url.getId();

        String shortenCode= hashids.encode(savedId);

        url.setShortCode(shortenCode);
        url=urlRepository.save(url);
        return shortenCode;

    }

    public String resolve(String shortCode){
        long[] decode=hashids.decode(shortCode);

        if(decode.length==0){
            throw new InvalidShortCodeException("Invalid short code : "+shortCode);
        }

        long id=decode[0];
        Url url=urlRepository.findById(id).orElseThrow(()->new UrlNotFoundException("No URL found for the short code : "+shortCode));
        return url.getOriginalUrl();
    }




}
