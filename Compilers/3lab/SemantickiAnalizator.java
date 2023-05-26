

import java.io.*;
import java.util.*;

public class SemantickiAnalizator {

	public static List<String> unutarnja;
	public static List<List<String>> vanjska = new ArrayList<>(); 
	public static List<String> ispis;
	public static List<String> ulaz = new ArrayList<>();
	public static boolean flag = false;
	
	//provjeri lijevu stranu -- provjeri desnu stranu
	public static void pridruzivanje(int num) {
		
		//provjeri nalazi li se u tablici definiranih
		if(Check(ulaz.get(num))!=-1) {
			//System.out.println("nadeno "+ ulaz.get(num));
		}
		//ako ne dodaj u zadnju tablicu scopea
		else {
			unutarnja = vanjska.get(vanjska.size()-1);
			unutarnja.add(ulaz.get(num));
			vanjska.remove(vanjska.size()-1);
			vanjska.add(unutarnja);
			//System.out.println(vanjska);
		}
		for(int i = num+1; i<ulaz.size() ; i++) {
			if(ulaz.get(i).contentEquals("<naredba_pridruzivanja>") || ulaz.get(i).contentEquals("<za_petlja>")
				|| ulaz.get(i).contains("KR_AZ") || ulaz.get(i).contains("KR_ZA") ) {
				break;
			}
			else {
				int x=Check(ulaz.get(i));
				
				if(x!=-1) {
					System.out.println(ulaz.get(i).split(" ")[1]+" "+x+" "+ulaz.get(i).split(" ")[2]);
				}
				else {
				  flag = true;
				  System.out.println("err "+ulaz.get(i).split(" ")[1]+" "+ulaz.get(i).split(" ")[2]);
				  return;
				}
			}
		}
	}
	public static void zaPetlja(int num) {
		unutarnja = new ArrayList<String>();
		if(ulaz.get(num).contains("KR_ZA")) num++;
		unutarnja.add(ulaz.get(num));
		vanjska.add(unutarnja);
		int poc = num;
		num++;
		//System.out.println("prvi ident "+ulaz.get(num));
		if(ulaz.get(num).contains("KR_OD"))num++;
		for(int i=num; !ulaz.get(i).contains("KR_DO"); i++) {
			num++;
			//System.out.println("ulaz  " + ulaz.get(i));
			int x=Check(ulaz.get(i));
			if(ulaz.get(i).contains(ulaz.get(poc).split(" ")[2]) ||  x==-1) {
				  flag = true;
				  System.out.println("err "+ulaz.get(i).split(" ")[1]+" "+ulaz.get(i).split(" ")[2]);
				  return;
			}
			else {
				System.out.println(ulaz.get(i).split(" ")[1]+" "+x+" "+ulaz.get(i).split(" ")[2]);
			}
			
		}
		num++;
		
		if(ulaz.get(num).contentEquals("<naredba_pridruzivanja>")||ulaz.get(num).contentEquals("<za_petlja>")) return;
		for(int i=num;  i<ulaz.size(); i++) {
			if(ulaz.get(i).contentEquals("<naredba_pridruzivanja>")||ulaz.get(i).contentEquals("<za_petlja>")) return;
			int x=Check(ulaz.get(i));
			if(ulaz.get(i).contains(ulaz.get(poc).split(" ")[2]) ||  x==-1) {
				  flag = true;
				  System.out.println("err "+ulaz.get(i).split(" ")[1]+" "+ulaz.get(i).split(" ")[2]);
				  return;
			}
			else {
				System.out.println(ulaz.get(i).split(" ")[1]+" "+x+" "+ulaz.get(i).split(" ")[2]);
			}
		}
		
	}
	//pretrazi sve scopeove i vrati redni broj gdje je nadeno
	public static int Check(String str) {
		if(flag == true) return -1;
		String trazeno = str.split(" ")[2];
		for(int i = vanjska.size()-1; i>=0; i--) {
			List<String> unutarnja = vanjska.get(i);
			for(int j = 0; j<=unutarnja.size()-1; j++) {
				if(unutarnja.get(j).contains(trazeno)) {
					int ans = Integer.parseInt(unutarnja.get(j).split(" ")[1]);
					
					return ans;
				}
			}
		}
		
		return -1;
	}
	
	
	
	
	public static void SemAnal() throws IOException {	
		/*r(int i=0; i<ulaz.size(); i++) {
			System.out.println(ulaz.get(i));
		}
		System.out.println();
		//provjera je li pocinje sa novim scopeom -- nepotrebno
		if(ulaz.get(0).contains("<za_petlja>")) {
				unutarnja = new ArrayList<String>();
				vanjska.add(unutarnja);
		}*/

		unutarnja = new ArrayList<String>();
		vanjska.add(unutarnja);
		
		for(int i =0 ; i<ulaz.size(); i++) {
			if(flag == true) return;
			if(ulaz.get(i).contentEquals("<naredba_pridruzivanja>")) {
				pridruzivanje(i+1);
			}
			//kako izbrisati unutarnju listu za kr_za i kr_az
			if(ulaz.get(i).contentEquals("<za_petlja>")) {
				zaPetlja(i+1);
			}
			if(ulaz.get(i).contains("KR_AZ")){
				
				vanjska.remove(vanjska.size()-1);
				
			}
		}
	}
	
	
	
	public static void main(String[] args) throws IOException {
		String linija;
		BufferedReader citac = new BufferedReader(new InputStreamReader(System.in));
		while ((linija=citac.readLine()) != null){
			if(linija.isEmpty()) break;
			linija = linija.trim();
			if(linija.contentEquals("<E>") || linija.contentEquals("<T>") ||
					linija.contentEquals("<P>") || linija.contentEquals("$") ||
					linija.contentEquals("<T_lista>") || linija.contentEquals("<E_lista>")
					|| linija.contentEquals("<lista_naredbi>") || linija.contentEquals("<naredba>")
					|| linija.contentEquals("<program>") || linija.contains("L_ZAGRADA") || linija.contains("D_ZAGRADA") 
					|| linija.contains("OP") || linija.contains("BROJ")) continue;
			ulaz.add(linija);
		}
		SemAnal();
	}

}
