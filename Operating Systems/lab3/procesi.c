#include <stdio.h>
#include <stdlib.h>
#include <sys/types.h>
#include <sys/wait.h>
#include <sys/ipc.h>
#include <sys/shm.h>
#include <unistd.h>


int Id; // identifikacijski broj segmenta //
int *var; //globalna varijabla koju cemo povećavati;



void enlarge(int n){
        for(int i = 0; i<n; i++)
        *var = *var + 1;
}

int main(int argc, char *argv[]){
int m, n;


if (argc < 3) {
		
		exit(1);
}
m = atoi (argv[1]);
n = atoi (argv[2]);



Id = shmget ( IPC_PRIVATE, sizeof(int), 0600 );
var =  (int *)shmat(Id, NULL, 0);
*var = 0;

enlarge(n);

for(int i=1; i<m; i++){
        if ( !fork () ) { 
            enlarge(n);
            exit (0);
        }
}   
    for(int i=1; i<m; i++){
        wait(NULL);
    }
    printf("%d\n", *var);
    shmdt ( var );
    shmctl ( Id, IPC_RMID, NULL );
    

}