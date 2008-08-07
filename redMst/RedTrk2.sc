//redFrik - released under gnu gpl license

RedTrk2 : RedTrk {
	initRedTrk {|argKey, argItem, argSections|
		var channels, rate;
		key= argKey;
		if(argItem.isKindOf(Pbind), {
			argItem.patternpairs.pairsDo{|key, pat|
				var desc;
				if(key==\instrument, {
					desc= SynthDescLib.global.synthDescs[pat];
					if(desc.notNil, {
						channels= desc.outputs[0].numberOfChannels;//is this correct?
						rate= desc.outputs[0].rate;	//is this correct?
					});
				});
			};
			item= Pbus(argItem, 0, 0.02, channels ? 2, rate ? \audio);
		}, {
			item= argItem;
		});
		sections= argSections.asSequenceableCollection;
		RedMst.add(this);
	}
}
