
//For comprehensions based on scala's for and haskell's do.
For {

	*new { |... funcs|
		var recursiveFunc = { |prevValues,funcs|
			
			if(funcs.size == 2) {
				var v = funcs[0];
				if(v.size == 2) {
					v = v[0].value(*prevValues).select(v[1])
				}{
					v = v.value(*prevValues)	
				}; 
				v.fmap{ |x|
					funcs[1].value(*([x]++prevValues))
				}	
			} {
				var v = funcs.first;
				if(v.size == 2) {
					v = v[0].value(*prevValues).select(v[1])
				}{
					v = v.value(*prevValues)		
				};
				v >>= { |x| recursiveFunc.([x]++prevValues,funcs[1..]) }
			}
		};
		funcs = funcs.collect{ |x| if((x.class == Function) || (x.size > 1)){x}{ { x } } };
		if(funcs.size > 1) {		
			^recursiveFunc.([],funcs)				
		} {		
			"For needs at least two functions".warn
		}		
	}	
	
}