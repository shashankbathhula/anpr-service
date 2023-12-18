package com.avn.anprService.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EmailData {
    private String email;
    private String token;
    private String messageBody;
    private String subject;
    private String name;
    private String hyperLink;
}
