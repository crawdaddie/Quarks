// Functional Reactive Programming
// based on reactive-core

EventSource {
    var <listeners;

    new{
        ^super.new.initEventSource
    }

    initEventSource {
        listeners = [];
    }

    addListener { |f| listeners = listeners ++ [f] }

    removeListener { |f| listeners.remove(f) }
    
    removeAllListeners { listeners = [] }

	proc { |initialState, f|
		^ChildEventSource( initialState ).initChildEventSource(this,f)
	}

    collect { |f|
        ^CollectedES(this,f)
    }

    select { |f|
        ^SelectedES(this, f)
    }

    fold { |initial, f|
        ^FoldedES(this, initial, f);
    }

    flatCollect { |f, initialState|
        ^FlatCollectedES( this, f, initialState)
    }

    | { |otherES|
        ^MergedES( this, otherES )
    }

    takeWhile { |f|
        ^TakeWhileES( this, f)
    }

    do { |f|
        this.addListener(f);
        ^nil
    }

    fire { |event|
	    //("running fire "++event++" "++this.hash).postln;
        listeners.copy.do( _.value(event) )
    }

	//returns the corresponding signal
    hold { |initalValue|
    	^HoldFPSignal(this, initalValue)
    }
}

HoldFPSignal : Signal {
	var <now;
	var <change;
	var <listener;

	*new { |eventStream, initialValue|
		^super.new.init(eventStream, initialValue)
	}

	init { |eventStream, initialValue|
		change = eventStream;
		now = initialValue;
		listener = { |v| now = v};
		eventStream.addListener( listener )
	}

}

ChildEventSource : EventSource {
    var <state;
    var <parent;
    var <listenerFunc;
    var <handler; //: (T, S) => S

    *new{ |initialState|
        ^super.new.initState( initialState )
    }

    initState{ |initialState|
        state = initialState;
    }

    initChildEventSource { |p,h|
        parent = p;
        handler = h;
        listenerFunc = { |value| state = handler.value(value, state) };
        parent.addListener(listenerFunc)
    }

    remove {
        listeners.do( _.removeListener( listenerFunc ) )
    }
}

CollectedES : ChildEventSource {

    *new { |parent, f|
        ^super.new.init(parent, f)
    }

    init { |parent, f|
        this.initChildEventSource(parent, { |event|
            this.fire( f.(event) );
        })
    }
}

SelectedES : ChildEventSource {

    *new { |parent, f|
        ^super.new.init(parent, f)
    }

    init { |parent, f|
        this.initChildEventSource(parent, { |event|
             if( f.(event) ) {
                 this.fire( event )
             }
        })
    }
}

FoldedES : ChildEventSource {

    *new { |parent, initial, f|
        ^super.new(initial).init(parent, f)
    }

    init { |parent, f|
        this.initChildEventSource(parent, { |event, state|
             var next = f.(state, event);
             this.fire( next );
             next
        })
    }
}

FlatCollectedES : ChildEventSource {

    *new { |parent, f, initial|
        ^super.new(initial).init(parent, f)
    }

    init { |parent, f|
        var thunk = { |x|
         	//"firing the FlatCollectedES".postln;
         	this.fire(x) 
        };
        state !? _.addListener(thunk);
        this.initChildEventSource(parent, { |event, lastES|
             var nextES;
             lastES !? _.removeListener( thunk );
             nextES = f.(event);
             nextES !? _.addListener( thunk );
             nextES
        })
    }
}

TakeWhileES : ChildEventSource {

    *new { |parent, f|
        ^super.new.init(parent, f)
    }

    init { |parent, f|
        this.initChildEventSource(parent, { |event|
            if( f.(event) ) {
                this.fire( event )
            } {
                parent.removeListener(listenerFunc);
            }
        })
    }
}

MergedES : EventSource {

    *new { |parent, parent2|
        ^super.new.init(parent, parent2)
    }

    init { |parent1, parent2|
        var thunk = this.fire(_);
        parent1.addListener( thunk );
        parent2.addListener( thunk );
    }
}

TimerES : EventSource {
    var <routine;

    *new{ |delta, maxTime|
        ^super.new.init(delta, maxTime)
    }

    init { |delta, maxTime|

        var t = 0;
        routine = fork{
            100.do{
                delta.wait;
                if( t > maxTime) {
                    routine.stop;
                };
                t = t + delta;
                this.fire(t);
            }
        }

    }
}

