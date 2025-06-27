package com.matheus.url_shortener;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BodyResponseDTO {
    private String originalUrl;
    private String expirationTime;
}
