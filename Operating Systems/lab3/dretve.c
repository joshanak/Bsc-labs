#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <malloc.h>
#include <pthread.h>
#include <unistd.h>
int var;

 void *dretva ( void *rbr ){
     int *numb = rbr;
     
    for ( int i = 1; i <= *numb; i++) {
    
        var = var + 1;
    }
    return NULL;
}

    int main (int argc, char *argv[]){
    int m, n;
    var = 0;
    

   if (argc < 3) {
		
		exit(1);
	}
	m = atoi (argv[1]);
	n = atoi (argv[2]);
    pthread_t t[m];

    for (int i = 0; i < m; i++) {
    
    pthread_create ( &t[i], NULL, dretva, &n);

    }
    for (int j= 0; j < m; j++)
    pthread_join ( t[j], NULL ); //cekaj kraj dretve t[j] ˇ

    printf("%d\n", var);
    return 0;
 }