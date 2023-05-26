#include <stdio.h>
#include <semaphore.h>
#include <unistd.h>
//#include <signal.h>
#include <stdlib.h>
#include <sys/shm.h>
#include <sys/types.h>
#include <sys/wait.h>


#include<time.h>

/* #define _XOPEN_SOURCE 
#define _XOPEN_SOURCE_EXTENDED
*/



    int *sobovi, *patuljci;
    sem_t *KO, *djedBozicnjak, *konzultacije, *opcisobovi;

void stvori_sob(){
    
    sem_wait(KO); //ČekajBsem(K) kritični odsječak
    printf("Ulazak u KO \n");
    (*sobovi)++;
    printf("Stvorio se novi sob broj: %d\n", *sobovi);
    if((*sobovi)==10) 
    sem_post(djedBozicnjak); //PostaviBSem(djedbozicnjak)
    printf("Izlazak iz KO\n");
    sem_post(KO); //gotov kritični odsječak
    sem_wait(opcisobovi);

}



void patuljak(){
	sem_wait(KO);//ČekajBSem(K)
   printf("Ulazak u KO\n");
	(*patuljci)++;
    printf("Stvorio se novi patuljak broj : %d\n", *patuljci);
	if ((*patuljci) % 3 == 0) {//ovaj je treći
		sem_post(djedBozicnjak); //PostaviBSem(djedbozicnjak)
	} 
    printf("Izlazak iz KO\n");
	sem_post(KO);//PostaviBSem(K)
	sem_wait(konzultacije);//ČekajOSem(konzultacije)
    
   // sem_wait(KO);
    //(*patuljci)--;
    //sem_post(KO);
    
}


void sjeverniPol(){
    printf("Stvoren proces sjeverni pol\n");
    
     do {
       
        int r = (rand()%3+1); 
        sleep(r);
        r=(rand()%100);
        
		if(r>50 && 	(*sobovi)<10) {
            
            if(fork() == 0){       
           
            sleep(1);
			stvori_sob();
            exit(0);
            }
		}
        r=(rand()%100);
		if(r>50){
           
			if(fork()==0){
                sleep(1);
                patuljak();
                exit(0);
            }
		}
	}while(1);
}

void funkcijaDjedBozicnjak(){
    printf("Stvoren proces djed bozicnjak\n");
	do {
        printf("Djed spava\n");
		sem_wait(djedBozicnjak);//ČekajBSem(djedBozicnjak)
		sem_wait(KO);//ČekajBSem(K)
		if ((*sobovi) == 10 && (*patuljci) > 0) {
			sem_post(KO);//PostaviBSem(K)
            printf("Raznosi poklone, broj sobova %d, broj patuljaka %d\n", *sobovi, *patuljci);
			sleep(2);//raznosi poklone
			sem_wait(KO);//ČekajBSem(K)

			for(int i = 0; i<10; i++) sem_post(opcisobovi);//PostaviOSem(sobovi, 10) povećaj semafor za 10
			(*sobovi) = 0;
            printf("Raznošenje poklona završeno\n");
            sem_post(KO);//PostaviBSem(K)
		}
		if((*sobovi)==10) {
			sem_post(KO);//PostaviBSem(K)
            printf("Hranjenje sobova\n");
			sleep(2);//nahrani sobove
		    printf("Hranjenje sobova završeno\n");
		}

		//ako je samo_tri_patuljka_pred_vratima 
		while (*(patuljci) >= 3) {
			sem_post(KO);//PostaviBSem(K)
            printf("Rješavanje problema\n");
			sleep(2); //riješi njihov problem
			

           
			for(int i = 0; i<3; i++)
                sem_post(konzultacije);  //PostaviOSem(konzultacije, 3) povećaj semafor za 3
             (*patuljci)=(*patuljci)-3;
           printf("Rješavanje problema završeno\n");
			  
            
		}
		
	} while(1);
}



/*void prekini (int sig) {
    printf("Započeo završetak\n");
    sem_destroy(KO);
    sem_destroy(konzultacije);
    sem_destroy(opcisobovi);
    sem_destroy(djedBozicnjak);
    shmdt(KO);
    shmdt(djedBozicnjak);
    shmdt(opcisobovi);
    shmdt(konzultacije);
    printf("program zavrsio");
    kill(getpid(),SIGKILL);
    exit(0);

}*/

int main(void){
   
    int ID;
   // sigset(SIGINT,prekini);
  

    ID = shmget (IPC_PRIVATE, 4 * sizeof(sem_t) + 2 * sizeof(int), 0600);
	KO = shmat(ID, NULL, 0);
	djedBozicnjak = (sem_t*) (KO + 1);
	opcisobovi = (sem_t*) (djedBozicnjak + 1);
	konzultacije = (sem_t*) (opcisobovi + 1);
	shmctl(ID, IPC_RMID, NULL); 

	patuljci = (int *) (konzultacije + 1);
	sobovi = (int *) (patuljci + 1);
	


    srand(time(NULL)); 
    (*patuljci)=0;
    (*sobovi)=0;


    sem_init(djedBozicnjak, 1, 0); //djed bozicnjak semafor
    sem_init(KO, 1 , 1); //semafor kriticni odsjecak
    sem_init(opcisobovi, 1, 0); //opci semafor za sobove
    sem_init(konzultacije, 1, 0);//opci semafor za patuljkeđ

    if(fork()==0){
       
        sjeverniPol();
        exit(0);
    }
    if(fork()==0){
       
        funkcijaDjedBozicnjak();
        exit(0);

    }
    printf("STVORENI");
    wait(NULL);
    wait(NULL);


    sem_destroy(KO);
    sem_destroy(opcisobovi);
     sem_destroy(konzultacije);
    sem_destroy(djedBozicnjak);

    shmdt(KO);
    shmdt(djedBozicnjak);
    shmdt(opcisobovi);
    shmdt(konzultacije);
    

    return 0;

}