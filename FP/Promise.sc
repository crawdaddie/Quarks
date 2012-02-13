+ Function{
	
	promise{ 
		var es = EventSource();
		fork{ es.fire(this.value) };
		^es	
	}	
	
}