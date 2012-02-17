TypeClasses {

	classvar <dict;

	*initClass {

		dict = IdentityDictionary.new;

		dict.put(EventSource,
			(
				'Functor': ('fmap': { |fa,f| fa.collect(f) } ),
				'Monad' : ( 'bind' : { |fa,f| fa.flatCollect(f) } ),
				'Applic' : ('<*>' : { |fa,f| f.flatCollect{ |g| fa.fmap( g ) } } )
			);
		);
		dict.put(Option,
			(
				'Functor': ('fmap': { |fa,f| fa.collect(f) } ),
				'Monad' : (
					'bind' : { |fa,f| fa.flatCollect(f) },
					'pure' : { |a| Some(a) }
				),
				'Applic' : ('<*>' : { |fa,f| f >>= { |g| fa.fmap( g ) } } )
			);
		);
		dict.put(Array,
			(
				'Functor': ('fmap': { |fa,f| fa.collect(f) } ),
				'Monad' : (
					'bind' : { |fa,f| fa.collect(f).flatten },
					'pure' : { |a| [a] }
				),
				'Applic' : ('<*>' : { |fa,f| f >>= { |g| fa.fmap( g ) } } ),
				'Traverse' : ( 'traverse' : { |f,as|
				 	dict.at(f.(as[0]).class).at('Applic') !? { |x| x.at('<*>').postln } !? { |a|
				 		as.reverse.postln.inject([],{ |ys,x|
				 			a.(ys.postln, f.(x).fmap({ |z| { |zs| [z]++zs } }).postln )
				 		}).postln;
				 	}.postln ?? { "class of return values of f is not an Applicative Functor".warn }
				})

			);
		);
	}

}
