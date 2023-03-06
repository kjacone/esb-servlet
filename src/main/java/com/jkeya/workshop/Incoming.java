package com.jkeya.workshop;


import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;

import lombok.Data;



@Entity
@Data
public class Incoming {
	@Id
	@GeneratedValue
	private Long id;

	@NotNull
	@Size(min = 3, max = 50)
	private String name;

	private String otherName;

	@NotNull
	@Positive
	private Integer level;



	private String picture;

	@Column(columnDefinition = "TEXT")
	private String powers;
}
