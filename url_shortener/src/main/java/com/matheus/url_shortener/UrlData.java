package com.matheus.url_shortener;

import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UrlData {
    private String originalUrl;
    private long expirationTime;
}
