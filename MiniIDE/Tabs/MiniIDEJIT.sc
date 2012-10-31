/*
MiniIDEJIT
by Jonathan Siemasko
Contact: schemawound@yahoo.com
Homepage: http://schemawound.com/
*/

MiniIDEJIT : MiniIDETab{
	classvar <tabLabel = \JIT;

	*new{|bounds|
		^super
			.new(bounds)
			.init();
	}

	init{
		//LAYOUT
		layout = HLayout(
			VLayout(
				StaticText().fixedSize_(512@8).string_("PDEF"),
				PdefAllGui(16).parent.view,
				StaticText().fixedSize_(512@8).string_("PDEFN"),
				PdefnAllGui(16).parent.view
			),
			VLayout(
				StaticText().fixedSize_(512@8).string_("TDEF"),
				TdefAllGui(35).parent.bounds_(512@70).view
			)
		);
	}
}