TypeClasses {
	
	classvar <dict;
	
	*initClass {
		
		dict = IdentityDictionary.new;
		dict.put(Array,
			(
				'Functor': ('fmap': { |fa,f| fa.collect(f) } ),
				'Monad' : (
					'bind' : { |fa,f| fa.collect(f).flatten },
					'pure' : { |a| [a] }
				), 
				'Applic' : ('<*>' : { |fa,f| f >>= { |g| fa.fmap( g ) } } )
			);
		);		
	}	
	
}

AplicativeBuilder {
	var a, b;

	*new{ |a,b|
		^super.newCopyArgs(a,b)
	}
	
	| { |f|  ^( b <*>  ( a.fmap( f.curried ) ) ) }
	
	|*| { |c| ^AplicativeBuilder3(a,b,c) }
	
}

AplicativeBuilder3 {
	var a, b, c;

	*new{ |a,b,c|
		^super.newCopyArgs(a,b,c)
	}
	
	| { |f|  ^( c <*> (b <*> ( a.fmap( f.curried ) ) ) ) }
	
	|*| { |d| ^AplicativeBuilder4(a,b,c,d) }
	
}

AplicativeBuilder4 {
	var a, b, c, d;

	*new{ |a,b,c,d|
		^super.newCopyArgs(a,b,c,d)
	}
	
	| { |f|  ^( d <*> ( c <*> (b <*> ( a.fmap( f.curried ) ) ) ) ) }
		
}
	

+ Object {	
	
	getTypeInstance { |typeclass, class, function, f|
		//has to be check superclass also, and find first that matches.
		^TypeClasses.dict.at(class) !? _.at(typeclass) !? _.at(function) !? f ?? { (class.asString ++" is not a instance of the "++typeclass++" typeclass" ).warn };
	}
	
	//Functor
	fmap{ |f| 
		^this.getTypeInstance('Functor', this.class, 'fmap',  { |g| g.value(this, f) })
	}
	
	//Applicative Functor
	<*>{ |f| 
		^this.getTypeInstance('Applic', this.class, '<*>',  { |g| g.value(this, f) }) 
	}
	
	|*|{ |b|
		^AplicativeBuilder(this,b) 	
	}
			
	//Monad	
	>>={ |f|
		^this.getTypeInstance('Monad', this.class, 'bind', { |g| g.value(this, f) });
	}
	
	pure{ |class|
		^this.getTypeInstance('Monad', class, 'pure', { |g| g.value(this) });
	}
	
	//Monoid
	|+|{ |a| }	
	
	
}

+ Function {
	
	//partial application with unknown number of arguments
	pa { |arg1| ^{ arg ...args; this.value(arg1,*args) } }	
	curried1 { ^{ |x| this.pa(x) } }	
	curried2 { ^{ |x| { |y| this.pa(x).(y) } } }	
	curried3 { ^{ |x| { |y| {|z| this.pa(x).pa(y).(z) } } } }
	curried { 
		var r = { |f,n|
			var result;
			if(n == 1)Ê{
				{ |x| f.(x) }
			} {
				{|x|	r.value(f.pa(x),n-1) }
			}
		};
		^r.(this,this.def.argNames.size)
	}		
	
}

