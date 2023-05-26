#include<stdio.h>
#include<unistd.h>
#include<signal.h>
#include<math.h>
#include<sys/time.h>
#include<time.h>

#define _XOPEN_SOURCE
#define _XOPEN_SOURCE_EXTENDED
#include <stdlib.h>

int pid=0;

void prekidna_rutina(){
   kill(pid, SIGKILL);
   exit(0);
}

int main(int argc, char *argv[]){
   pid=atoi(argv[1]);
   sigset(SIGINT, prekidna_rutina);
   srand((unsigned)time(NULL));
   

   while(1){
      /* odspavaj 3-5 sekundi */
      /* slučajno odaberi jedan signal (od 4) */
      /* pošalji odabrani signal procesu 'pid' funkcijom kill*/
       int random = rand()%3+3;
       sleep(random);
       int broj = rand()%4+1; 
       if(broj == 1) kill(pid, SIGUSR1);
       if(broj == 2) kill(pid, SIGUSR2);
       if(broj == 3) kill(pid, SIGPIPE);
       if(broj == 4) kill(pid, SIGSYS);
   }
   return 0;
}
