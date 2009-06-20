/*

	Copyright 2009 (c) - Marije Baalman (nescivi)
	Part of the FileLog quark

	Released under the GNU/GPL license, version 2 or higher

*/

FilePlayer : FileReader {

	var <currentLine = 0;
	var lineMap;

	*new { | pathOrFile, skipEmptyLines=false, skipBlanks=false,  delimiter |
		var stream;
		if (pathOrFile.isKindOf(File) ) { stream = pathOrFile }  { stream =  File(pathOrFile, "r") }; 
		if (stream.isOpen.not) { warn("FileReader: file" + pathOrFile + "not found.") ^nil };
		^super.newCopyArgs(stream, skipEmptyLines, skipBlanks,  delimiter ? this.delim).myInit;
	}

	myInit{
		lineMap = Order.new;
		lineMap.put( 0, 0 );
	}

	reset{
		currentLine = 0;
		super.reset;
	}

	next{
		var res = super.next;
		if ( res.notNil ){
			this.setCurrentLine( currentLine + 1 );
		};
		^res;
	}

	setCurrentLine{ |cl|
		currentLine = cl;
		lineMap.put( currentLine, stream.pos );
		//	[ currentLine, stream.pos].postln;
	}

	goToLine{ |line|
		var ind, lmap;
		var pos = lineMap.at( line );
		if ( pos.notNil,
			{ 
				stream.pos = pos;
				currentLine = line;
			},
			{ 
				ind = lineMap.slotFor( line );
				// ind is now the index into the indices for the highest line number before the one we know.
				lmap = lineMap.indices.at( ind );
				stream.pos = lineMap.at( lmap );
				//	[ind, lmap, stream.pos, line, line-lmap ].postln;
				this.skipNextN( line - lmap );
			}
		);
	}

	readAtLine{ |line|
		this.goToLine( line );
		^this.next;
	}

	readAtLineInterpret{ |line|
		this.goToLine( line );
		^this.nextInterpret;
	}

	length{
		^stream.length;
	}

}

TabFilePlayer : FilePlayer { 
	classvar <delim = $\t;
}

CSVFilePlayer : FilePlayer { 
	classvar <delim = $,;
}

SemiColonFilePlayer : FilePlayer { 
	classvar <delim = $;;
}