package com.Amogh.url_shortener.dto;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor

public class UrlRequest {
    private String url;
}

//We use the DTO's(Data transfer object) to eliminate the mass assignment risk and also to ensure decoupling
//here in our case the mass assignment risk : is accepting id , shortcode with the long url , but which will be
//which will be set by the machine , not the input by the user( we shall only take the long url)