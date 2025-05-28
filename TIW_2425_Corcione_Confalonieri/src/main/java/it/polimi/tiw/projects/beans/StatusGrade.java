package it.polimi.tiw.projects.beans;

public enum StatusGrade {
	NON_INSERITO(0), INSERITO(1), PUBBLICATO(2), 
	RIFIUTATO(3), VERBALIZZATO(4);
	
	private final int value;
	
	StatusGrade(int value) {
		this.value = value;
	}
	
	public static StatusGrade getStatusGradeFromString(String value) {
		switch (value) {
		case "NON INSERITO":
			return StatusGrade.NON_INSERITO;
		case "INSERITO":
			return StatusGrade.INSERITO;
		case "PUBBLICATO":
			return StatusGrade.PUBBLICATO;
		case "RIFIUTATO":
			return StatusGrade.RIFIUTATO;
		case "VERBALIZZATO":
			return StatusGrade.VERBALIZZATO;
		}
		return null;
		
	}
	
	public int getValue() {
		return value;
	}
}
