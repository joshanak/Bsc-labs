
import java.io.*;
import java.nio.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class SintaksniAnalizator {
	public static Stack<String> stog = new Stack<String>();
	public static List<String> ulaz = new ArrayList<String>();
	public static List<String> izlaz = new ArrayList<String>();
	public static int razina; //pokazuje na kojoj sam razini
	public static int trenutni; //pokazuje trenutni index ulaza
	public static boolean zastavica = true;
	public static String err = null;
	public static void greska(int trenutni) {
		if(zastavica == true) {
		if(trenutni >= (ulaz.size())) {
			err = "err kraj";
		}
		else {
			err = "err " + ulaz.get(trenutni);
		}
	}
		
	}
	
	public static String tip(int trenutni) {
		if(trenutni<=(ulaz.size()-1)){
		return ulaz.get(trenutni).split(" ")[0];
		}
		return "  ";
	}
	
	public static String concat(String ulaz) {
		for(int i =0; i<razina; i++) {
			ulaz = " " + ulaz;
		}
		return ulaz;
	}
	
	public static void program() {
		izlaz.add(concat("<program>"));
		razina++;
		lista_naredbi();
	}
	
	
	public static void lista_naredbi() {
		izlaz.add(concat("<lista_naredbi>"));
		String leksjedinka = tip(trenutni);
		
		razina++;
		if((leksjedinka.contentEquals("IDN")) || (leksjedinka.contentEquals("KR_ZA"))) {
			naredba();
			razina--;
			if(zastavica == false) return;
			lista_naredbi();
			razina--;
		}
		//ako mi je sljedeci kr_az ili mi je kraj niza moze epsilon prijelaz
		else if((leksjedinka.contentEquals("KR_AZ")) || trenutni>=(ulaz.size()-1)){
			izlaz.add(concat("$"));
		}
		//inace greska
		else {
			greska(trenutni);
			zastavica = false;
		}
	}
	
	public static void naredba() {
		izlaz.add(concat("<naredba>"));
		razina++;
		String leksjedinka = tip(trenutni);
		if(zastavica == false) return;
		if(leksjedinka.contentEquals("IDN")) {
			naredba_pridruzivanja();
			razina--;
		}
		else if(leksjedinka.contentEquals("KR_ZA")) { 
			za_petlja();
			razina--;
		}
		else { //inace greska
			greska(trenutni);
			zastavica = false;
			return;
		}
		
	}
	
	public static void naredba_pridruzivanja() {
		izlaz.add(concat("<naredba_pridruzivanja>"));
		razina++;
		String leksjedinka = tip(trenutni);
		if(zastavica == false) return;
		if(leksjedinka.contentEquals("IDN")) {
			izlaz.add(concat(ulaz.get(trenutni)));
			trenutni++;
			leksjedinka = tip(trenutni);
			if(leksjedinka.contentEquals("OP_PRIDRUZI")) {
				izlaz.add(concat(ulaz.get(trenutni)));
				trenutni++;
				E();
				razina--;
			}
			else {
				greska(trenutni);
				zastavica = false;
				return;
			}
		}	
		else {
			greska(trenutni);
			zastavica = false;
			return;
		}
	}
	
	
	public static void za_petlja() {
		izlaz.add(concat("<za_petlja>"));
		razina++;
		String leksjedinka = tip(trenutni);
		if(zastavica == false) return;
		if(leksjedinka.contentEquals("KR_ZA")) {
			izlaz.add(concat(ulaz.get(trenutni)));
			trenutni++;	
			if(tip(trenutni).contentEquals("IDN")) {
				izlaz.add(concat(ulaz.get(trenutni)));
				trenutni++;	
				if(tip(trenutni).contentEquals("KR_OD")) {
					izlaz.add(concat(ulaz.get(trenutni)));
					trenutni++;	
					E();
					if(zastavica == false) return;
					razina--;
					if(tip(trenutni).contentEquals("KR_DO")) {
						izlaz.add(concat(ulaz.get(trenutni)));
						trenutni++;	
						E();
						if(zastavica == false) return;
						razina--;
						lista_naredbi();
						if(zastavica == false) return;
						razina--;
						if(tip(trenutni).contentEquals("KR_AZ")) {
							izlaz.add(concat(ulaz.get(trenutni)));
							trenutni++;	
						}
						else {
							greska(trenutni);
							zastavica = false;
							return;
						}
					}
					else {
						greska(trenutni);
						zastavica = false;
						return;
					}
					
				}
				else {
					greska(trenutni);
					zastavica = false;
					return;
				}
			}
			else {
				greska(trenutni);
				zastavica = false;
				return;
			}
		}

		
	}
	public static void E() {
		izlaz.add(concat("<E>"));
		razina++;
		String leksjedinka = tip(trenutni);
		if(zastavica == false) return;
		if(leksjedinka.contentEquals("IDN") || leksjedinka.contentEquals("BROJ") || leksjedinka.contentEquals("OP_PLUS") || leksjedinka.contentEquals("OP_MINUS") 
			|| leksjedinka.contentEquals("L_ZAGRADA")) {
			T();
			razina--;
			if(zastavica == false) return;
			E_lista();
			razina--;
		}
		else {
			greska(trenutni);
			zastavica = false;
			return;
		}
	}
	public static void E_lista() {
		izlaz.add(concat("<E_lista>"));
		razina++;
		String leksjedinka = tip(trenutni);
		if(zastavica == false) return;
		if(leksjedinka.contentEquals("OP_PLUS")) {
			izlaz.add(concat(ulaz.get(trenutni)));
			trenutni++;
			E();
			razina--;
		}
		else if(leksjedinka.equals("OP_MINUS")) {
			izlaz.add(concat(ulaz.get(trenutni)));
			trenutni++;
			E();
			razina--;
		}
		else if(leksjedinka.contentEquals("IDN") || leksjedinka.contentEquals("KR_ZA") || leksjedinka.contentEquals("KR_DO") || leksjedinka.contentEquals("KR_AZ") 
				|| tip(trenutni).contentEquals("D_ZAGRADA") ||  trenutni>=(ulaz.size()-1)) {
			izlaz.add(concat("$"));
		}
		else {
			greska(trenutni);
			zastavica = false;
			return;
		}
		
	}
	public static void T() {
		izlaz.add(concat("<T>"));
		razina++;
		String leksjedinka = tip(trenutni);
		if(zastavica == false) return;
		if(leksjedinka.contentEquals("IDN") || leksjedinka.equals("BROJ") || leksjedinka.contentEquals("OP_PLUS") || leksjedinka.contentEquals("OP_MINUS") 
				|| leksjedinka.contentEquals("L_ZAGRADA")){
			P();
			razina--;
			T_lista();
			razina--;
		}
		else {
			greska(trenutni);
			zastavica = false;
			return;
		}
	}
	public static void T_lista() {
		izlaz.add(concat("<T_lista>"));
		razina++;
		String leksjedinka = tip(trenutni);
		if(zastavica == false) return;
		if(leksjedinka.contentEquals("OP_PUTA")) {
			izlaz.add(concat(ulaz.get(trenutni)));
			trenutni++;
			T();
			razina--;
		}
		else if(leksjedinka.contentEquals("OP_DIJELI")) {
			izlaz.add(concat(ulaz.get(trenutni)));
			trenutni++;
			T();
			razina--;
		}
		else if(leksjedinka.contentEquals("IDN") || leksjedinka.contentEquals("KR_ZA") || leksjedinka.contentEquals("KR_AZ") || leksjedinka.contentEquals("KR_DO") 
				|| leksjedinka.contentEquals("OP_PLUS") || leksjedinka.contentEquals("OP_MINUS") || leksjedinka.contentEquals("D_ZAGRADA")
				|| trenutni>=(ulaz.size()-1)) {
			izlaz.add(concat("$"));
		}
		else {
			greska(trenutni);
			zastavica = false;
			return;
		}
		
	}
	public static void P() {
		izlaz.add(concat("<P>"));
		razina++;
		String leksjedinka = tip(trenutni);
		if(zastavica == false) return;
		if(leksjedinka.contentEquals("OP_PLUS")) {
			izlaz.add(concat(ulaz.get(trenutni)));
			trenutni++;
			P();
			razina--;
		}
		else if(leksjedinka.contentEquals("OP_MINUS")) {
			izlaz.add(concat(ulaz.get(trenutni)));
			trenutni++;
			P();
			razina--;
		}
		else if(leksjedinka.contentEquals("IDN")) {
			izlaz.add(concat(ulaz.get(trenutni)));
			trenutni++;
		}
		else if(leksjedinka.contentEquals("BROJ")) {
			izlaz.add(concat(ulaz.get(trenutni)));
			trenutni++;
		}
		else if(leksjedinka.contentEquals("L_ZAGRADA")) {
			izlaz.add(concat(ulaz.get(trenutni)));
			trenutni++;
			E();
			razina--;
			if(tip(trenutni).contentEquals("D_ZAGRADA")) {
				izlaz.add(concat(ulaz.get(trenutni)));
				trenutni++;
			}
			else {
				greska(trenutni);
				zastavica = false;
				return;
			}
		}
		else {
			greska(trenutni);
			zastavica = false;
		}
	}
	
	public static void ispis(){
		if(err != null) {
			System.out.println(err);
		}
		else{
			for(int i=0; i<izlaz.size(); i++) {
			System.out.println(izlaz.get(i));
		}
		}
	}
	public static void main(String[] args) throws IOException {
		
		Scanner scanner = new Scanner(System.in);
		BufferedReader citac = new BufferedReader(new InputStreamReader(System.in));
		razina = 0;
		trenutni = 0;
		
		String linija;
		while ((linija=citac.readLine()) != null){
			if(linija.isEmpty()) break;
			ulaz.add(linija);
		}
		
		program();
		ispis();
	}

}
