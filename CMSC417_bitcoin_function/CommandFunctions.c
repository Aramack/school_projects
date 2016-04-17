#include <stdio.h>
#include <netinet/in.h>
#include <string.h>
#include <CBVersion.h>
#include <CBNetworkAddress.h>
#include <CBMessage.h>
#include <CBChainDescriptor.h>
#include <CBGetBlocks.h>
#include <CBBlock.h>
#include <CBPeer.h>
#include <CBInventoryItem.h>
#include <CBInventoryBroadcast.h>
#include <CBAddressBroadcast.h>
#include <CBByteArray.h>
#include <CBFullValidator.h>
#include <time.h>
#include "CommandFunctions.h"
#include "NetworkFunctions.h"

CBFullValidator *validator = NULL;
struct knownHashes *known = NULL;

/****************************************** 
 * Start of processing function 
 * processing message minus header 
 ******************************************/ 

void processVersion(CBPeer *peer, uint8_t *buffer, uint32_t length) {
	CBByteArray *serialisedData = CBNewByteArrayWithData(buffer, length);
	peer->versionMessage = CBNewVersionFromData(serialisedData);
	CBVersionDeserialise(peer->versionMessage);
	if(peer->incomming) {
		sendVersion(peer);
		sendVerAck(peer);
	}
}

void processAck(CBPeer *peer, uint8_t *buffer, uint32_t length) {
	peer->versionAck = true;
	sendGetBlocks(peer);
}

void processPing(CBPeer *peer, uint8_t *buffer, uint32_t length) {	
	//Just sends a pong message in return with the nonce recieved  
	sendPong(peer, buffer, length);
}

void processGetAddr(CBPeer *peer, uint8_t *buffer, uint32_t length) {
	sendAddr(peer);
}

void processAddr(CBPeer *peer, uint8_t *buffer, uint32_t length) {
	CBByteArray *data = CBNewByteArrayWithDataCopy(buffer, length);
	CBAddressBroadcast *broadcast = CBNewAddressBroadcastFromData(data, false);
	CBVarInt count = CBVarIntDecode(broadcast->base.bytes,0);
	count.val = (count.val>30)?30:count.val;
	CBVarIntEncode(broadcast->base.bytes,0,count);
	CBAddressBroadcastDeserialise(broadcast);
	peerList *cur;
	int index; 
	
	for(index = 0; index < broadcast->addrNum; index++) {
		bool exists = false;
		for(cur = getPeersHead(); cur != NULL; cur = cur->next) {
			if(CBNetworkAddressEquals(cur->peer, broadcast->addresses[index])) {
				exists = true;
			}
		}
		if(!exists) {
			addNewPeerByCBNetworkAddress(broadcast->addresses[index]);	
		
		}
	}

		
}

void processGetBlocks(CBPeer *peer, uint8_t *buffer, uint32_t length) {

}

void processInv(CBPeer *peer, uint8_t *buffer, uint32_t length) {
	CBByteArray *serialisedData = CBNewByteArrayWithDataCopy(buffer, length);
	CBInventoryBroadcast *inventory = CBNewInventoryBroadcastFromData(serialisedData);
	CBInventoryBroadcastDeserialise(inventory);
	printf("Received %ul results in inventory\n", inventory->itemNum);
	
	sendGetData(peer, inventory);

}

void processGetData(CBPeer *peer, uint8_t *buffer, uint32_t length) {

}

void processBlocks(CBPeer *peer, uint8_t *buffer, uint32_t length) {

}

void processTx(CBPeer *peer, uint8_t *buffer, uint32_t length) {

}

/****************************************** 
 * Start of sending functins
 *****************************************/

void sendVersion(CBPeer *peer) {
	uint32_t vers = 70001;
	uint32_t len;
	uint64_t nonce = rand();
	CBByteArray *ua = CBNewByteArrayFromString("cmsc417versiona", "\00");
	CBVersion *version = CBNewVersion(vers, CB_SERVICE_FULL_BLOCKS, time(NULL), &peer->base, getLocalAddress(), nonce, ua, 0);
	CBMessage *message = CBGetMessage(version);
	len = CBVersionCalculateLength(version);
	message->bytes = CBNewByteArrayOfSize(len);
	len = CBVersionSerialise(version, false);
	if(message->bytes) {
		uint8_t hash[32];
		uint8_t hash2[32];
		CBSha256(CBByteArrayGetData(message->bytes), message->bytes->length, hash);
		CBSha256(hash, 32, hash2);
		memcpy(message->checksum, hash2, 4);
	} 
	message->type = CB_MESSAGE_TYPE_VERSION;
	peer->versionSent = true;
	sendPeerMessage(peer, message);	

}     

void sendPing(CBPeer *peer) {
	peer->sentHeader = true;
	CBMessage *message = CBNewMessageByObject();
	message->bytes = CBNewByteArrayWithDataCopy((uint8_t[8]){rand(), rand()}, 8);
	uint8_t hash[32];
	uint8_t hash2[32];

	CBSha256(CBByteArrayGetData(message->bytes), 8, hash);
	CBSha256(hash, 32, hash2);
	memcpy(message->checksum, hash2, 4);
	message->type = CB_MESSAGE_TYPE_PING;
	sendPeerMessage(peer, message);
} 

void sendVerAck(CBPeer *peer) {

}

void sendPong(CBPeer *peer, uint8_t *buffer, uint32_t length) {

}

void sendGetAddr(CBPeer *peer) {
	CBMessage *message = CBNewMessageByObject();
	message->bytes = CBNewByteArrayOfSize(0);
	uint8_t hash[32];
	uint8_t hash2[32];
	uint32_t nonce = rand();

	CBSha256((uint8_t *)&nonce, 0, hash);
	CBSha256(hash, 32, hash2);
	message->type = CB_MESSAGE_TYPE_GETADDR;	
	memcpy(message->checksum, hash2, 4);
	peer->getAddresses = true;
	sendPeerMessage(peer, message);
}

void sendAddr(CBPeer *peer) {
	CBAddressBroadcast *addrBroadcast = CBNewAddressBroadcast(true);
	CBMessage *message;
	int32_t len;
	int8_t hash[32], hash2[32];
	peerList *cur; 
	for(cur = getPeersHead(); cur != NULL && addrBroadcast->addrNum < 30; cur = cur->next) {
		if(cur->peer->connectionWorking) {
			CBAddressBroadcastTakeNetworkAddress(addrBroadcast, &cur->peer->base);
		}
	}

	len = CBAddressBroadcastCalculateLength(addrBroadcast);
	message = CBGetMessage(addrBroadcast);
	message->bytes = CBNewByteArrayOfSize(len);
	CBAddressBroadcastSerialise(addrBroadcast, false);
	CBSha256(CBByteArrayGetData(message->bytes), message->bytes->length, hash);
	CBSha256(hash, 32, hash2);
	memcpy(message->checksum, hash2, 4);
	message->type = CB_MESSAGE_TYPE_ADDR;

}

void sendGetBlocks(CBPeer *peer) {
	uint32_t vers = 32;
	uint8_t hash[32], hash2[32];
	CBChainDescriptor *descr = CBNewChainDescriptor();
	CBBlock *myGen = CBNewBlockGenesis();
	CBByteArray *genHash = CBNewByteArrayWithDataCopy(myGen->hash, 32);
	CBChainDescriptorAddHash(descr, genHash);
	uint8_t *unknown = calloc(32,sizeof(uint8_t));
	CBByteArray *unknownHash = CBNewByteArrayWithDataCopy(unknown, 32);
	CBGetBlocks *getBlocks = CBNewGetBlocks(vers, descr, unknownHash);
	uint32_t len = CBGetBlocksCalculateLength(getBlocks);
	CBMessage *message = CBGetMessage(getBlocks);
	message->bytes = CBNewByteArrayOfSize(len);
	CBGetBlocksSerialise(getBlocks, false);
	
	CBSha256(CBByteArrayGetData(message->bytes), message->bytes->length, hash);
	CBSha256(hash, 32, hash2);
	memcpy(message->checksum, hash2, 4);
	message->type = CB_MESSAGE_TYPE_GETBLOCKS;

	sendPeerMessage(peer, message);
}

void sendInv(CBPeer *peer) {

}

void sendGetData(CBPeer *peer, CBInventoryBroadcast *inv) {
	CBMessage *message = CBGetMessage(inv);
	message->type = CB_MESSAGE_TYPE_GETDATA;
	
	sendPeerMessage(peer, message);
}

void sendBlock(CBPeer *peer) {

}

void sendTx(CBPeer *peer) {

}


void createFullValidator() {
	validator = CBNewFullValidator(2 ,false, 0);	
}
