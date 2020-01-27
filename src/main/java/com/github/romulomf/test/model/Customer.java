package com.github.romulomf.test.model;

import org.apache.commons.lang3.StringUtils;

public class Customer implements SaleData {

	private String companyNumber;

	private String name;

	private String businessArea;

	public Customer() {
		// Default constructor
	}

	public Customer(String companyNumber, String name, String businessArea) {
		this();
		this.companyNumber = companyNumber;
		this.name = name;
		this.businessArea = businessArea;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Customer) {
			Customer other = (Customer) obj;
			return StringUtils.equalsIgnoreCase(companyNumber, other.companyNumber) && StringUtils.equalsIgnoreCase(name, other.name);
		}
		return false;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int hashCode = 0;
		hashCode += (companyNumber != null ? companyNumber.hashCode() : 1) * prime;
		hashCode += (name != null ? name.hashCode() : 1) * prime;
		return hashCode;
	}

	public String getCompanyNumber() {
		return companyNumber;
	}

	public void setCompanyNumber(String companyNumber) {
		this.companyNumber = companyNumber;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBusinessArea() {
		return businessArea;
	}

	public void setBusinessArea(String businessArea) {
		this.businessArea = businessArea;
	}

	@Override
	public DataType getDataType() {
		return DataType.CUSTOMER;
	}
}