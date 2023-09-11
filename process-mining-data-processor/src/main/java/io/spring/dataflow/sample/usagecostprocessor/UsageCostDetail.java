package io.spring.dataflow.sample.usagecostprocessor;

public class UsageCostDetail {

	public UsageCostDetail() { }

	private Event userId;

	private double callCost;

	private double dataCost;

	public Event getUserId() {
		return userId;
	}

	public void setUserId(Event userId) {
		this.userId = userId;
	}

	public double getCallCost() {
		return callCost;
	}

	public void setCallCost(double callCost) {
		this.callCost = callCost;
	}

	public double getDataCost() {
		return dataCost;
	}

	public void setDataCost(double dataCost) {
		this.dataCost = dataCost;
	}

	public String toString() {
		return "{\"userId\": \""+this.getUserId() + "\", \"callCost\": \""+ this.getCallCost()+"\", \"dataCost\": \"" + this.getDataCost()+ "\" }";
	}
}
