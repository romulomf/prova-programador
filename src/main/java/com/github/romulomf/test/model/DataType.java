package com.github.romulomf.test.model;

import org.apache.commons.lang3.StringUtils;

public enum DataType {

	SALESMAN("001"),
	CUSTOMER("002"),
	SALE("003");

	private String code;

	private DataType(String code) {
		this.code = code;
	}

	public static DataType parse(String code) {
		DataType dataType = null;
		for (DataType currentDataType : values()) {
			if (StringUtils.equals(currentDataType.code, code)) {
				dataType = currentDataType;
				break;
			}
		}
		if (dataType == null) {
			throw new IllegalArgumentException(String.format("Invalid data type code %1$s does not exist.", code));
		}
		return dataType;
	}

	@Override
	public String toString() {
		return code;
	}
}