package com.github.romulomf.test.model;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class Sale implements SaleData, Comparable<Sale> {

	private int id;

	private List<SaleItem> items;

	private String salesmanName;

	public Sale() {
		// Default constructor
	}

	public Sale(int id, List<SaleItem> items, String salesmanName) {
		this.id = id;
		this.items = items;
		this.salesmanName = salesmanName;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public List<SaleItem> getItems() {
		return items;
	}

	public void setItems(List<SaleItem> items) {
		this.items = items;
	}

	public String getSalesmanName() {
		return salesmanName;
	}

	public void setSalesmanName(String salesmanName) {
		this.salesmanName = salesmanName;
	}

	public double getTotal() {
		return items.stream().mapToDouble(SaleItem::getTotal).sum();
	}

	@Override
	public DataType getDataType() {
		return DataType.SALE;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Sale) {
			Sale other = (Sale) obj;
			return id == other.id;
		}
		return false;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		return prime * id;
	}

	@Override
	public int compareTo(Sale other) {
		Double selfSaleSum = Double.valueOf(this.getTotal());
		Double otherSaleSum = Double.valueOf(other.getTotal());
		return otherSaleSum.compareTo(selfSaleSum);
	}

	public static Sale parse(String[] data) {
		int id = Integer.parseInt(data[1]);
		String salesmanName = data[3];
		List<SaleItem> saleItems = new ArrayList<>();
		String itemData = StringUtils.substringBetween(data[2], "[", "]");
		String[] items = StringUtils.split(itemData, ',');
		for (String item : items) {
			String[] itemInfo = StringUtils.split(item, '-');
			SaleItem saleItem = SaleItem.parse(itemInfo);
			saleItems.add(saleItem);
		}
		return new Sale(id, saleItems, salesmanName);
	}
}