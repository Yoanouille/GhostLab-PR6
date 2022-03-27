#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include "lab.h"

typedef struct list_wall list_wall;
struct list_wall {
    int i;
    int j;
    list_wall *next;
};

lab *build_lab(int w, int h) {
    int **tab = malloc(sizeof(int *) * h);
    if(tab == NULL) {
        dprintf(2, "Error malloc 1 lab %dx%d\n", w,h);
        exit(EXIT_FAILURE);
    }
    for(int i = 0; i < h; i++) {
        tab[i] = malloc(sizeof(int) * w);
        if(tab[i] == NULL) {
            dprintf(2, "Error malloc 2 %i lab %dx%d\n", i,w,h);
            exit(EXIT_FAILURE);
        }
        for(int j = 0; j < w; j++) {
            tab[i][j] = 0;
        }
    }
    lab *l = malloc(sizeof(lab));
    l->tab = tab;
    l->h = h;
    l->w = w;
    return l;
}

void destroy_lab(lab *l) {
    for(int i = 0; i < l->h; i++) {
        free(l->tab[i]);
    }
    free(l->tab);
    free(l);
}

void print_lab(lab *l) {
    for(int i = 0; i < l->h; i++) {
        for(int j = 0; j < l->w; j++) {
            printf("%d ", l->tab[i][j]);
        }
        printf("\n");
    }
}

void print_lab_2(lab *l) {
    for(int i = 0; i < l->h; i++) {
        for(int j = 0; j < l->w; j++) {
            if(l -> tab[i][j] == 0) printf("\u25A0 ");
            else printf("\u25A1 ");    
        }
        printf("\n");
    }
}

list_wall *add_wall(list_wall *list, int i, int j) {
    if(list == NULL) {
        list_wall *l = malloc(sizeof(list_wall));
        if(l == NULL) {
            dprintf(2,"Error malloc add_wall %d %d\n", i, j);
            exit(EXIT_FAILURE);
        }
        l->i = i;
        l->j = j;
        l->next = NULL;
        return l;
    }
    if(list->i == i && list->j == j) return list;
    list->next = add_wall(list->next, i, j);
    return list;
}

list_wall *remove_wall(list_wall *l, int index) {
    if(l == NULL) return NULL;
    if(index == 0) {
        list_wall *n = l->next;
        free(l);
        return n;
    }
    l->next = remove_wall(l->next, index - 1);
    return l;
}

void destroy_list_wall(list_wall *l) {
    if(l != NULL) {
        destroy_list_wall(l->next);
        free(l);
    }
}

void print_list_wall(list_wall *l) {
    if(l == NULL) printf("\n");
    else {
        printf("(%d, %d) ", l->i, l->j);
        print_list_wall(l->next);
    }
}

int size(list_wall *l) {
    if(l == NULL) return 0;
    return 1 + size(l->next);
}

list_wall *get(list_wall *l, int index) {
    if(l == NULL) return NULL;
    if(index == 0) return l;
    return get(l->next, index - 1);
}

int has_only_one_free_neighbors(lab *l, int i, int j) {
    int rep = 0;
    if(i > 0 && l->tab[i - 1][j] == 1) rep++; 
    if(i < l->h - 1 && l->tab[i + 1][j] == 1) rep++;
    if(j > 0 && l->tab[i][j - 1] == 1) rep++;
    if(j < l->w - 1 && l->tab[i][j + 1] == 1) rep++;
    return rep == 1;
}

list_wall *add_neighbors(lab *l, list_wall *list, int i, int j) {
    if(i > 0 && l->tab[i - 1][j] == 0) list = add_wall(list, i - 1, j);
    if(i < l->h - 1 && l->tab[i + 1][j] == 0) list = add_wall(list, i + 1, j);
    if(j > 0 && l->tab[i][j - 1] == 0) list = add_wall(list, i, j - 1);
    if(j < l->w - 1 && l->tab[i][j + 1] == 0) list = add_wall(list, i, j + 1);
    return list;
}   

void gen_lab(lab *l) {
    l->tab[0][0] = 1;
    list_wall *list = add_neighbors(l, NULL, 0, 0);
    
    while(list != NULL) {
        int s = size(list);
        int r = rand() % s;
        //printf("%d\n", r);
        list_wall *elt = get(list, r);
        if(has_only_one_free_neighbors(l, elt->i, elt->j)) {
            //printf("(%d, %d)\n", elt->i, elt->j);
            l->tab[elt->i][elt->j] = 1;
            list = add_neighbors(l, list, elt->i, elt->j);
        }
        list = remove_wall(list, r);
        //print_lab(l);
        //printf("\n");
    }
}

int main(void) {
    srand(time(NULL));
    lab *l = build_lab(15, 10);
    gen_lab(l);
    //print_lab(l);
    print_lab_2(l);
    
    for(int i = 0; i < 10; i++) {
        gen_lab(l);
        print_lab_2(l);
        printf("\n");
    }
    
    destroy_lab(l);

    // list_wall *list = NULL;
    // list = add_wall(list, 0, 0);
    // list = add_wall(list, 1, 1);
    // list = add_wall(list, 7, 7);
    // list = add_wall(list, 1, 1);

    // print_list_wall(list);
    // destroy_list_wall(list);

}