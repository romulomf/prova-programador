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
}