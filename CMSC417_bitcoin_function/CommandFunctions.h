
struct knownHashes{ 
	CBByteArray *hash;
	struct knownHashes *next;
} knownhashes;

void processVersion(CBPeer *peer, uint8_t *buffer, uint32_t length);
void processAck(CBPeer *peer, uint8_t *buffer, uint32_t length);
void processPing(CBPeer *peer, uint8_t *buffer, uint32_t length);
void processGetAddr(CBPeer *peer, uint8_t *buffer, uint32_t length);
void processAddr(CBPeer *peer, uint8_t *buffer, uint32_t length);
void processGetBlocks(CBPeer *peer, uint8_t *buffer, uint32_t length);
void processInv(CBPeer *peer, uint8_t *buffer, uint32_t length);
void processGetData(CBPeer *peer, uint8_t *buffer, uint32_t length);
void processBlocks(CBPeer *peer, uint8_t *buffer, uint32_t length);
void processTx(CBPeer *peer, uint8_t *buffer, uint32_t length);


void sendVersion(CBPeer *peer);
void sendVerAck(CBPeer *peer);
void sendPing(CBPeer *peer);
void sendPong(CBPeer *peer, uint8_t *buffer, uint32_t length);
void sendGetAddr(CBPeer *peer);
void sendAddr(CBPeer *peer);
void sendGetBlocks(CBPeer *peer);
void sendInv(CBPeer *peer);
void sendGetData(CBPeer *peer, CBInventoryBroadcast *inv);
void sendBlock(CBPeer *peer);
void sendTx(CBPeer *peer);

void **getMyBlockChain();
void createFullValidator();
