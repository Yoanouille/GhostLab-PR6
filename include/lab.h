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

//Alloue la mémoire du labyrinthe
lab *build_lab(int w, int h);

//Libère la mémoire du labyrinthe
void destroy_lab(lab *l);

//Fonction d'affichage du labyrinthe
void print_lab(lab *l);
void print_lab_2(lab *l);

//Génère le labyrinthe
void gen_lab(lab *l);

//Génère les pièges
void gen_piege(lab *l, int nb_piege);

#endif