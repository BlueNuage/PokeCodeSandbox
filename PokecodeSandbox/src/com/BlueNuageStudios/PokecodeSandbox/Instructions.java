package com.BlueNuageStudios.PokecodeSandbox;

public class Instructions extends BackendInstructions{
	
	//This is where your settings will go:
	public Instructions(MainClass mainClass){
		//IGNORE THIS:
		getClass(mainClass); 
		
		//Settings here:
		setRepeat(false);
		setGender("male");
	}
	
	//Put variables here:
	int example = 0;

	//Write instructions here:
	public void instructions()
	{
		moveUp();
		dropTree();
		moveRight();
		dropTree();
		moveDown();
		dropTree();
		moveLeft();
		dropTree();
		moveDown();
		
	}

}

