/**
 * Header file for client.c 
 *	written by Andrew Lally
 **/


 typedef struct headerStruct {
	uint32_t magic;
	char command[12];
	uint32_t length;
	uint8_t checksum[4];
 } header;

 typedef struct peerlist {
 	CBPeer * peer;
	int maxSock;
	struct peerlist *next;
} peerList;

#define UMD_SERVER "128.8.126.25"
#define DEFAULT_LOCAL_PORT 22333
#define DEFAULT_PORT "22333"

#define UMD_NETMAGIC 0xd0b4bef9

#define VERSION_COMMAND 	"version\0\0\0\0\0"
#define VERACK_COMMAND 		"verack\0\0\0\0\0\0"
#define PING_COMMAND			"ping\0\0\0\0\0\0\0\0"
#define PONG_COMMAND			"pong\0\0\0\0\0\0\0\0"
#define GETADDR_COMMAND 	"getaddr\0\0\0\0\0"
#define ADDR_COMMAND			"addr\0\0\0\0\0\0\0\0"
#define GETBLOCKS_COMMAND	"getblocks\0\0\0"
#define INV_COMMAND			"inv\0\0\0\0\0\0\0\0\0"
#define GETDATA_COMMAND		"getdata\0\0\0\0\0"
#define BLOCK_COMMAND		"block\0\0\0\0\0\0\0"
#define TX_COMMAND			"tx\0\0\0\0\0\0\0\0\0\0"

void sendPeerMessage(CBPeer *peer, CBMessage *message);


peerList *getPeersHead();

CBNetworkAddress *getLocalAddress();

void receivePeerMessages(CBPeer *peer);

void addNewPeerByCBNetworkAddress(CBNetworkAddress *address);

void setMaxSock();

peerList* removePeer(peerList *head, CBPeer *peer);

void addNewPeerByCBPeer(CBPeer *peer);

void acceptConnection(int sock);

void startListeningForConnection();

void connectToPeer(CBPeer *peer);

void monitorSockets();
