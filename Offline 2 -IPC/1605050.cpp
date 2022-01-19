#include<iostream>
#include<cstdio>
#include<pthread.h>
#include<unistd.h>
#include<semaphore.h>
#include<cstring>
#include <time.h>
#include<stdlib.h>
#include<atomic>
using namespace std;

#define number_of_cycles 10
#define number_of_servicemen 3
#define room_size 2

pthread_mutex_t mutex[number_of_servicemen];
sem_t empty_room,departure_room;
pthread_cond_t cond;
int dep=0;
atomic_bool hpt_waiting = ATOMIC_VAR_INIT(false);
atomic_int dep_thd = ATOMIC_VAR_INIT(0);

void* used_service(void* arg){
    int ind=0,t;


  for(int i=0;i<number_of_servicemen;i++){

        pthread_mutex_lock(&mutex[i]);
        if(i == 0)
        {
            while(dep_thd > 0)
                pthread_cond_wait(&cond,&mutex[i]);
        }
        if(i>0){
        pthread_cond_broadcast(&cond);
        pthread_mutex_unlock(&mutex[i-1]);
        }
        printf("%s no cyclist  started taking service from %d th servicemen\n",(char*)arg,i+1);
        t = rand() % 10 + 1;
        sleep(t);
        printf("%s no cyclist finished taking service from %d th servicemen\n",(char*)arg,i+1);

    }

    //printf("%s cyclist is done\n",(char*)arg);
    pthread_mutex_unlock(&mutex[number_of_servicemen-1]);
    //bill payment part
    sem_wait(&empty_room);
        //pthread_mutex_lock(&mutex_bill);
        printf("%s no cyclist has started paying bill\n",(char*)arg);
        t = rand() % 10 +1 ;
        sleep(t);
        printf("%s cyclist has finished paying bill\n",(char*)arg);
        dep++;
        //pthread_mutex_unlock(&mutex_bill);
        sem_post(&empty_room);

        //departure_part

        for(int i=0;i< number_of_servicemen;i++)
        {
            if(i==0)
            {
                dep_thd++;
                           }
            pthread_mutex_lock(&mutex[i]);


        }
        printf("%s no cyclist has departed\n",(char*)arg);
        dep_thd--;
        pthread_cond_broadcast(&cond);
        for(int i=number_of_servicemen-1;i > -1;i--)
        {
            pthread_mutex_unlock(&mutex[i]);
        }

    pthread_exit((void*)strcat((char*)arg," consumer is finishing\n"));
}


int main(int argc, char* argv[])
{   //printf("All is good");
    int res;
    cond = PTHREAD_COND_INITIALIZER;
    for(int i=0;i<number_of_servicemen;i++)
    {
        res = pthread_mutex_init(&mutex[i],NULL);
        if(res !=0)
        {
            printf("Mutex initiation failed");
        }
    }
    res = sem_init(&empty_room,0,room_size);
    if(res !=0)
        {
            printf("empty_room initiation failed");
        }
    res = sem_init(&departure_room,0,0);
    if(res !=0)
        {
            printf("full_room initiation failed");
        }
     pthread_t cyclists[number_of_cycles];
    for(int i = 0; i < number_of_cycles; i++){
        char *id = new char[4];
        strcpy(id,to_string(i+1).c_str());

        res = pthread_create(&cyclists[i],NULL,used_service,(void *)id);

        if(res != 0){
            printf("Thread creation failed\n");
        }
    }

    for(int i = 0; i < number_of_cycles; i++){
        void *result;
        pthread_join(cyclists[i],&result);
        //printf("%s",(char*)result);
    }

    res = sem_destroy(&departure_room);
    if(res != 0){
        printf("Failed\n");
    }
    res = sem_destroy(&empty_room);
    if(res != 0){
        printf("Failed\n");
    }

    for(int i=0;i<number_of_servicemen;i++){
    res = pthread_mutex_destroy(&mutex[i]);
    if(res != 0){
        printf("Failed\n");
    }
    }

    return 0;
}
