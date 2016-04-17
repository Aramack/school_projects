
#include <stdio.h>
#include <netinet/in.h>
#include <string.h>
#include	<CBVersion.h>
#include <CBNetworkAddress.h>
#include <CBMessage.h>
#include <CBPeer.h>
#include <CBByteArray.h>
#include <time.h>
#include <sys/select.h>
#include	"client.h"
#include "NetworkFunctions.h"
#include "CommandFunctions.h"


int main(int argc, char *argv[]) {  
	CBByteArray *umdIP = CBNewByteArrayWithDataCopy((uint8_t [16]){0,0,0,0,0,0,0,0,0,0,0,0,128,8,126,25}, 16);
	CBNetworkAddress *umdAddress = CBNewNetworkAddress(0, umdIP, 28333, CB_SERVICE_FULL_BLOCKS, false);
	startListeningForConnection();
	createFullValidator();
	addNewPeerByCBNetworkAddress(umdAddress);
	monitorSockets();	
	return 1;
}
