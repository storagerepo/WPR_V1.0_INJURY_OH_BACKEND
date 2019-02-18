package com.deemsys.project.pdfparser;

public class PDFParserOneProperty {

	private String propertyName;
	private double x;
	private double y;
	private double width;
	private double height;
	public String getPropertyName() {
		return propertyName;
	}
	public void setPropertyName(String propertyName) {
		this.propertyName = propertyName;
	}
	public double getX() {
		return x;
	}
	public void setX(double x) {
		this.x = x;
	}
	public double getY() {
		return y;
	}
	public void setY(double y) {
		this.y = y;
	}
	public double getWidth() {
		return width;
	}
	public void setWidth(double width) {
		this.width = width;
	}
	public double getHeight() {
		return height;
	}
	public void setHeight(double height) {
		this.height = height;
	}
	public PDFParserOneProperty(String propertyName, double x, double y, double width, double height) {
		super();
		this.propertyName = propertyName;
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}
	public PDFParserOneProperty() {
		super();
		// TODO Auto-generated constructor stub
	}
}
