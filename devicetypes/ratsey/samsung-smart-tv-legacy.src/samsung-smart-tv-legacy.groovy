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
 *  Samsung TV [Legacy]
 *
 *  Author: SmartThings (juano23@gmail.com)
 *  Date: 2015-01-08
 *
 *  Updates:
 *	2017-12-30	jens@ratsey.com	Adapted for legacy Samsung TVs.  Requires partner Raspberry Pi app
 *
 *	For Samsung key codes refer https://github.com/Bntdumas/SamsungIPRemote/blob/master/samsungKeyCodes.txt
 */
 
metadata {
	definition (name: "Samsung Smart TV [Legacy]", namespace: "ratsey", author: "Jens Ratsey-Woodroffe") {
    		capability "switch" 
            
			command "mute" 
			command "source"
			command "menu"    
            command "tools"           
			command "HDMI"    
            command "Sleep"
            command "Up"
            command "Down"
            command "Left"
            command "Right" 
			command "chup" 
 			command "chdown"               
			command "prech"
			command "volup"    
            command "voldown"           
            command "Enter"
            command "Return"
            command "Exit"
            command "Info"            
            command "Size"
	}

    preferences {
        // the LAN IP address and port for the server for example: 192.168.0.100:8080
        input("host", "string", title:"Host", description: "The IP address and port of the Raspberry Pi.", required: true, displayDuringSetup: true)
        
        // The MAC address of the Pi server.  Required for the hub to send LAN commands
        input("macid", "string", title:"Mac Address", description: "The MAC address of the Raspberry Pi.", required: true, displayDuringSetup: true)
        
        input("tv", "string", title:"TV IP Address", description: "The IP address of the TV to control.", required: true, displayDuringSetup: true)
	}

    standardTile("switch", "device.switch", width: 1, height: 1, canChangeIcon: true) {
        state "default", label:'TV', action:"switch.off", icon:"st.Electronics.electronics15", backgroundColor:"#ffffff"
    }
    standardTile("power", "device.switch", width: 1, height: 1, canChangeIcon: false) {
        state "default", label:'', action:"switch.off", decoration: "flat", icon:"st.thermostat.heating-cooling-off", backgroundColor:"#ffffff"
    }    
    standardTile("mute", "device.switch", decoration: "flat", canChangeIcon: false) {
        state "default", label:'Mute', action:"mute", icon:"st.custom.sonos.muted", backgroundColor:"#ffffff"
    }    
	standardTile("source", "device.switch", decoration: "flat", canChangeIcon: false) {
        state "default", label:'Source', action:"source", icon:"st.Electronics.electronics15"
    }
	standardTile("tools", "device.switch", decoration: "flat", canChangeIcon: false) {
        state "default", label:'Tools', action:"tools", icon:"st.secondary.tools"
    }
	standardTile("menu", "device.switch", decoration: "flat", canChangeIcon: false) {
        state "default", label:'Menu', action:"menu", icon:"st.vents.vent"
    }
	standardTile("HDMI", "device.switch", decoration: "flat", canChangeIcon: false) {
        state "default", label:'Source', action:"HDMI", icon:"st.Electronics.electronics15"
    }
    standardTile("Sleep", "device.switch", decoration: "flat", canChangeIcon: false) {
        state "default", label:'Sleep', action:"Sleep", icon:"st.Bedroom.bedroom10"
    }
    standardTile("Up", "device.switch", decoration: "flat", canChangeIcon: false) {
        state "default", label:'Up', action:"Up", icon:"st.thermostat.thermostat-up"
    }
    standardTile("Down", "device.switch", decoration: "flat", canChangeIcon: false) {
        state "default", label:'Down', action:"Down", icon:"st.thermostat.thermostat-down"
    }
    standardTile("Left", "device.switch", decoration: "flat", canChangeIcon: false) {
        state "default", label:'Left', action:"Left", icon:"st.thermostat.thermostat-left"
    }
    standardTile("Right", "device.switch", decoration: "flat", canChangeIcon: false) {
        state "default", label:'Right', action:"Right", icon:"st.thermostat.thermostat-right"
    }  
	standardTile("chup", "device.switch", decoration: "flat", canChangeIcon: false) {
        state "default", label:'CH Up', action:"chup", icon:"st.thermostat.thermostat-up"
    }
	standardTile("chdown", "device.switch", decoration: "flat", canChangeIcon: false) {
        state "default", label:'CH Down', action:"chdown", icon:"st.thermostat.thermostat-down"
    }
	//standardTile("prech", "device.switch", decoration: "flat", canChangeIcon: false) {
    //    state "default", label:'Pre CH', action:"prech", icon:"st.secondary.refresh-icon"
    //}
    standardTile("volup", "device.switch", decoration: "flat", canChangeIcon: false) {
        state "default", label:'Vol Up', action:"volup", icon:"st.thermostat.thermostat-up"
    }
    standardTile("voldown", "device.switch", decoration: "flat", canChangeIcon: false) {
        state "default", label:'Vol Down', action:"voldown", icon:"st.thermostat.thermostat-down"
    }
    standardTile("Enter", "device.switch", decoration: "flat", canChangeIcon: false) {
        state "default", label:'Enter', action:"Enter", icon:"st.illuminance.illuminance.dark"
    }
    standardTile("Return", "device.switch", decoration: "flat", canChangeIcon: false) {
        state "default", label:'Return', action:"Return", icon:"st.secondary.refresh-icon"
    }
    standardTile("Exit", "device.switch", decoration: "flat", canChangeIcon: false) {
        state "default", label:'Exit', action:"Exit", icon:"st.locks.lock.unlocked"
    }    
    standardTile("Info", "device.switch", decoration: "flat", canChangeIcon: false) {
        state "default", label:'Info', action:"Info", icon:"st.motion.acceleration.active"
    }    
    //standardTile("Size", "device.switch", decoration: "flat", canChangeIcon: false) {
    //    state "default", label:'Picture Size', action:"Size", icon:"st.contact.contact.open"
    //}      
    main "switch"
    details (["power","HDMI","Sleep","chup","prech","volup","chdown","mute","voldown", "menu", "Up", "tools", "Left", "Enter", "Right", "Return", "Down", "Exit", "Info","Size"])	
}

def parse(String description) {

	def msg = parseLanMessage(description)

    def headersAsString = msg.header // => headers as a string
    def headerMap = msg.headers      // => headers as a Map
    def body = msg.body              // => request body as a string
    def status = msg.status          // => http status code of the response
    def json = msg.json              // => any JSON included in response body, as a data structure of lists and maps
    def xml = msg.xml                // => any XML included in response body, as a document tree structure
    def data = msg.data              // => either JSON or XML in response body (whichever is specified by content-type header in response)

}

//
def getMacID() {
	return "${macid}"
}

// 
def getHostAddress() {    
	return "${host}"
}

// 
def getTV() {    
	return "${tv}"
}

// 
private http_command(uri) {
	log.debug("Executing hubaction ${uri} on " + getHostAddress() + " DeviceID: " + getMacID())
	device.setDeviceNetworkId(getMacID())
    
    def hubAction = new physicalgraph.device.HubAction(
    	[
    		method: "GET",
        	path: uri,
        	headers: [HOST:getHostAddress()]
        ],
        getMacID()
        )

    return hubAction
}

def tvAction(action) {
	def cmds = []
	cmds << http_command("/action/" + action)
    log.debug cmds
    
    sendHubCommand(cmds)
}

def off() {
	log.debug "Turning TV OFF"
    //parent.tvAction("POWEROFF",device.deviceNetworkId) 
    tvAction("KEY_POWEROFF")
    sendEvent(name:"Command", value: "Power Off", displayed: true) 
}

def mute() {
	log.trace "MUTE pressed"
    //parent.tvAction("MUTE",device.deviceNetworkId) 
    tvAction("KEY_MUTE")
    sendEvent(name:"Command", value: "Mute", displayed: true) 
}

def source() {
	log.debug "SOURCE pressed"
    //parent.tvAction("SOURCE",device.deviceNetworkId) 
    tvAction("KEY_DTV")
    sendEvent(name:"Command", value: "Source", displayed: true) 
}

def menu() {
	log.debug "MENU pressed"
    //parent.tvAction("MENU",device.deviceNetworkId) 
    tvAction("KEY_MENU")
}

def tools() {
	log.debug "TOOLS pressed"
    //parent.tvAction("TOOLS",device.deviceNetworkId) 
    tvAction("KEY_TOOLS")
    sendEvent(name:"Command", value: "Tools", displayed: true)     
}

def HDMI() {
	log.debug "HDMI pressed"
    //parent.tvAction("HDMI",device.deviceNetworkId) 
    tvAction("KEY_HDMI")
    sendEvent(name:"Command sent", value: "Source", displayed: true)
}

def Sleep() {
	log.debug "SLEEP pressed"
    //parent.tvAction("SLEEP",device.deviceNetworkId) 
   
    sendEvent(name:"Command", value: "Sleep", displayed: true)
}

def Up() {
	log.debug "UP pressed"
    //parent.tvAction("UP",device.deviceNetworkId)
    tvAction("KEY_UP")
}

def Down() {
	log.debug "DOWN pressed"
    //parent.tvAction("DOWN",device.deviceNetworkId) 
    tvAction("KEY_DOWN")
}

def Left() {
	log.debug "LEFT pressed"
    //parent.tvAction("LEFT",device.deviceNetworkId) 
    tvAction("KEY_LEFT")
}

def Right() {
	log.debug "RIGHT pressed"
    //parent.tvAction("RIGHT",device.deviceNetworkId) 
    tvAction("KEY_RIGHT")
}

def chup() {
	log.debug "CHUP pressed"
    //parent.tvAction("CHUP",device.deviceNetworkId)
    tvAction("KEY_CHUP")
    sendEvent(name:"Command", value: "Channel Up", displayed: true)         
}

def chdown() {
	log.debug "CHDOWN pressed"
    //parent.tvAction("CHDOWN",device.deviceNetworkId) 
    tvAction("KEY_CHDOWN")
    sendEvent(name:"Command", value: "Channel Down", displayed: true)     
}

def prech() {
	log.debug "PRECH pressed"
    //parent.tvAction("PRECH",device.deviceNetworkId)    
    sendEvent(name:"Command", value: "Prev Channel", displayed: true)       
}

def Exit() {
	log.debug "EXIT pressed"
    //tvAction("KEY_RETURN")
    //parent.tvAction("EXIT",device.deviceNetworkId) 
}

def volup() {
	log.debug "VOLUP pressed"
    //parent.tvAction("VOLUP",device.deviceNetworkId)
    tvAction("KEY_VOLUP")
    sendEvent(name:"Command", value: "Volume Up", displayed: true)         
}

def voldown() {
	log.debug "VOLDOWN pressed"
    //parent.tvAction("VOLDOWN",device.deviceNetworkId) 
    tvAction("KEY_VOLDOWN")
    sendEvent(name:"Command", value: "Volume Down", displayed: true)         
}

def Enter() {
	log.debug "ENTER pressed"
    //parent.tvAction("ENTER",device.deviceNetworkId) 
    tvAction("KEY_ENTER")
}

def Return() {
	log.debug "RETURN pressed"
    //parent.tvAction("RETURN",device.deviceNetworkId) 
    tvAction("KEY_RETURN")
}

def Info() {
	log.debug "INFO pressed"
    //parent.tvAction("INFO",device.deviceNetworkId) 
    tvAction("KEY_INFO")
	sendEvent(name:"Command", value: "Info", displayed: true)    
}

def Size() {
	log.debug "PICTURE_SIZE pressed"
    //parent.tvAction("PICTURE_SIZE",device.deviceNetworkId) 
    sendEvent(name:"Command", value: "Picture Size", displayed: true)
}