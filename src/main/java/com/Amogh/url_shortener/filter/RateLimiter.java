package com.Amogh.url_shortener.filter;


import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Component
public class RateLimiter extends OncePerRequestFilter {
    private final Map<String , Bucket> buckets=new ConcurrentHashMap<>();


    @Override
    protected  void doFilterInternal(HttpServletRequest request , HttpServletResponse response , FilterChain chain) throws ServletException, IOException{

        //to check the type of request(get/post)
        String type=request.getMethod().equals("POST") ?"POST":"GET";

        //to create the key for ip address(1 for post , 1 for request)
        String key=request.getRemoteAddr()+":"+type;

        //create a bucket if it is not present in the hashmap
        Bucket bucket=buckets.computeIfAbsent(key ,k->createBucket(type));

        //to check if the bucket is working
        ConsumptionProbe probe=bucket.tryConsumeAndReturnRemaining(1);

        //if yes continue to the servlet and controller
        if(probe.isConsumed()){
            chain.doFilter(request , response);

        }

        //else don't let it pass and throw 429 as response
        else{
            long waitSeconds= TimeUnit.NANOSECONDS.toSeconds(probe.getNanosToWaitForRefill())+1;
            response.setStatus(429);
            response.setHeader("Retry-After" , String.valueOf(waitSeconds));
            response.setContentType("text/plain");
            response.getWriter().write("Too many requests , Try again in "+waitSeconds+"seconds");
        }
    }


    private Bucket createBucket(String type){

        //Capacity -> post(10) ,get(100)
        int capacity=type.equals("POST")?10 : 100;

        //builder with the capacity
        return Bucket.builder().addLimit(limit->limit.capacity(capacity).refillGreedy(capacity , Duration.ofMinutes(1))).build();
    }
}
