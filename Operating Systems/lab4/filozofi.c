#include<pthread.h>
#include<stdio.h>
#include<stdlib.h>
#include<unistd.h>
#include<signal.h>
#define BROJ 5

pthread_mutex_t m;
pthread_cond_t red[BROJ];

char filozofi[BROJ];
int vilica[BROJ];
int z[BROJ];

     void jesti(int n){
       
         // n - redni broj filozofa 
     pthread_mutex_lock(&m);//uđi_u_kritični_odsječak;
      filozofi[n] = 'o';
      while (vilica[n] == 0 || vilica[(n + 1) % 5] == 0)
         pthread_cond_wait(&red[n], &m);//čekaj_u_redu_uvjeta(red[n]);
      vilica[n] = vilica[(n + 1) % 5] = 0;
     
      filozofi[n] = 'X';
        
       pthread_mutex_unlock(&m);     //izađi_iz_kritičnog_odsječka;

      printf("%c %c %c %c %c\n", filozofi[0], filozofi[1], filozofi[2], filozofi[3], filozofi[4]);
        
     pthread_mutex_lock(&m);//uđi_u_kritični_odsječak;
      filozofi[n] = 'O';
      vilica[n] = vilica[(n + 1) % 5] = 1;
     
     if(n!=0){
        pthread_cond_signal(&red[(n+1)%5]);//oslobodi_dretvu_iz_reda(red[(n + 1) % 5]);
        pthread_cond_signal(&red[(n-1)%5]);//oslobodi_dretvu_iz_reda(red[(n - 1) % 5]);
    }
    else{
        pthread_cond_signal(&red[(n+1)%5]);//oslobodi_dretvu_iz_reda(red[(n + 1) % 5]);
        pthread_cond_signal(&red[4]);//oslobodi_dretvu_iz_reda(red[(n - 1) % 5]);
    } 
        pthread_mutex_unlock(&m);//izađi_iz_kritičnog_odsječka;
    }

    void *filozof(void *rbr){
        
      while(1){

        sleep(4);//misli
        jesti(*((int*)rbr));        
      }
    }

   




int main(void){
    
    pthread_t t[BROJ];
    pthread_mutex_init(&m, NULL);
    pthread_cond_init(red, NULL);
    


     for (int i = 0; i < BROJ; i++) {
    filozofi[i]='O';
    vilica[i]='1';
    z[i]=i;
    pthread_create ( &t[i], NULL, filozof, &z[i]);
    }
   
    for (int j= 0; j < BROJ; j++)
    pthread_join ( t[j], NULL ); //cekaj kraj dretve t[j] ˇ




}
