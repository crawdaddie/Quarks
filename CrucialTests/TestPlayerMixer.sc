
TestPlayerMixer : TestAbstractPlayer {
	
	makePlayer {
		var a;
		Instr(\testSine,{arg freq=1000,mul=0.1; SinOsc.ar(freq,0,mul).dup});
		a=PlayerMixer([
			Patch(\testSine,[400,0.1]),
			Patch(\testSine,[800,0.08]),
			Patch(\testSine,[1600,0.06])
		]);
		^a
	}
	makeBus { ^Bus.audio(s,2) }
	test_startStopStartStop {
		this.startStopStart;
	}

}

/*
)

a.play;

a.addPlayer(Patch(\testSine,[2000,0.2]));
a.addPlayer(Patch(\testSine,[2400,0.15]));
a.addPlayer(Patch(\testSine,[2800,0.1]));

a.addPlayer( Patch(\testSine, [rrand(400,4000),0.1 ] ) );

*/

