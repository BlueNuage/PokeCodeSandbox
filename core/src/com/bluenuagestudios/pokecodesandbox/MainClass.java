package com.bluenuagestudios.pokecodesandbox;

import java.util.ArrayList;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;

public class MainClass implements ApplicationListener {
	private float w;
	private float h;
	private SpriteBatch batch;
	private Texture maleCharacter;
	private Texture femaleCharacter;
	private Texture gameCharacter;
	TextureRegion characterRegion;
	private Texture pokemonTiles;
	TextureRegion[] characterFrames = new TextureRegion[12];
	TextureRegion[] pokemonOutsideTiles = new TextureRegion[9];
	Animation characterAnimation;
	ArrayList<Room> rooms = new ArrayList<Room>();
	Vector2 hackCamera = new Vector2(0, 0);
	Vector2 characterLocation;
	float turnWait = 0;
	final float turnLength = 0.75f; 
	Instructions instructions;
	float generalWait = 0;
	final float generalWaitLength = 100;
	boolean hasRun = false;
	boolean currentlyRunning = false;
	int currentCycle = 0;
	int moveUpNumber = 0;
	public ArrayList<String> instructionSet = new ArrayList<String>(); 
	public ArrayList<Vector2> teleportValues = new ArrayList<Vector2>();
	boolean repeat = false;
	String gender = "male";
	boolean changedGender = false;
	
	boolean completedTurn = true;
	String characterDirection = "STOPPED";
	int characterMoveAmmount = 0;
	
	Vector2 characterChordinates;
	
	Vector2 mapSize;
	
	ObjectManager objectManager;
	boolean dontWait = false;
	
	String lastCommand = "NONE";
	
	@Override
	public void create() {		
		w = Gdx.graphics.getWidth();
		h = Gdx.graphics.getHeight();
		
		batch = new SpriteBatch();
		
		maleCharacter = new Texture(Gdx.files.internal("data/lucasCharacter.png"));
		femaleCharacter = new Texture(Gdx.files.internal("data/dawnCharacter.png"));
		
		if(gender.equals("male"))
			gameCharacter = maleCharacter;
		else if(gender.equals("female"))
			gameCharacter = femaleCharacter;
		
		pokemonTiles = new Texture(Gdx.files.internal("data/pokemonTileset.png"));
		
		characterFrames[0] = new TextureRegion(gameCharacter, 50, 74, 34, 46); //Standing down
		characterFrames[1] = new TextureRegion(gameCharacter, 120, 72, 34, 50); //Walking down 1
		characterFrames[2] = new TextureRegion(gameCharacter, 190, 72, 34, 50); //Walking down 2
		characterFrames[3] = new TextureRegion(gameCharacter, 470, 74, 34, 46); //Standing up
		characterFrames[4] = new TextureRegion(gameCharacter, 540, 72, 34, 50); //Walking up 1
		characterFrames[5] = new TextureRegion(gameCharacter, 610, 72, 34, 50); //Walking up 2
		characterFrames[6] = new TextureRegion(gameCharacter, 262, 74, 34, 46); //Standing right
		characterFrames[7] = new TextureRegion(gameCharacter, 332, 72, 34, 46); //Walking right 1
		characterFrames[8] = new TextureRegion(gameCharacter, 402, 72, 34, 48); //Walking right 2
		characterFrames[9] = new TextureRegion(gameCharacter, 680, 74, 34, 46); //Standing left
		characterFrames[10] = new TextureRegion(gameCharacter, 750, 72, 34, 48); //Walking left 1
		characterFrames[11] = new TextureRegion(gameCharacter, 820, 72, 34, 46); //Walking left 2
		characterAnimation = new Animation(0.25f, characterFrames);
		
		pokemonOutsideTiles[0] = new TextureRegion(pokemonTiles, 64, 512, 64, 64); //middle
		pokemonOutsideTiles[1] = new TextureRegion(pokemonTiles, 64, 448, 64, 64); //topHorizantal
		pokemonOutsideTiles[2] = new TextureRegion(pokemonTiles, 64, 576, 64, 64); //bottomHorizantal
		pokemonOutsideTiles[3] = new TextureRegion(pokemonTiles, 0, 512, 64, 64); //leftVertical
		pokemonOutsideTiles[4] = new TextureRegion(pokemonTiles, 128, 512, 64, 64); //rightVertical
		pokemonOutsideTiles[5] = new TextureRegion(pokemonTiles, 0, 448, 64, 64); //upperLeftCorner
		pokemonOutsideTiles[6] = new TextureRegion(pokemonTiles, 128, 448, 64, 64); //upperRightCorner
		pokemonOutsideTiles[7] = new TextureRegion(pokemonTiles, 0, 576, 64, 64); //bottomLeftCorner
		pokemonOutsideTiles[8] = new TextureRegion(pokemonTiles, 128, 576, 64, 64); //bottomRightCorner
		
		mapSize = new Vector2(17, 11);
		rooms.add(new Room((int)mapSize.x, (int)mapSize.y));
		
		//characterLocation = new Vector2(Math.round(w/64)/2 * 64 - 14 , Math.round(h/64)/2 * 64 + 15);
		//characterChordinates = new Vector2((characterLocation.x + 14)/64 - 1, (characterLocation.y - 15)/64);
		
		objectManager = new ObjectManager(pokemonTiles, mapSize);
		
		characterLocation = new Vector2(0, 0);
		teleport(new Vector2(8, 5));
		
		instructions = new Instructions(this);
		characterRegion = characterFrames[0];
		
		
		
		//objectManager.addObject("POKEBALL", new Vector2(10, 10));
	}
	
	@Override
	public void dispose() {
		batch.dispose();
		gameCharacter.dispose();
	}

	public void update() {
		if(changedGender)
		{
			if(gender.equals("male"))
				gameCharacter = maleCharacter;
			else if(gender.equals("female"))
				gameCharacter = femaleCharacter;
			
			for(TextureRegion currentTextureRegion : characterFrames)
			{
				currentTextureRegion.setTexture(gameCharacter);
			}
			changedGender = false;
				
		}
		
		
		if(characterDirection.equals("DOWN"))
		{
			if(characterMoveAmmount < 64)
			{
				if(characterMoveAmmount == 0)
					characterRegion = characterFrames[0];
				else if(characterMoveAmmount == 10)
					characterRegion = characterFrames[1];
				else if(characterMoveAmmount == 32)
					characterRegion = characterFrames[2];
				else if(characterMoveAmmount == 48)
					characterRegion = characterFrames[1];
				
				characterLocation.y -= 2;
				characterMoveAmmount += 2;
			}
			else
			{
				characterRegion = characterFrames[0];
				characterMoveAmmount = 0;
				characterDirection = "STOPPED";
				completedTurn = true;
			}
		}
		
		else if(characterDirection.equals("UP"))
		{
			if(characterMoveAmmount < 64)
			{
				if(characterMoveAmmount == 0)
					characterRegion = characterFrames[3];
				else if(characterMoveAmmount == 10)
					characterRegion = characterFrames[4];
				else if(characterMoveAmmount == 32)
					characterRegion = characterFrames[5];
				else if(characterMoveAmmount == 54)
					characterRegion = characterFrames[4];
				
				characterLocation.y += 2;
				characterMoveAmmount += 2;
			}
			else
			{
				characterRegion = characterFrames[3];
				characterMoveAmmount = 0;
				characterDirection = "STOPPED";
				completedTurn = true;
			}
		}
		
		else if(characterDirection.equals("RIGHT"))
		{
			if(characterMoveAmmount < 64)
			{
				if(characterMoveAmmount == 0)
					characterRegion = characterFrames[6];
				else if(characterMoveAmmount == 10)
					characterRegion = characterFrames[7];
				else if(characterMoveAmmount == 32)
					characterRegion = characterFrames[8];
				else if(characterMoveAmmount == 48)
					characterRegion = characterFrames[7];
				
				characterLocation.x += 2;
				characterMoveAmmount += 2;
			}
			else
			{
				characterRegion = characterFrames[6];
				characterMoveAmmount = 0;
				characterDirection = "STOPPED";
				completedTurn = true;
			}
		}
		
		else if(characterDirection.equals("LEFT"))
		{
			if(characterMoveAmmount < 64)
			{
				if(characterMoveAmmount == 0)
					characterRegion = characterFrames[9];
				else if(characterMoveAmmount == 10)
					characterRegion = characterFrames[10];
				else if(characterMoveAmmount == 32)
					characterRegion = characterFrames[11];
				else if(characterMoveAmmount == 48)
					characterRegion = characterFrames[10];
				
				characterLocation.x -= 2;
				characterMoveAmmount += 2;
			}
			else
			{
				characterRegion = characterFrames[9];
				characterMoveAmmount = 0;
				characterDirection = "STOPPED";
				completedTurn = true;
			}
		}
		
	}
	
	public void instructionsTech(){
		if(instructionSet.size() > 0 && (turnWait >= turnLength || dontWait) && completedTurn)
		{
			objectManager.update();
			dontWait = false;
			
			if(instructionSet.get(0) == "UP")
			{
				if(characterChordinates.y < mapSize.y - 1 && !objectUp())
				{
					completedTurn = false;
					characterDirection = "UP";
					characterChordinates.y++;
					lastCommand = "MOVEMENT";
				}
				else
				{
					characterRegion = characterFrames[3];
				}
				
			}
			else if(instructionSet.get(0) == "DOWN")
			{
				if(characterChordinates.y > 0 && !objectDown())
				{
					completedTurn = false;
					characterDirection = "DOWN";
					characterChordinates.y--;
					lastCommand = "MOVEMENT";
				}
				else
				{
					characterRegion = characterFrames[0];
				}
			}
			else if(instructionSet.get(0) == "LEFT")
			{
				if(characterChordinates.x > 0 && !objectLeft())
				{
					completedTurn = false;
					characterDirection = "LEFT";
					characterChordinates.x--;
					lastCommand = "MOVEMENT";
				}
				else
				{
					characterRegion = characterFrames[9];
				}
			}
			else if(instructionSet.get(0) == "RIGHT")
			{
				
				if(characterChordinates.x < mapSize.x - 1 && !objectRight())
				{
					completedTurn = false;
					characterDirection = "RIGHT";
					characterChordinates.x++;
					lastCommand = "MOVEMENT";
				}
				else
				{
					characterRegion = characterFrames[6];
				}
			}
			else if(instructionSet.get(0) == "TELEPORT")
			{
				teleport(teleportValues.get(0));
				teleportValues.remove(0);
				lastCommand = "MOVEMENT";
			}
			else if(instructionSet.get(0) == "POKEBALL")
			{
				objectManager.addObject("POKEBALL", new Vector2(characterChordinates.x, characterChordinates.y));
				dontWait = true;
				lastCommand = "DROPOBJECT";
			}
			else if(instructionSet.get(0) == "POTTEDPLANT")
			{
				objectManager.addObject("POTTEDPLANT", new Vector2(characterChordinates.x, characterChordinates.y));
				dontWait = true;
				lastCommand = "DROPOBJECT";
			}
			else if(instructionSet.get(0) == "BUSH")
			{
				objectManager.addObject("BUSH", new Vector2(characterChordinates.x, characterChordinates.y));
				dontWait = true;
				lastCommand = "DROPOBJECT";
			}
			else if(instructionSet.get(0) == "FLOWER")
			{
				objectManager.addObject("FLOWER", new Vector2(characterChordinates.x, characterChordinates.y));
				dontWait = true;
				lastCommand = "DROPOBJECT";
			}
			else if(instructionSet.get(0) == "TREE")
			{
				objectManager.addObject("TREE", new Vector2(characterChordinates.x, characterChordinates.y));
				dontWait = true;
				lastCommand = "DROPOBJECT";
			}
			else if(instructionSet.get(0) == "REMOVEUP")
			{
				characterRegion = characterFrames[3]; //Turn character up
				objectManager.removeObject(new Vector2(characterChordinates.x, characterChordinates.y + 1));
				dontWait = true;
				lastCommand = "REMOVEOBJECT";
			}
			else if(instructionSet.get(0) == "REMOVEDOWN")
			{
				characterRegion = characterFrames[0]; //Turn character down
				objectManager.removeObject(new Vector2(characterChordinates.x, characterChordinates.y - 1));
				dontWait = true;
				lastCommand = "REMOVEOBJECT";
			}
			else if(instructionSet.get(0) == "REMOVELEFT")
			{
				characterRegion = characterFrames[9]; //Turn character left
				objectManager.removeObject(new Vector2(characterChordinates.x - 1, characterChordinates.y));
				dontWait = true;
				lastCommand = "REMOVEOBJECT";
			}
			else if(instructionSet.get(0) == "REMOVERIGHT")
			{
				characterRegion = characterFrames[6]; //Turn character right
				objectManager.removeObject(new Vector2(characterChordinates.x + 1, characterChordinates.y));
				dontWait = true;
				lastCommand = "REMOVEOBJECT";
			}
			else if(instructionSet.get(0) == "CHECKUP")
			{
				objectUp();
				dontWait = true;
				lastCommand = "CHECK";
			}
			
			instructionSet.remove(0);
			turnWait = 0;
		}
		else
		{
			if(lastCommand.equals("MOVEMENT") || lastCommand.equals("REMOVEOBJECT") || lastCommand.equals("CHECK"))
				objectManager.update();
		}
	}
	
	@Override
	public void render() {	
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		update();
		turnWait += Gdx.graphics.getDeltaTime();
		
		instructionsTech();

		
		batch.begin();
		
		for(int i = 0; i < rooms.get(0).height; i++)
		{
			for(int j = 0; j < rooms.get(0).width; j++)
			{
				batch.draw(pokemonOutsideTiles[rooms.get(0).roomArray[i][j]], i * 64 - hackCamera.x, j * 64 - hackCamera.y);
			}
		}
		
		objectManager.draw(batch);
		
		batch.draw(characterRegion, characterLocation.x, characterLocation.y);
		
		batch.end();
		
		if(hasRun == false && currentlyRunning == false && currentCycle >= 3)
		{
			instructions.instructions();
			if(repeat == false)
				hasRun = true;
		}
		else if(currentCycle < 3)
			currentCycle++;
	}

	
	public void teleport(Vector2 location)
	{
		if(locationInBounds(location) && locationEmpty(location))
		{
			characterChordinates = location;
			characterLocation.set(new Vector2(characterChordinates.x * 64 + 14, characterChordinates.y * 64 + 15));
		}
		
	}
	
	@Override
	public void resize(int width, int height) {
		w = width;
		h = height;
		Matrix4 batchMatrix = new Matrix4();
		batchMatrix.setToOrtho2D(0, 0, w, h);
		batch.setProjectionMatrix(batchMatrix);
		//characterLocation = new Vector2(Math.round(w/48)/2 * 48 , Math.round(h/48)/2 * 48);
	}
	
	public boolean objectUp()
	{
		PlaceableObject currentObject = objectManager.objects[(int)characterChordinates.x][(int)characterChordinates.y + 1];
		if(currentObject != null && currentObject.collideable && !currentObject.remove)
		{
			return true;
		}
		return false;
	}
	
	public boolean objectDown()
	{
		PlaceableObject currentObject = objectManager.objects[(int)characterChordinates.x][(int)characterChordinates.y - 1];
		if(currentObject != null && currentObject.collideable && !currentObject.remove)
		{
			return true;
		}
		return false;
	}
	
	public boolean objectLeft()
	{
		PlaceableObject currentObject = objectManager.objects[(int)characterChordinates.x - 1][(int)characterChordinates.y];
		if(currentObject != null && currentObject.collideable && !currentObject.remove)
		{
			return true;
		}
		return false;
	}
	
	public boolean objectRight()
	{
		PlaceableObject currentObject = objectManager.objects[(int)characterChordinates.x + 1][(int)characterChordinates.y];
		if(currentObject != null && currentObject.collideable && !currentObject.remove)
		{
			return true;
		}
		return false;
	}
	
	public boolean locationEmpty(Vector2 location)
	{
		PlaceableObject currentObject = objectManager.objects[(int)location.x][(int)location.y];
		if(currentObject != null && currentObject.collideable && !currentObject.remove)
		{
			return false;
		}
		return true;
	}
	
	public boolean locationInBounds(Vector2 location)
	{
		if(location.x < mapSize.x - 1 && location.x > 0 && location.y < mapSize.y - 1 && location.y > 0)
			return true;
		else
			return false;
	}
	
	@Override
	public void pause() {
	}

	@Override
	public void resume() {
	}
}

