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