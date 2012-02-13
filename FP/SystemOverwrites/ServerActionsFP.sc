+ SynthDef {

	addES { |libname, keepDef = true|
	    /*var x = EventSource();
	    var	servers, desc = this.asSynthDesc(libname ? \global, keepDef);
		if(libname.isNil) {
			servers = Server.allRunningServers
		} {
			servers = SynthDescLib.getLib(libname).servers
		};
		servers.collect { |each|
			this.sendP(each.value)
		}.sequence; //sequence not implemented yet
		Task{
            // HERE you do the b.read or SynthDef().add or whatever
            s.sync;
            // HERE you do whatever is next
        }.play;
        */
	}

	sendES { |server|
		var eventsource = EventSource();
		Task{
			this.send(server);
			server.sync;
			"sent".postln;
			eventsource.fire(this);
		}.play;
		^eventsource
	}

}

+ Buffer {
	*readES { arg server,path,startFrame = 0,numFrames = -1, bufnum;
		var es = EventSource();
    	this.read(server,path,startFrame = 0,numFrames = -1, { es.fire(this) }, bufnum);
    	^es
    }
}

+ Server {

	bootES {
		var x = EventSource();
		this.doWhenBooted({ x.fire(this) });
		this.boot;
		^x
	}

}