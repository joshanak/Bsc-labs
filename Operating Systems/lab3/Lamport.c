#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <malloc.h>
#include <pthread.h>
#include <unistd.h>

int var, iterations, threads;
int *ulaz, *broj;

//funkcija koja vraća najveći element iz polja
int maksimum(){
    

    int max = broj[0];
 
    for (int i = 1; i < threads; i++){
        if (broj[i] > max)
            max = broj[i];
    }
    return max;

}



void *dretva ( void *rbr ){
 int *numb = rbr;
 ulaz[*numb] = 1;
 broj[*numb] = maksimum() + 1;
 ulaz[*numb] = 0;

 for(int j = 0; j<threads; j++){
      while(ulaz[j] != 0){};
      while((broj[j] != 0) && ((broj[j] < broj[*numb]) || ((broj[j] == broj[*numb]) && (j < (*numb))))){};
}


//kritično odsjek
 for ( int i = 1; i <= iterations; i++) {
    
        var = var + 1;
    }

broj[*numb] = 0;


 //nekritični odsjek   
    return NULL;

}




int main(int argc, char *argv[]){
    int *memorija;
    
    var = 0;
    
    pthread_t *t;

    if (argc < 3) {
		
		exit(1);
    }
    threads = atoi (argv[1]);
    iterations = atoi (argv[2]);
    //scanf("%d %d", &threads, &iterations);
   
	//ne radi zbog segmentation fault-a
   /* memorija = malloc ( sizeof(int) * (2*threads) + threads * sizeof(pthread_t) ); //pridjeli memoriju
    ulaz = memorija;    //pridjeli memoriju globalnim varijablama
    broj = ulaz + threads;
    t = (pthread_t *) (broj + 1); //pripremi memoriju za dretve */
   
    broj = malloc (threads * sizeof(int));
    ulaz = malloc (threads * sizeof(int));
	t = malloc (threads * sizeof(pthread_t)); 

    for(int i=0; i<threads; i++) broj[i] = ulaz[i] = 0; //inicijaliziraj polja
    
    
    int br[threads];
    
    for (int i = 0; i < threads; i++) {
    br[i]=i;
    
    pthread_create ( &t[i], NULL, dretva, &br[i]); //stvori dretve i pokreni program
    }
    
    for (int j= 0; j < threads; j++)
    pthread_join ( t[j], NULL ); //cekaj kraj dretve t[j]

    printf("%d\n", var);
}
