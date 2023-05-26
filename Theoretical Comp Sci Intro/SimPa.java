import java.io.*;
import java.util.*;

public class SimPa {
				
	public static ArrayList<String> stanja;
	public static ArrayList<String> ulazniZnakovi;
	public static ArrayList<String> stogovniZnakovi;
	public static ArrayList<String> prihvatljivaStanja;
	public static ArrayList<String> ulazniNizovi;
	public static String pocetnoStanje;
	
	public static Character pocetniZnakStoga;
	public static String pocetniStog;
	
	static Map<String, String> funkcijePrijelaza;
	static Stack<Character> stog;
	
	public static char vrhStoga;
	
	
	//sluzi za dobivanje kljuca iz mape funkcije Prijelaza
	public static String trojkaPrijelaza(String stanje,char znak, char stog) {
		String kljuc = stanje +"," + znak + ","+ stog;
		return kljuc;
	}
	
	//sluzi za dobivanje vrijednosti iz mape funkcijePrijelaza
	public static ArrayList<String> dvojkaPrijelaza(String str) {
		ArrayList<String> dvojka = new ArrayList<>(Arrays.asList(str.split(",")));
		return dvojka;
	}

	
	
	
	public static void main(String[] args) throws IOException {
			//Scanner scanner = new Scanner(System.in);
			
			BufferedReader citac = new BufferedReader(new InputStreamReader(System.in));
			
			ulazniNizovi = new ArrayList<>(Arrays.asList(citac.readLine().split("\\|")));
			
			
			stanja = new ArrayList<>(Arrays.asList(citac.readLine().split(",")));
	     
			ulazniZnakovi = new ArrayList<>(Arrays.asList(citac.readLine().split(",")));
			
			stogovniZnakovi = new ArrayList<>(Arrays.asList(citac.readLine().split(",")));
	        
		    prihvatljivaStanja = new ArrayList<>(Arrays.asList(citac.readLine().split(",")));
	     
		    pocetnoStanje = citac.readLine();
		    
		    pocetniStog = citac.readLine();
		    
		    pocetniZnakStoga = pocetniStog.charAt(0);
		   
		    
		    String temp, prviDio, drugiDio;
		    funkcijePrijelaza = new TreeMap<>();
		    while ((temp=citac.readLine()) != null){
		    	
		    	 	if(temp.isBlank()) break;
					String[] prijelaz = temp.split("->");
					prviDio = prijelaz[0];
					drugiDio = prijelaz[1];
					funkcijePrijelaza.put(prviDio, drugiDio);
					
					
		        } 	
		    
		   
		    
		    for (String object: ulazniNizovi) {
		       simPaSimulator(object);
		    }
		    
	    
	    }
	
	
	
		public static void simPaSimulator(String ulazniNiz) {
			 stog = new Stack<>(); //inicijalizacija stoga
			 int flag = 0;
			 stog.push(pocetniZnakStoga); //stavljanje prvog elementa na stog
			 String trenutnoStanje = pocetnoStanje;
			 List<String> vrijednost;
			 String kljuc;
			 String epsilonKljuc;
			 
			 String[] ulazniZnakovi = ulazniNiz.split(","); //dobivam polje stringova svih operatora
			 
			 
			 int i = 0;
			 System.out.print(pocetnoStanje+"#"+"K"+"|");
			 //prolaziKrozSveZnakove
			 while(i<ulazniZnakovi.length) {  
				 char ulazniZnak = ulazniZnakovi[i].charAt(0); //dobivam pojedinacni operator
				 vrhStoga ='$';
				 if(!stog.empty())  vrhStoga = stog.pop();	//poppam vrh stoga

				 
				  kljuc = trojkaPrijelaza(trenutnoStanje, ulazniZnak, vrhStoga);
				  epsilonKljuc = trojkaPrijelaza(trenutnoStanje, '$', vrhStoga);
			
				 if(funkcijePrijelaza.containsKey(kljuc)){
					  vrijednost = dvojkaPrijelaza(funkcijePrijelaza.get(kljuc));
					  
					  i++;
				 }
				 else if(funkcijePrijelaza.containsKey(epsilonKljuc)){
					  vrijednost = dvojkaPrijelaza(funkcijePrijelaza.get(epsilonKljuc));
					  
				 }
				 else {
					 System.out.print( "fail"+"|");
					 flag = 1;
					 break;
				 }
				 
				 trenutnoStanje = vrijednost.get(0);
				 
				 
				 //ako se ne stavlja novi znak stoga
				 if(vrijednost.get(1).contentEquals("$"));
				 //ako je samo jedan novi znak stoga
				 else if(vrijednost.get(1).length()==1) {
					 stog.push(vrijednost.get(1).charAt(0));
				 }
				 //ako je  vise znakova stoga
				 else { 
					 char[] lista = vrijednost.get(1).toCharArray();
				        for(int k = (lista.length-1); k >= 0; k--){
				        	
				         stog.push(lista[k]);
				        
				        } 
					/*System.out.print(trenutnoStanje+"#");
					String[] stacktrace = stog.toString().replace("[", "").replace("]","").replaceAll("\\s+","").split(",");
					for(int m=(stacktrace.length-1); m>=0; m--){
						System.out.print(stacktrace[m]);
					}
					System.out.print("|");*/
				 }
				 //ispis
				 System.out.print(trenutnoStanje+"#");
				 if(stog.empty()) System.out.print("$" + "|");
				 else {
					String[] stacktrace = stog.toString().replace("[", "").replace("]","").replaceAll("\\s+","").split(",");
					for(int m=(stacktrace.length-1); m>=0; m--){
						System.out.print(stacktrace[m]);
					}
					System.out.print("|");
				 }
			 }
			 
			 if(!stog.empty()) { epsilonKljuc = trojkaPrijelaza(trenutnoStanje, '$', stog.peek());
			 
			 }
			 else {
				 epsilonKljuc = trojkaPrijelaza(trenutnoStanje, '$', '$');
			 }
			 
			 //ako nema vise ulaznihZnakova i ali postoji epsilon prijelaz za trenutno Stanje koje nije prihvatljivo
			 while((!prihvatljivaStanja.contains(trenutnoStanje))
					&& funkcijePrijelaza.containsKey(epsilonKljuc)&&
					flag == 0) {
				
				epsilonKljuc = trojkaPrijelaza(trenutnoStanje, '$', stog.peek());
				vrijednost = dvojkaPrijelaza(funkcijePrijelaza.get(epsilonKljuc));
				trenutnoStanje = vrijednost.get(0);
				if(!stog.empty()) vrhStoga = stog.pop();
				 
				if(vrijednost.get(1).contentEquals("$"));
				 //ako je samo jedan novi znak stoga
				 else if(vrijednost.get(1).length()==1) {
					 stog.push(vrijednost.get(1).charAt(0));
				 }
				 //ako je  vise znakova stoga
				 else { 
					 char[] lista = vrijednost.get(1).toCharArray();
				        for(int k = (lista.length-1); k >= 0; k--){
				        	
				         stog.push(lista[k]);
				       } 
				 }
				//ispis  
				System.out.print(trenutnoStanje+"#");
				 if(stog.empty()) System.out.print("$" + "|");
				 else {
					String[] stacktrace = stog.toString().replace("[", "").replace("]","").replaceAll("\\s+","").split(",");
					for(int m=(stacktrace.length-1); m>=0; m--){
						System.out.print(stacktrace[m]);
					}
					System.out.print("|");
				 }
				 
				
				
				        
			 }
			 
			 
			 //proslo kroz sve znakove i na redu je zadnji ispis
			 if(prihvatljivaStanja.contains(trenutnoStanje) && flag == 0){
				 System.out.println("1");
			 }
			 else {
				 System.out.println("0");
			 }
			
		}
	
	
	}


