/*
 * Node 2 Hostname
 */

#ifndef _N2H_C_
#define _N2H_C_

#include <stdio.h>
#include <stdlib.h>
#include <assert.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>
#include <netdb.h>
#include <unistd.h>
#include <string.h>

#include "common.h"
#include "n2h.h"
#include "queue.h"
#include "rt.h"

#define logf (stdout)

static struct n2h *g_n2h;
static int my_id;

int create_n2h()
{
	InitDQ(g_n2h, struct n2h);
	assert (g_n2h);
  
	g_n2h->nid = -1;
	g_n2h->hostname = NULL;
  
	return (g_n2h != 0x0);
}

/*
 * returns a socket that is bound on the port
 */
int bind_port(int port) {
  /*Copied from book "TCP/IP Sockets In C" page 57-59*/

  char service[6];
  service[0] ='\0';
  service[1] ='\0';
  service[2] ='\0';
  service[3] ='\0';
  service[4] ='\0';
  service[5] ='\0';

  sprintf(service, "%d", port);
  
  struct addrinfo hints;
  memset(&hints, 0, sizeof(hints));
  hints.ai_family = AF_UNSPEC;
  hints.ai_flags = AI_PASSIVE;
  hints.ai_socktype = SOCK_DGRAM;
  hints.ai_protocol = IPPROTO_UDP;

  struct addrinfo *servAddr;
  int rtnVal = getaddrinfo(NULL, service, &hints, &servAddr);
  if(rtnVal != 0){
    printf("getaddrinfo failure\n");
    return -1;
  }

  int sock = socket(servAddr->ai_family, servAddr->ai_socktype, servAddr->ai_protocol);
  if(sock < 0){
    printf("Socket failure\n");
    return -1;
  }
   
  if(bind(sock, servAddr->ai_addr, servAddr->ai_addrlen) < 0){
    printf("bind() failure \n");
    return -1;
  }

  return sock;
}

/*
 * Check if <hostname> is a valid host
 * Add into node->hostname mapping
 */
int add_n2h(node nid, char *hostname)
{
	struct n2h* nl = (struct n2h *) getmem (sizeof (struct n2h));
	nl->nid = nid;

	assert(gethostbyname(hostname));
    
	nl->hostname = (char *) malloc (strlen(hostname)+1);
	strcpy (nl->hostname, hostname);
  
	InsertDQ(g_n2h, nl);
	return (nl != 0x0);
}

/*
 * do "node_id->hostname mapping"
 */
char *gethostbynode(node nid)
{
	struct n2h *i;
    
	for (i = g_n2h->next; i != g_n2h; i= i->next) {
		assert(i);
		if ( i->nid == nid )
			return i->hostname;
	}
	return 0x0;
}

/*
 * Using node->hostname list to initiailize the routing table
 */
int init_rt_from_n2h()
{
	struct n2h *i;

	for (i = g_n2h->next; i != g_n2h; i= i->next) {
		assert(i);
		if ( i->nid != get_myid() ) {
			// dest, cost, next-hop
			assert(add_rte(i->nid, -1, i->nid));
		}
	}
	return true;
}

/*
 * Visit the while node->hostname list
 */
void print_n2h()
{
	struct n2h *i;

	fprintf (logf, "\n[n2h] ***** dumping node-to-hostname list *****\n");  
	for (i=g_n2h->next; i != g_n2h; i=i->next){
		assert(i);
		fprintf (logf, "[n2h]\tnode(%d) <--> hostname(%s)\n",
			i->nid, i->hostname);
	}
}

/*
 * store my node id given from command line
 */
void set_myid (node myid)
{
	my_id = myid;
}

/*
 * return my node id given from command line
 */
node get_myid()
{
	return my_id;
}

/*
 * Is <nid> on my machine ?
 */
bool is_me(node nid)
{
	int ret;
	int my_net_num;
	int node_net_num;
	char myhostname[256];
	struct hostent *hp;

	// get my net number
	ret = gethostname(myhostname, 256);
	assert (ret >= 0);

	assert ((hp = gethostbyname(myhostname)));
	memcpy (&my_net_num, hp->h_addr, 4);

	// get nid's net number
	assert ((hp = gethostbyname(gethostbynode(nid))));
	memcpy(&node_net_num, hp->h_addr, 4);

	// it's me?
	if (my_net_num == node_net_num)
		return true;
	else
		return false;
}



#endif
