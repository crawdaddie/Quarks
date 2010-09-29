/*using this human readable syntax: Scale('b','major')returns a FloatArray   FloatArray[ 2, 4, 6, 7, 9, 11, 13 ]  */  CXScale  { 	 	classvar <noteNames,<scales; 	 	*initClass {	 		noteNames = IdentityDictionary[ 				'a'->0, 				'a#'->1, 				'bb'->1, 				'b'->2, 				'c'->3, 				'c#'->4, 				'db'->4, 				'd'->5, 				'd#'->6, 				'eb'->6, 				'e'->7, 				'f'->8, 				'f#'->9, 				'gb'->9, 				'g'->10, 				'g#'->11, 				'ab'->11 				];		/*			I would like to keep data files that scales can be loaded from.			rather than sticking them here in the class */					scales = IdentityDictionary[					\major -> #[0,2,4,5,7,9,11],					\minor -> #[0,2,3,5,7,9,10],					\dorian -> #[0,2,3,5,7,9,10],					\minor7 -> #[0,2,3,5,7,9,10],					\aeolian -> #[0,2,3,5,7,8,10],					'dom7' -> #[0,2,4,5,7,9,10],					'flat7' -> #[0,2,4,5,7,9,10],					'lydianb7' -> #[0,2,4,6,7,9,10],					\whole -> #[0,2,4,6,8,10],					\dim -> #[0,1,3,4,6,7,9,10],					\altdim -> #[0,2,3,5,6,8,9,11],					\pelog -> #[0, 1, 3, 7, 8],					'hira-joshi' -> #[0, 1, 5, 6, 10]					]; 	}	*new { arg note='c',scale='major';		^(noteNames.at(note) + scales.at(scale)).as(FloatArray)	}	*degree { arg note='c';		^noteNames.at(note)	}	*degrees { arg notes;		^notes.collect({ arg note; noteNames.at(note) })	}				}  