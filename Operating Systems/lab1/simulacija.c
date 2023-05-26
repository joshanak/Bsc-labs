#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>
#include <unistd.h>
#include <stdbool.h>

char* memorija;
int size;

void ispisSpremnik(){
   
    int j = 0;
    for(int i = 0; i<size; i++){
       if(j==10) j = 0;
       printf("%d", j);
       j++;
    }
    printf("\n");

}
void ispisMemorije(){
    
    for(int i=0; i<size; i++) printf("%c", memorija[i]);
    printf("\n");

}

void dinamickaAlokacija(int x, int mjesta){
    
    char zahtjev = x + '0';
   ;
    int flag;
    int lokacija;
    for(lokacija=0; lokacija<size; lokacija++){ //iteriraj po cijelom polju
        if(memorija[lokacija]!='-') continue; //preskoci sva mjesta koja nisu prazna
        flag = 0;
        for(int j=0; j<mjesta; j++){
            if(memorija[lokacija+j] != '-'){   //ako mi je prvo mjesto prazno ali netko od sljedecih nije prekini
              flag = 1;
              break;
            }
            
        }
        if(flag == 0) break;
    }

    if(flag == 1) printf("Nema više memorije \n");
    else{
        for(int i = 0; i<mjesta; i++){
            memorija[lokacija+i] = zahtjev;
        }
    }
    ispisMemorije();
     printf("\n");
    }


void oslobadanje(int x){
   
    char zahtjev = '0' + x;
    for(int i = 0; i<size; i++){
        if(memorija[i] == zahtjev) memorija[i] = '-';
    }
    ispisMemorije();
     printf("\n");
}

void inicijalizacija(){
   
    
    for(int i = 0; i<size; i++){
        memorija[i] = '-';
    }
    ispisMemorije();
}

void garbageCollector(){

    char pomocna[size]; 
    for(int i = 0; i<size; i++){
        pomocna[i] = '-';
    }
    int j = 0;
    for(int i=0; i<size; i++){
         if(memorija[i]!='-'){
             pomocna[j] = memorija[i];
             j++;
         }
     }
    memorija = pomocna; 
    ispisMemorije();
    printf("\n");
}


int main(int argc, char *argv[]){
    
    if (argc < 2) {
		
		exit(1);
    }
    size = atoi (argv[1]);
    
    memorija = malloc(size*sizeof(char)); //zauzimanje potrebno velicinu polja
    inicijalizacija();  //postavljanje sve na praznu vrijednost
    srand(time(NULL)); //pokretanje seed-a za rand
    char unos;      // Z ili 0
    
    int brojZahtjeva = 0;
    int r;
    while(1){
        scanf("%c", &unos);
        
        if(unos == 'Z'){
            r = (rand()%10+1);
            printf("Novi zahtjev %d za %d spremnička mjesta\n", brojZahtjeva, r);
            ispisSpremnik();
            dinamickaAlokacija(brojZahtjeva, r); //slanje zastavice za zahtjev
             brojZahtjeva++;
             sleep(2);
        }
        else if(unos == 'O'){
            printf("Koji zahtjev osloboditi?\n");
            scanf("%d", &r);
            printf("Oslobađa se zahtjeva %d\n", r);
            ispisSpremnik();
            oslobadanje(r); //slanje zastavice za oslobadanje
            sleep(2);
        }
        else if(unos == 'G'){
            ispisSpremnik();
            garbageCollector();
            sleep(2);
        }
        else{
            continue;
        }
       
        

    }


}