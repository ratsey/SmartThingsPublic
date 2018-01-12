/**
 *  Pi Security Module
 *
 *  Copyright 2017 Jens Ratsey-Woodroffe
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */
 
 // Uses Raspberry Pi Zero W with GPIO expansion hat to replace an existing hard wired security system
 
metadata {
	definition (name: "Pi Security Module", namespace: "ratsey", author: "Jens Ratsey-Woodroffe") {
		capability "Contact Sensor"
        attribute "contact", "string"
	}

    // when setting up your device via the smartthings app, these preference settings are available
    preferences {
        // the LAN IP address and port for the server for example: 192.168.0.100:8080
        input("host", "string", title:"Host", description: "The IP address and port of the Raspberry Pi host", required: true, displayDuringSetup: true)
        
        // The MAC address of the Pi server.  Required for the hub to send LAN commands
        input("macid", "string", title:"Mac Address", description: "The MAC address of the Raspberry Pi host", required: true, displayDuringSetup: true)
        
        // The GPIO sensor to use
        input("gpio", "number", title:"GPIO Pin", description: "The pin on the Pi host to monitor", required: true, displayDuringSetup: true)
        
	}
    
	simulator {
		// TODO: define status and reply messages here
	}

	tiles {
		// TODO: define your main and details tiles here
	}
}

// parse events into attributes
def parse(String description) {
	log.debug "Parsing '${description}'"
	// TODO: handle 'contact' attribute

	def msg = parseLanMessage(description)
    def body = msg.body              // => request body as a string
    
    sendEvent(name: "contact", value: msg.body, isStateChange: true)
}

// 
def getHostAddress() {    
	return "${host}"
}

//
def getMacID() {
	return "${macid}"
}

// 
private http_command(uri) {
	log.debug("Executing hubaction ${uri} on " + getHostAddress())
	device.setDeviceNetworkId(MacID)
    
    def hubAction = new physicalgraph.device.HubAction(
    	[
    		method: "GET",
        	path: uri,
        	headers: [HOST:getHostAddress()]
        ],
        MacID
        )

    return hubAction
}

// Get pin state
def getPinState() {
	def cmds = []
	cmds << http_command("/gpio/" + gpio)
    log.debug cmds
    
    sendHubCommand(cmds)
}