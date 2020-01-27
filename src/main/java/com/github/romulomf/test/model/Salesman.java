package com.github.romulomf.test.model;

import org.apache.commons.lang3.StringUtils;

public class Salesman implements SaleData {

	private String socialNumber;

	private String name;

	private double salary;

	public Salesman() {
		// Default constructor
	}

	public Salesman(String socialNumber, String name, double salary) {
		this();
		this.socialNumber = socialNumber;
		this.name = name;
		this.salary = salary;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Salesman) {
			Salesman other = (Salesman) obj;
			return StringUtils.equalsIgnoreCase(socialNumber, other.socialNumber) && StringUtils.equalsIgnoreCase(name, other.name);
		}
		return false;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int hashCode = 0;
		hashCode += (socialNumber != null ? socialNumber.hashCode() : 1) * prime;
		hashCode += (name != null ? name.hashCode() : 1) * prime;
		return hashCode;
	}

	public String getSocialNumber() {
		return socialNumber;
	}

	public void setSocialNumber(String socialNumber) {
		this.socialNumber = socialNumber;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getSalary() {
		return salary;
	}

	public void setSalary(double salary) {
		this.salary = salary;
	}

	@Override
	public DataType getDataType() {
		return DataType.SALESMAN;
	}
}