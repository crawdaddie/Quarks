SoundBlock {
	var <>color, <>ids;

	// fiducial ids of cubes
	classvar <>allIds;

	*initClass {
		allIds = (
			red:[
				[ 108, 109, 110, 111, 112, 113 ],
				[ 114, 115, 116, 117, 118, 119 ],
				[ 120, 121, 122, 123, 124, 125 ]
			],
			green:[
				[ 126, 127, 128, 129, 130, 131 ],
				[ 132, 133, 134, 135, 136, 137 ],
				[ 138, 139, 140, 141, 142, 143 ]
			],
			lightGreen:[
				[ 144, 145, 146, 147, 148, 149 ],
				[ 150, 151, 152, 153, 154, 155 ],
				[ 156, 157, 158, 159, 160, 161 ]
			],
			orange:[
				[ 162, 163, 164, 165, 166, 167 ],
				[ 168, 169, 170, 171, 172, 173 ],
				[ 174, 175, 176, 177, 178, 179 ]
			],
			blue:[
				[ 180, 181, 182, 183, 184, 185 ],
				[ 186, 187, 188, 189, 190, 191 ],
				[ 192, 193, 194, 195, 196, 197 ]
			],
			yellow:[
				[ 198, 199, 200, 201, 202, 203 ],
				[ 204, 205, 206, 207, 208, 209 ],
				[ 210, 211, 212, 213, 214, 215 ]
			]
			);
	}
		
	*basicNew{|color = \red, ids|
		^super.new.init(color, ids)
	}
	*new{|color = \red, number = 0|
		^this.basicNew(color, allIds[color][number])
	}
	init {|aColor, aIDs|
		color = aColor;
		ids   = aIDs;	
	}
	
	printOn { | stream |
		if (stream.atLimit) { ^this };
		stream << this.class.name << "( " << this.color.asCompileString << ", " << this.ids << " )" ;
	}
}

BufferBlock : SoundBlock {
	var <>buffers;
	var <>synth;
	//var nodeProxy;
	var <synthParams;
	var <out;
	
	// helper
	var ampTrigs = #[
				[1, 0, 0, 0, 0, 0],
				[0, 1, 0, 0, 0, 0],
				[0, 0, 1, 0, 0, 0],
				[0, 0, 0, 1, 0, 0],
				[0, 0, 0, 0, 1, 0],
				[0, 0, 0, 0, 0, 1]
			];
	
	*new{|color=\red, number=0, buffers, outChannel = 0|
		^super.new(color, number).initBuffers(buffers, outChannel)
	}

	initBuffers {|aBuffers, aOut|
		buffers = aBuffers;
		out = aOut;
		synthParams = (
/*			amp0: [0], // used to trigger
			amp1: [0],
			amp2: [0],
			amp3: [0],
			amp4: [0],
			amp5: [0],
*/			
			masterAmp: [0.1],
			rates: 1!6,
			amp: [0.1],
			interpolation: [2],
			masterMute: [0],
			mute: [0]
		);
	}




	play {|server|
		server = server ? Server.default;
		
		synth.isNil.if({
			server.bind{
				synth = Synth(\bbSynth, [\out, out], target: server).setn(\bufnums, buffers.collect(_.bufnum));
				synthParams.keysValuesDo{|key, value|
					synth.setn(key, value)
				}
			}
		}, {
			"% already playing".format(color).inform;	
		})
	}

	stop {
		synth.free;
		synth = nil;	
	}

	out_{|val|
		out = val;
		synth.notNil.if{
			synth.set(\out, val);	
		}	
	}
	
	set {|what=\rates, val = 0, side=0|
		(what == \amps).if{
			//synth.setn(what, (ampTrigs[side] * val));
			synth.set((what ++ side).asSymbol, val);
			^this // break	
		};
		synthParams[what][side] = val;
		synth.notNil.if{
			synth.setn(what, synthParams[what]);
		}
	}

	setFace {|id, what=\amps, val=1|
		this.set(what, val, ids.indexOf(id))
	}

/*	nodeProxy {|server|
		server = server ? Server.default;
		
		nodeProxy.isNil.if({
			nodeProxy = NodeProxy(server, \audio, 1);
			nodeProxy.source = \bbSynth;
			nodeProxy.setn(\bufnums, buffers.collect(_.bufnum).postln);
			synthParams.keysValuesDo{|key, value|
				nodeProxy.setn(key, value)
			}
		});
		^nodeProxy
	}

	play {
		this.nodeProxy.playN(out);
	}

	out_ {|val|
		out = val;
		nodeProxy.isPlaying.if({
			nodeProxy.playN(out)	
		})
	}

	stop {
		nodeProxy.stop;	
	}


	set {|what=\amps, val = 0, side=0|
		synthParams[what][side] = val;
		nodeProxy.notNil.if{
			nodeProxy.setn(what, synthParams[what]);
		}
	}
*/

	*sendSynth {
		SynthDef(\bbSynth, {|out = 0, amp = 0.1, mute = 0, masterAmp = 1, masterMute = 0, interpolation = 2, ampLagUp = 0.1, ampLagDown = 1|
			var bufnums = \bufnums.kr([1, 2, 3, 4, 5, 6]);
			

/*			var inAmps = \amps.tr(
				[0, 0, 0, 0, 0, 0], 0
			);
*/

			var inAmps = [
				\amps0.tr(1),
				\amps1.tr(1),
				\amps2.tr(1),
				\amps3.tr(1),
				\amps4.tr(1),
				\amps5.tr(1)
			];

			var rates = \rates.kr(
				[1, 1, 1, 1, 1, 1], 0.1
			);
			
			var amps = inAmps.collect{|amp, i| 
				EnvGen.kr(Env.perc(ampLagUp, ampLagDown), gate: amp-0.1, levelScale: amp);
				// amp.lagud(1, 1)
			};
			
			Out.ar(out, 
				(BufRd.ar(
					1, 
					bufnums, 
					Phasor.ar(
						0, BufRateScale.kr(bufnums) * rates, 
						0, 
						BufFrames.kr(bufnums)
					), 
					1, 
					interpolation
				) * amps).sum * amp * masterAmp * (1 - masterMute) * (1-mute)
			);
		}).memStore;
	}
}