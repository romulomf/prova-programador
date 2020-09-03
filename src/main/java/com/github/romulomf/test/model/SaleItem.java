package com.github.romulomf.test.model;

public class SaleItem {

	private int id;

	private int quantity;

	private double price;

	public SaleItem() {
		// Default constructor
	}

	public SaleItem(int id, int quantity, double price) {
		this();
		this.id = id;
		this.quantity = quantity;
		this.price = price;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getQuantity() {
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public double getTotal() {
		return quantity * price;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof SaleItem) {
			SaleItem other = (SaleItem) obj;
			return id == other.id;
		}
		return false;
	}

	@Override
	public int hashCode() {
		final int prime = 13;
		return prime * id;
	}

	public static SaleItem parse(String[] data) {
		int itemCode = Integer.parseInt(data[0]);
		int itemQuantity = Integer.parseInt(data[1]);
		double itemPrice = Double.parseDouble(data[2]);
		return new SaleItem(itemCode, itemQuantity, itemPrice);
	}
}