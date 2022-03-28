#ifndef LAB_H
#define LAB_H
#include <stdio.h>
#include <stdlib.h>
#include <time.h>
typedef struct lab
{
    int **tab;
    int h;
    int w;
} lab;

lab *build_lab(int w, int h);
void destroy_lab(lab *l);
void print_lab(lab *l);
void gen_lab(lab *l);


#endif