/**
 * A Signal in FRP represents a continuous value.
 *
 * Here it is represented by the FPSignal trait, which is currently implemented in terms of a 'now' value
 * and a 'change' event stream. Transformations are implemented around those two members.
 *
 * To obtain a signal, see Var, Val, Timer, and BufferSignal. In addition, new signals can be derived from
 * existing signals using the transformation methods defined in this trait.
 * @param T the type of value this signal contains
 */
FPSignal {

	now { }

	change { } //returns EventStream

	do { |f|
		f.(this.now);
		this.change.do(f)
	}

	collect { |f|
        ^CollectedSignal(this,f)
    }

    select { |f|
        ^SelectedSignal(this, f)
    }

    fold { |initial, f|
        ^FoldedSignal(this, initial, f);
    }

    flatCollect { |f, initialState|
        ^FlatCollectedSignal( this, f, initialState)
    }

    | { |otherSignal|
        ^MergedSignal( this, otherSignal )
    }

    takeWhile { |f|
        ^TakeWhileSignal( this, f)
    }

}

SignalChangeES : EventSource {
	var ref;

	*new { |handler| ^super.new.initSignalChange(handler) }

    initSignalChange { |handler|
    	ref = handler
    }
}

ChildFPSignal : FPSignal {
    var <state;
    var <parent;
    var <listenerFunc;
    var <handler; //: (T, S) => S
    var <change;
    var <now;

    *new{ |initialState|
        ^super.new.initState( initialState )
    }

    initState{ |initialState|
        state = initialState;
        change = SignalChangeES();
    }

    initChildFPSignal { |p,h|
        parent = p;
        handler = h;
        listenerFunc = { |value| state = handler.value(value, state) };
        parent.change.addListener( listenerFunc )
    }

}


CollectedFPSignal : ChildFPSignal {

    *new { |parent, f|
        ^super.new.init(parent, f)
    }

    init { |parent, f|
        this.initChildFPSignal(parent, { |event|
            var x = f.(event);
            now = x;
            change.fire( x );
        })
    }
}

FlatCollectedFPSignal : ChildFPSignal {

    *new { |parent, f, initial|
        ^super.new(initial).init(parent, f)
    }

    init { |parent, f|
        var thunk = { |event|
        	var x = f.(event);
            now = x;
            change.fire( x );
        };
        state !? _.addListener(thunk);
        this.initChildFPSignal(parent, { |event, lastFPSignal|
             var nextFPSignal;
             lastFPSignal.change !? _.removeListener( thunk );
             nextFPSignal = f.(event);
             thunk.( nextFPSignal.now );
             nextFPSignal.change !? _.addListener( thunk );
             nextFPSignal
        })
    }
}

//A signal that never changes, and therefore never fires anything;
Val : FPSignal {
	var <now;
	var <change;

	*new { |now| ^super.newCopyArgs(now).init }

	init {
		change = EventSource();
	}
}

Var : Val {

    value { ^now }

    value_ { |v|
    	now = v;
    	change.fire(v);
    }

}