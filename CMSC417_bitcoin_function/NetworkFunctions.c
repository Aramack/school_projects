#include <stdio.h>
#include <netinet/in.h>
#include <string.h>
#include <CBVersion.h>
#include <CBNetworkAddress.h>
#include <CBMessage.h>
#include <CBPeer.h>
#include <CBByteArray.h>
#include <time.h>
#include <sys/select.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <arpa/inet.h>
#include <stdlib.h>
#include <netdb.h>
#include <fcntl.h>
#include <unistd.h>
#include "NetworkFunctions.h"
#include "CommandFunctions.h"

CBNetworkAddress *localAddress; //Address structure used for incoming connections
peerList *peersHead;	//Linked list of all peers known about
fd_set masterFD;  //Holds all the open file descriptors used for select
int acceptSocket;

CBNetworkAddress *getLocalAddress() {
	return localAddress;
}

peerList *getPeersHead() {
	return peersHead;
}

void makeHeader(header *hdr, CBMessage *message) {
	switch(message->type) {
		case(CB_MESSAGE_TYPE_VERSION): 
			memcpy(hdr->command, VERSION_COMMAND, 12);
			break;
		case(CB_MESSAGE_TYPE_VERACK): 
			memcpy(hdr->command, VERACK_COMMAND, 12);
			break;
		case(CB_MESSAGE_TYPE_PING): 
			memcpy(hdr->command, PING_COMMAND, 12);
			break;
		case(CB_MESSAGE_TYPE_PONG): 
			memcpy(hdr->command, PONG_COMMAND, 12);
			break;
		case(CB_MESSAGE_TYPE_GETADDR): 
			memcpy(hdr->command, GETADDR_COMMAND, 12);
			break;
		case(CB_MESSAGE_TYPE_ADDR): 
			memcpy(hdr->command, ADDR_COMMAND, 12);
			break;
		case(CB_MESSAGE_TYPE_GETBLOCKS): 
			memcpy(hdr->command, GETBLOCKS_COMMAND, 12);
			break;
		case(CB_MESSAGE_TYPE_INV): 
			memcpy(hdr->command, INV_COMMAND, 12);	
			break;
		case(CB_MESSAGE_TYPE_GETDATA): 
			memcpy(hdr->command, GETDATA_COMMAND, 12);
			break;
		case(CB_MESSAGE_TYPE_BLOCK): 
			memcpy(hdr->command, BLOCK_COMMAND, 12);
			break;
		case(CB_MESSAGE_TYPE_TX): 
			memcpy(hdr->command, TX_COMMAND, 12);
			break;
	}
	hdr->length = message->bytes->length;
	hdr->magic = UMD_NETMAGIC;
	memcpy(hdr->checksum, message->checksum, 4);
}

void sendPeerMessage(CBPeer *peer, CBMessage *message) {
	header hdr;
	CBByteArray *data;

	makeHeader(&hdr, message);
	data = message->bytes;
	if(send(peer->socketID, &hdr, 24, MSG_NOSIGNAL) < 24) {
		peersHead = removePeer(peersHead, peer);
		setMaxSock();
		printf("Send Error\n");
		return;
	}
	if(message->bytes->length > 0) {
		send(peer->socketID, data->sharedData->data + data->offset, hdr.length, 0);
	}
	peer->connectionWorking = true;
	printf("Message sent with header %s\n", hdr.command);
	CBFreeMessage(message);
}

void receivePeerMessages(CBPeer *peer) {
	header hdr; 
	uint8_t *buffer;
	
	if( recv(peer->socketID, &hdr, 24, 0) < 24) {
		printf("Failed to read all header data\n");
		return;
	}
	
	//Some messages don't have data so theres no reason to malloc or call recv
	if(hdr.length > 0) {
		if( (buffer = malloc(hdr.length)) == NULL) {
			perror("Buffer allocation Error\n");
			return;
		}
		sleep(2);
		if(recv(peer->socketID, buffer, hdr.length, 0) < hdr.length) {
			printf("Incomplete read from the peer\n");
			return;
		}
	}

	if(!strncmp(hdr.command, VERSION_COMMAND, 12)) {
		processVersion(peer, buffer, hdr.length);
	}else if(!strncmp(hdr.command, VERACK_COMMAND, 12)) {
		processAck(peer, buffer, hdr.length);
	}else if(!strncmp(hdr.command, PING_COMMAND, 12)) {
		processPing(peer, buffer, hdr.length);
	}else if(!strncmp(hdr.command, PONG_COMMAND, 12)) {
		peer->sentHeader = false;
	}else if(!strncmp(hdr.command, GETADDR_COMMAND, 12)) {
		processGetAddr(peer, buffer, hdr.length);
	}else if(!strncmp(hdr.command, ADDR_COMMAND, 12)) {
		processAddr(peer, buffer, hdr.length);
	}else if(!strncmp(hdr.command, GETBLOCKS_COMMAND, 12)) {
		processGetBlocks(peer, buffer, hdr.length);
	}else if(!strncmp(hdr.command, INV_COMMAND, 12)) {
		processInv(peer, buffer, hdr.length);
	}else if(!strncmp(hdr.command, GETDATA_COMMAND, 12)) {
		processGetData(peer, buffer, hdr.length);
	}else if(!strncmp(hdr.command, BLOCK_COMMAND, 12)) {
		processBlocks(peer, buffer, hdr.length);
	}else if(!strncmp(hdr.command, TX_COMMAND, 12)) {
		processTx(peer, buffer, hdr.length);
	}

	peer->connectionWorking = true;
	peer->base.lastSeen = time(NULL);
	printf("Message Received with header: %s\n", hdr.command);
}

void connectToPeer(CBPeer *peer) {
	int sock;
	struct sockaddr_in addr;
	uint8_t *ip = CBByteArrayGetData(peer->base.ip);
	peerList *peers; 
	int flags;

	if( (sock = socket(PF_INET, SOCK_STREAM, 0)) < 0) {
		perror("Failed to create socket");
		return;
	}
	
	flags = fcntl(sock, F_GETFL, 0);
	fcntl(sock, F_SETFL, flags | O_NONBLOCK);

	memset(&addr, sizeof(addr), 0);
	addr.sin_family = AF_INET;
	addr.sin_port = ntohs(peer->base.port);
	addr.sin_addr.s_addr = (((((ip[15]<<8)|ip[14])<<8)|ip[13])<<8)|ip[12];
	
	if(connect(sock, (struct sockaddr *)&addr, sizeof(addr)) == false) {
		perror("Failed to connect to host");
		return;
	}

	peer->socketID = sock;
	peer->connectionWorking = false;
	peers = malloc(sizeof(peerList));
	peers->peer = peer;
	peers->maxSock = (peersHead == NULL || peersHead->maxSock < sock)?sock:peersHead->maxSock;
	peers->next = peersHead;
	peersHead = peers;
	peer->base.lastSeen = time(NULL);
	FD_SET(peer->socketID, &masterFD);
	fcntl(sock, F_SETFL, flags);
}

void acceptConnection(int sock) {

}

void setMaxSock() {
	peerList *cur;
	int maxSock = 0;
	for(cur = peersHead; cur != NULL; cur = cur->next) {
		if(maxSock < cur->peer->socketID) {
			maxSock = cur->peer->socketID;
		}
	}
	peersHead->maxSock = maxSock;
}

peerList *removePeer(peerList *head, CBPeer *peer) {
	if(head == NULL) {
		return NULL;
	}else if(peer == head->peer) {
		//Need to put stuff to free memory used
		return head->next;
	}else {
		head->next = removePeer(head->next, peer);
		return head;
	}
}

void addNewPeerByCBNetworkAddress(CBNetworkAddress *address) {
	CBPeer *peer = CBNewPeerByTakingNetworkAddress(address);
	connectToPeer(peer);
}

void startListeningForConnection() {
	int servSock;
	struct addrinfo hints, *servInfo;
	struct sockaddr_in *addr;
	CBByteArray *address;
	short first, second, third, fourth;

	memset(&hints, 0, sizeof(hints));
	hints.ai_family = AF_INET;
	hints.ai_socktype = SOCK_STREAM;
	hints.ai_flags = AI_PASSIVE;

	getaddrinfo(NULL, DEFAULT_PORT, &hints, &servInfo);

	servSock = socket(servInfo->ai_family, servInfo->ai_socktype, servInfo->ai_protocol);
	bind(servSock, servInfo->ai_addr, servInfo->ai_addrlen);

	freeaddrinfo(servInfo);	
	addr = (struct sockaddr_in*)servInfo->ai_addr;
	listen(servSock, 10);
	first = addr->sin_addr.s_addr & 0xFF;
	second = (addr->sin_addr.s_addr>>8) & 0xFF;
	third = (addr->sin_addr.s_addr>>16) & 0xFF;
	fourth = (addr->sin_addr.s_addr>>24) & 0xFF;
	address = CBNewByteArrayWithDataCopy((uint8_t [16]){0,0,0,0,0,0,0,0,0,0,0xFF,0xFF,first,second,third,fourth}, 16);
	
	localAddress = CBNewNetworkAddress(0, address, DEFAULT_LOCAL_PORT, CB_SERVICE_FULL_BLOCKS, false); 
	acceptSocket = servSock;
	



}

void monitorSockets() {
	fd_set readSet, writeSet;
	struct timeval tv;
	peerList *cur;
	tv.tv_sec = 1;
	tv.tv_usec = 0;
	FD_ZERO(&readSet);

	readSet = masterFD;
	writeSet = masterFD;

	while( select(peersHead->maxSock + 1, &readSet, &writeSet, NULL, &tv) >= 0 ) {
		for(cur = peersHead; cur != NULL; cur = cur->next) {	
			if(FD_ISSET(cur->peer->socketID, &readSet) && cur->peer->connectionWorking) {
				receivePeerMessages(cur->peer);
			}
			if(FD_ISSET(cur->peer->socketID, &writeSet)) {
				if(!cur->peer->versionSent) {
					sendVersion(cur->peer);
				}else if(cur->peer->versionAck && !cur->peer->getAddresses) {
					sendGetAddr(cur->peer);	
				}else if(time(0) - cur->peer->base.lastSeen > 55 && !cur->peer->sentHeader) {
					sendPing(cur->peer);
				}else if(cur->peer->versionAck) {
					//sendGetBlocks(cur->peer);
				}
			}
			if(time(0) - cur->peer->base.lastSeen > 90 && cur->peer->sentHeader) {
				printf("Should remove peer from the list\n");
				peersHead = removePeer(peersHead, cur->peer);
				setMaxSock();
			}
		}

		writeSet = masterFD;
		readSet = masterFD;
	}
}
