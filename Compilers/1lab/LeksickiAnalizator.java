import java.util.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class LeksickiAnalizator {
	
	
	public static ArrayList<String> kljucniSimbol =  new ArrayList<String>
	(Arrays.asList("za", "od", "do", "az"));
	
	public static ArrayList<String> operatorRijec = new ArrayList<String>
	(Arrays.asList("OP_PRIDRUZI", "OP_PLUS", "OP_MINUS", "OP_PUTA", "OP_DIJELI"));
	
	public static ArrayList<String> operatorSimbol = new ArrayList<String>
	(Arrays.asList("=", "+", "-", "*", "/"));
	
	public static int brojred;
	
	public static String[] rijeci;
	
	public static boolean flag;
	
	
	public static void printIDN(String str) {
		if(!str.isEmpty())
		System.out.println("IDN" + " " + brojred + " " + str);
		
		
		
	}
	public static void printBroj(String str) {
		if(!str.isEmpty())
		System.out.println("BROJ" + " " + brojred + " " + str);
		
	}
	
	//provjerava je li trenutni simbol operator i ako je ispisuje
	public static boolean checkOperator(String str, boolean vrsta, String ispis) {
		for(int i = 0; i<operatorSimbol.size(); i++) { //prolazi sve operatore i ispisuje
			if(str.equals(operatorSimbol.get(i))) {
			if(vrsta == false && !(ispis.isEmpty())) printBroj(ispis);
			if(vrsta == true  && !(ispis.isEmpty())) printIDN(ispis);
			System.out.println(operatorRijec.get(i).toUpperCase() + " " + brojred + " " + str);
			return true;
			}
		}
		return false;
	}
	
	
	
	
	//provjerava je li trenutna rijec kljucna rijec = radi
	public static boolean checkKeyWord(String str) {
		for(String simbol: kljucniSimbol) { 
			if(str.equals(simbol)) {
			System.out.println("KR_" + simbol.toUpperCase() + " " + brojred + " " + simbol);
			return true;
			}
		}
		return false;
	}
	
	
	public static void func(String linija) {
		
			rijeci = linija.trim().split("\\s+"); //odvaja sve rijeci nevezano za kolicinu razmaka
			
			
			for(String rijec : rijeci) { //prolazi kroz sve rijeci u liniji
				int pocetak = 0;
				char trenutni;
				boolean vrsta = true; // 0 je broj 1 je IDN
				
				StringBuilder sb = new StringBuilder();
				
				flag = false;
				
			//nije potrebno	if(rijec.equals("//")) return; //ako je jednak komentaru preskoci
				
				if(checkKeyWord(rijec)==true) continue;
				
				
				for(int i = 0, n = rijec.length() ; i < n ; i++) { 
					
					trenutni = rijec.charAt(i);
					
					//provjera ako je komentar
					if((i+1) < n) {
						if(trenutni == 47 && rijec.charAt(i+1) == 47) {
							flag = true;
							break;
						}
					}
					
					
					//NAPRAVI PROVJERU ZAGRADA
					if(trenutni == '(' || trenutni == ')') {
						if(vrsta == false) printBroj(sb.toString());
						if(vrsta == true) printIDN(sb.toString());
						pocetak = i+1; 
						sb.setLength(0); // clear string buffer
						if(trenutni == '(')
						System.out.println("L_ZAGRADA" + " " + brojred + " " + "(");
						else
							System.out.println("D_ZAGRADA" + " " + brojred + " " + ")");
						continue;
					}
					
					if(checkOperator(String.valueOf(trenutni), vrsta, sb.toString())){
						  pocetak=i+1; 
			              sb.setLength(0); // clear string buffer
			              continue;
			          }
					
					if(pocetak == i && !(Character. isLetter(trenutni))) {
						vrsta = false;
						sb.append(trenutni);
						
					}
					else if(vrsta == false && !(Character. isLetter(trenutni))) {
						sb.append(trenutni);
						
					}
					if(vrsta == false && (Character. isLetter(trenutni))) {
						printBroj(sb.toString());
						sb.setLength(0); // clear string buffer
						vrsta = true;
						pocetak = i+1;
						
					}
					//moram napravit appendanje za IDN + moram skuzit ako je char operator da mi se ispise trenutni string builder
					
					if((Character. isLetter(trenutni)) && vrsta == true) {
						sb.append(trenutni);
					}
					
					//ako je kraj rijeci ispisi broj ili IDN
					if(((i+1) == n) && (sb.length()!= 0)) {
						if(vrsta == true) printIDN(sb.toString());
						if(vrsta == false) printBroj(sb.toString());
					}
					
				}
				
				if(flag == true) break;
			}
				
	}
	
	
	
	public static void main(String[] args) throws IOException {
	
			
		BufferedReader citac = new BufferedReader(new InputStreamReader(System.in));
			
			brojred = 1;
			String linija;
			
			while ((linija=citac.readLine()) != null){
				func(linija);
				brojred++;
			}
			
	}
}



