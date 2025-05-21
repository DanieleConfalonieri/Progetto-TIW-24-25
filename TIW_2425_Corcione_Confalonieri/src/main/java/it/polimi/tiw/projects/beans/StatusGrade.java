package it.polimi.tiw.projects.beans;

public enum StatusGrade {
	NON_INSERITO(0), INSERITO(1), PUBBLICATO(2), 
	RIFIUTATO(3), VERBALIZZATO(4);
	
	private final int value;
	
	StatusGrade(int value) {
		this.value = value;
	}
	
	public static StatusGrade getStatusGradeFromInt(int value) {
		switch (value) {
		case 0:
			return StatusGrade.NON_INSERITO;
		case 1:
			return StatusGrade.INSERITO;
		case 2:
			return StatusGrade.PUBBLICATO;
		case 3:
			return StatusGrade.RIFIUTATO;
		case 4:
			return StatusGrade.VERBALIZZATO;
		}
		return null;
		
	}
	
	public int getValue() {
		return value;
	}
}
