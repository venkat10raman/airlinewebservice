package com.gainitgyan.airlinewebservice.validation;

import java.util.Arrays;
import java.util.List;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

import org.apache.commons.lang3.StringUtils;

public class FlightMfdByValidator implements ConstraintValidator<FlightMfdBy, String> {
	
	List<String> approvedMfd = Arrays.asList("aaa", "bbb", "ccc");
	
	@Override
	public boolean isValid(String value, ConstraintValidatorContext context) {
		return (StringUtils.isNotBlank(value) && approvedMfd.contains(value));
	}

}
