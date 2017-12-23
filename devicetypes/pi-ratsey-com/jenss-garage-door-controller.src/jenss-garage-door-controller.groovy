/**
 *  Copyright 2015 SmartThings
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
metadata {
    //
	definition (name: "Jens's Garage door Controller", namespace: "pi.ratsey.com", author: "Jens Ratsey-Woodroffe") {
		capability "garageDoorControl"
        capability "Polling"
        capability "Refresh"
        capability "ContactSensor"

        attribute "contact", "string"
        attribute "door", "string"

		command "open"
        command "close"

		command "leftDoor"
		command "centreDoor"
	}

    // when setting up your device via the smartthings app, these preference settings are available
    preferences {
        // the LAN IP address and port for the server for example: 192.168.0.100:8080
        input("host", "string", title:"Host", description: "The IP address and port of the Raspberry Pi.", required: true, displayDuringSetup: true)
        
        // The MAC address of the Pi server.  Required for the hub to send LAN commands
        input("macid", "string", title:"Mac Address", description: "The MAC address of the Raspberry Pi.", required: true, displayDuringSetup: true)
	}

    // these are the tiles displayed in the smartthings app when you setup this device
	tiles(scale:2) {
       
        standardTile("door1", "device.leftDoor", width: 3, height: 3) {
        	state("open", label: "Open", backgroundColor: "#00A0DC", action: "leftDoor", icon:"st.doors.garage.garage-open", nextState: "closing")
            state("closed", label: "Closed", backgroundColor: "#e86d13", action: "leftDoor", icon:"st.doors.garage.garage-closed", nextState: "opening")
            state("opening", label: "Opening", backgroundColor: "#ed8a00", action: "leftDoor", icon:"st.doors.garage.garage-opening")
            state("closing", label: "Closing", backgroundColor: "#ed8a00", action: "leftDoor", icon:"st.doors.garage.garage-closing")
		}
        
        standardTile("door2", "device.centreDoor", width: 3, height: 3) {
        	state("open", label: "Open", backgroundColor: "#00A0DC", action: "centreDoor", icon:"st.doors.garage.garage-open", nextState: "closing")
            state("closed", label: "Closed", backgroundColor: "#e86d13", action: "centreDoor", icon:"st.doors.garage.garage-closed", nextState: "opening")
            state("opening", label: "Opening", backgroundColor: "#ed8a00", action: "centreDoor", icon:"st.doors.garage.garage-opening")
            state("closing", label: "Closing", backgroundColor: "#ed8a00", action: "centreDoor", icon:"st.doors.garage.garage-closing")
		}
        
        standardTile("sRefresh", "device.doorStatus", inactiveLabel: false, decoration: "flat") {
        	state "default", label: '', action:"refresh.refresh", icon:"st.secondary.refresh"
        }
	}
}

//
def installed() {
    updated()
}

//
def updated() {
    log.debug "updated"
    unschedule(getDoorsState)
    runEvery5Minutes(getDoorsState)
    runIn(1, getDoorsState)
}

//
def poll() {
	log.debug "My Garage Door Polling"   
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
def leftDoor() {
	def cmds = []
	cmds << http_command("/door/door1/activate")
    log.debug cmds
    
    sendHubCommand(cmds)
    runIn(18, getDoorsState)
}

//
def centreDoor() {
	def cmds = []
	cmds << http_command("/door/door2/activate")
    log.debug cmds
    
    sendHubCommand(cmds)
    runIn(18, getDoorsState)
}

//
def open() {
	log.debug "open command received.   Door1=${state.door1} Door2=${state.door2}"
  
    if (state.door1 == "closed")
    	leftDoor()
    
    if (state.door2 == "closed")
    	centreDoor()
}

//
def close() {
	log.debug "close command received.  Door1=${state.door1} Door2=${state.door2}"
    
    if (state.door1 == "open")
    	leftDoor()
    
    if (state.door2 == "open")
    	centreDoor()
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

//
def refresh() {
	log.debug "Refreshing door state"
    getDoorsState()
}

//
def getDoorsState() {
	def cmds = []
    log.debug("Fetching door states")
    cmds << http_command("/status")
    
    sendHubCommand(cmds)
}

//
def parse(String description) {
	def msg = parseLanMessage(description)

    def headersAsString = msg.header // => headers as a string
    def headerMap = msg.headers      // => headers as a Map
    def body = msg.body              // => request body as a string
    def status = msg.status          // => http status code of the response
    def json = msg.json              // => any JSON included in response body, as a data structure of lists and maps
    def xml = msg.xml                // => any XML included in response body, as a document tree structure
    def data = msg.data              // => either JSON or XML in response body (whichever is specified by content-type header in response)
	
    log.debug(data)
	 
    try {
		def object = new groovy.json.JsonSlurper().parseText(body)
	            
	    log.debug("Door " + object[0]["door"] + ':' + object[0]["state"])
	    log.debug("Door " + object[1]["door"] + ':' + object[1]["state"])
        
	    sendEvent(name: "leftDoor", value: object[0]["state"], isStateChange: true)
		sendEvent(name: "centreDoor", value: object[1]["state"], isStateChange: true)

		state.door1 = object[0]["state"]
    	state.door2 = object[1]["state"]
        
        // Set the overall garage door state in case we need to send an intruder alert
        if (state.door1 == "open" || state.door2 == "open") {
        	state.overall = "open"
        }
        else {
        	state.overall = "closed"
        }
        
        sendEvent(name: "contact", value: state.overall, isStateChange: true)
        sendEvent(name: "door", value: state.overall, isStateChange: true)
    }
    catch (Exception e) {
    	//log.error(e)
    }
}

