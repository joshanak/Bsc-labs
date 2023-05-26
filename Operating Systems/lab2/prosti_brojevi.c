#include<stdio.h>
#include<unistd.h>
#include<signal.h>
#include<math.h>
#include<sys/time.h>
#define _XOPEN_SOURCE
#define _XOPEN_SOURCE_EXTENDED

int pauza = 0;
unsigned long broj = 1000000001;
unsigned long zadnji = 1000000001;
int prost ( unsigned long n ) {
	unsigned long i, max;

	if ( ( n & 1 ) == 0 ) /* je li paran? */
		return 0;

	max = sqrt ( n );
	for ( i = 3; i <= max; i += 2 )
		if ( ( n % i ) == 0 )
			return 0;

	return 1; /* broj je prost! */
}

void periodicki_ispis (int sig) {
   printf("zadnji prosti broj = %ld\n", zadnji);
}

void postavi_pauzu (int sig) {
   pauza = 1 - pauza;
}

void prekini (int sig) {
    printf("zadnji prosti broj = %ld\n", zadnji);
    kill(getpid(), SIGKILL);
}



int main(void) {
   //povezi_signale_s_funkcijama; // na signal SIGTERM pozovi funkciju prekini()
   //postavi_periodicki_alarm;    // svakih 5 sekundi pozovi funkciju periodicki_ispis();
    sigset(SIGINT, postavi_pauzu);
    sigset(SIGALRM, periodicki_ispis);
    sigset(SIGTERM, prekini);

    struct itimerval t;

	/* povezivanje obrade signala SIGALRM sa funkcijom "periodicki_posao" */
	/* definiranje periodičkog slanja signala */
	/* prvi puta nakon: */
	t.it_value.tv_sec = 5;
	t.it_value.tv_usec = 0;
	/* nakon prvog puta, periodicki sa periodom: */
	t.it_interval.tv_sec = 5;
	t.it_interval.tv_usec = 0;

	/* pokretanje sata s pridruženim slanjem signala prema "t" */
	setitimer (ITIMER_REAL, &t, NULL );

     /*ponavljaj {
      ako je ( prost ( broj ) == DA )
         zadnji = broj;
      broj++;
      dok je ( pauza == 1 )
         pauziraj ();
   }*/

    while(1){
       if(prost(broj)==1){
       zadnji = broj;
       }
        broj++;
        while(pauza == 1){
        pause();
        }
   }
}