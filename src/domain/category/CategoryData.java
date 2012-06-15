package domain.category;

/*
 * Set Category data
 */

public class CategoryData {
	private String name;	//category name
	private int type;	//0 is main, 1 is sub
	private int parent;	//child belong parent
	private int cashflowType; //0:expense, 1:income
	
	public int getCashflowType() {
		return cashflowType;
	}

	public void setCashflowType(int cashflowType) {
		this.cashflowType = cashflowType;
	}

	public int getParent() {
		return parent;
	}

	public void setParent(int parent) {
		this.parent = parent;
	}

	public CategoryData() {
		name = "";
		type = 0;
		parent = 0;
		cashflowType = 0;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public int getType() {
		return type;
	}
	
	public void setType(int type) {
		this.type = type;
	}	
}
