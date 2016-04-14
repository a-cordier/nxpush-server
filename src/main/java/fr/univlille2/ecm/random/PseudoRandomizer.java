package fr.univlille2.ecm.random;


import java.util.Random;

/**
 * 
 * @author acordier
 * @date 13 avr. 2016
 */
public class PseudoRandomizer {

	private Random random;
	
	private static PseudoRandomizer instance;
	
	private PseudoRandomizer(){
		random = new Random();
	}

	
	public char getRandomChar(){
		return(char)(random.nextInt(26)+'a');
	}
	
	public char[] getRandomWord(int length) throws Exception{
		if(length <= 0){
			throw new Exception("length is not correct");
		}
		char[] out = new char[length];
		for(int i = 0; i < length; i++){
			out[i]=getRandomChar();
		}
		return out;
	}
	
	public String getRandomString(int length) throws Exception{
		return new String(getRandomWord(length));
	}
	
	public static PseudoRandomizer getInstance(){
		synchronized (PseudoRandomizer.class) {
			if(instance==null){
				instance = new PseudoRandomizer();
			}
		}
		return instance;
	}
}
