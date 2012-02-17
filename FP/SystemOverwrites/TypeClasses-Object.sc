
+ Object {	
	
	getTypeInstance { |typeclass, class, function, f|
		//has to be check superclass also, and find first that matches.
		^TypeClasses.dict.at(class) !? _.at(typeclass) !? _.at(function) !? f
			?? { (class.asString ++" is not a instance of the "++typeclass++" typeclass" ).warn };
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
		^this.getTypeInstance('Monad', this.class, 'pure', { |g| g.value(this) });
	}
	
	//Monoid
	|+|{ |a| }

	//Traverse
	traverse { |f| ^this.getTypeInstance('Traverse', this.class, 'traverse', { |g| g.value(f, this) }) }
	sequence { ^this.traverse({|x| x}) }

}
