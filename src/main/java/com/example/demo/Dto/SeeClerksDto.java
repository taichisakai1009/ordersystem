package com.example.demo.Dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class SeeClerksDto {

	private Integer clerkId;
	
	private String name;
	
    private Integer clerkNumber;

    private String password;

    private String mailAddress;

    private String tel;

    private LocalDate startDate;
    
    private String roleName;
}
