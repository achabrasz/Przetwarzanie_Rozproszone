#include <stdio.h>
#include <stdlib.h>
#include "priority_queue_list.h"


void qlist(pqueue *head, void (*print_data)(void *)) {
	pqueue *p;
	
	for (p = head; p != NULL; p = p->next) {
		printf("%d: ", p->k);
		print_data(p->data);
		printf("\n");
	}
	
}

void qinsert(pqueue **phead, void *data, int k) {
pqueue *p, *new;
	
	new = (pqueue *)malloc(sizeof(pqueue));
	new->data = data;
	new->k = k;
	
	if (*phead == NULL) {
		new->next = NULL;
		new->prev = NULL;
		*phead = new;
	} else {
		if ((*phead)->k < k) {
			new->next = *phead;
			new->prev = NULL;
			(*phead)->prev = new;
			*phead = new;
			return;
		}
		for (p = *phead; p->next != NULL && p->next->k > k; p = p->next);
		if (p->next == NULL) {
			new->next = NULL;
			new->prev = p;
			p->next = new;
		} else {
			new->next = p->next;
			new->prev = p;
			p->next->prev = new;
			p->next = new;
		}
	}
}


void qremove(pqueue **phead, int k) {
pqueue *p;
	
	for (p = *phead; p != NULL && p->k != k; p = p->next);
	if (p != NULL) {
		if (p->prev == NULL) {
			*phead = p->next;
			if (p->next != NULL) {
				p->next->prev = NULL;
			}
		} else {
			p->prev->next = p->next;
			if (p->next != NULL) {
				p->next->prev = p->prev;
			}
		}
		free(p);
	}
}

