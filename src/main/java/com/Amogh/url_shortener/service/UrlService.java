package com.Amogh.url_shortener.service;


import com.Amogh.url_shortener.entity.Url;
import com.Amogh.url_shortener.exception.InvalidShortCodeException;
import com.Amogh.url_shortener.exception.UrlNotFoundException;
import com.Amogh.url_shortener.repository.UrlRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.hashids.Hashids;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.time.Duration;


@Slf4j

@Service
public class UrlService {

    private final UrlRepository urlRepository;
    private final StringRedisTemplate redisTemplate;

    @Value("${hashids.salt}")
    private String salt;

    private Hashids hashids;

    public UrlService(UrlRepository urlRepository , StringRedisTemplate redisTemplate){
        this.urlRepository=urlRepository;
        this.redisTemplate=redisTemplate;
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
        String shortcode=hashids.encode(savedId);
        return shortcode;

    }


    //Redis is a cache-aside layer. If it fails, reads fall back to Postgres and writes to the cache are skipped, so a cache outage degrades performance but not availability

    public String resolve(String shortCode){
        long[] decode=hashids.decode(shortCode);

        if(decode.length==0){
            throw new InvalidShortCodeException("Invalid short code : "+shortCode);
        }

        String key="url:"+shortCode;

        //checking the cache first
        String cached=getFromCache(key);
        if(cached!=null){
            return cached;
        }

        //if not found go with postgres
        Url url=urlRepository.findById(decode[0]).orElseThrow(()-> new UrlNotFoundException("No url found for the shortcode : "+shortCode));

        //store this in cache for next time and set the expiry time as 24 hours
        putInCache(key , url.getOriginalUrl());
        return url.getOriginalUrl();



    }

    private String getFromCache(String key){
        try{
            return redisTemplate.opsForValue().get(key);
        }catch(DataAccessException e){
            log.warn("Redis read failed , falling back to DB : {}" , e.getMessage());
            return null;
        }
    }

    private void putInCache(String key , String value){
        try{
            redisTemplate.opsForValue().set(key , value , Duration.ofHours(24));

        }catch(DataAccessException e){
            log.warn("Redis write failed , continuing without caching {}", e.getMessage());
        }
    }




}
