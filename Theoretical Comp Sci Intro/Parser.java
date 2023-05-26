import java.util.*;
import java.io.*;

public class Parser {
	public static char znak;
	public static BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
	public static boolean zastavica=true;
	public static String ulazniNiz;
	public static int brojac=0;
	public static char[] znakovi;
	
	public static void S() throws IOException {
		System.out.print("S");
		
		if(zastavica == false ) return;
		
		

		
		if(brojac<=(znakovi.length-1)) {
			znak = znakovi[brojac];
			brojac++;
			}
		else {
			zastavica = false;
			//System.out.println("GREsKA "+ brojac);
			return;
		}
		
		
		
		
		if(znak == 'a') {
			
					A();
					if(zastavica == false ) return;
				    B();
				
		}
		else if(znak == 'b') {
					B();
					 if(zastavica == false ) return;
				    A();
				    if(zastavica == false ) return;
					}
		else {
			zastavica = false;
			//System.out.println("GREsKA "+ brojac);
			return;
		}
					
		}
		
		
	
	
	
	public static void A() throws IOException {
		System.out.print("A");
		
		if(zastavica == false ) return;
		
		if(brojac<=(znakovi.length-1)) {
			znak = znakovi[brojac];
			brojac++;
			}
		else {
			zastavica = false;
		//	System.out.println("GREsKA "+ brojac);
			return;
		}
		
		
		if(znak == 'b') {
			
				C();
			
		}
		else if(znak == 'a') {
		
		}
		else {
			zastavica = false;
			return;
		}
	}
	
	
	public static void C() throws IOException {
		System.out.print("C");
		if(zastavica == false ) return;
	
		
			A();
			if(zastavica == false ) return;
	     	A();	
	     	
	}
	
	public static void B() throws IOException {
		System.out.print("B");
		if(zastavica == false ) return;
		
		
		if(brojac<=(znakovi.length-1)) {
		znak = znakovi[brojac];
		brojac++;
		//ako ima jos znakova i sljedeci znak je c
		if(znak == 'c') {
			
			if(brojac<=(znakovi.length-1)) {
				znak = znakovi[brojac];
				brojac++;
				}
			
			if(znak == 'c') {
					S();
					if(zastavica == false ) return;
					
					if(brojac<=(znakovi.length-1)) {
						znak = znakovi[brojac];
						brojac++;
						}
					else {
						zastavica = false ;
						//System.out.println("GREsKA "+ brojac);
						return;
					}
					
					if(znak == 'b') {
						
						if(brojac<=(znakovi.length-1)) {
							znak = znakovi[brojac];
							brojac++;
							}
						else {
							zastavica = false;
							//System.out.println("GREsKA "+ brojac);
							return;
						}
						
						if(znak == 'c') {
						}
						else {
							zastavica = false;
							//System.out.println("GREsKA "+ brojac);
							return;
						}
					}
					
					else {
						zastavica = false;
						//System.out.println("GREsKA "+ brojac);
						return;
					}
				}
			
			else {
				zastavica = false;
				//System.out.println("GREsKA "+ brojac);
				return;
			}
				
			}
			else {
			brojac = brojac - 1;
			}
		
		}
			else {
				
			}
		}
		
		
	
	
	

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		ulazniNiz = reader.readLine();
		znakovi = ulazniNiz.toCharArray();
       
        S();
        
      
    //    System.out.println();
     //   System.out.println(brojac + " " + zastavica);
       
        if ( zastavica == false || brojac != (znakovi.length)) {
        	System.out.println();
        	System.out.println("NE");
        }
        else {
        	System.out.println();
        	System.out.println("DA");
        }
	}

}
