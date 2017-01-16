SUMMARY = "module for loading one of several alternate implementations of a module"
DESCRIPTION = "Module::Implementation abstracts out the process of choosing one of several\n\
 underlying implementations for a module. This can be used to provide XS and\n\
 pure Perl implementations of a module, or it could be used to load an\n\
 implementation for a given OS or any other case of needing to provide\n\
 multiple implementations.\n\
 .\n\
 This module is only useful when you know all the implementations ahead of\n\
 time. If you want to load arbitrary implementations then you probably want\n\
 something like a plugin system, not this module."
HOMEPAGE = "https://metacpan.org/release/Module-Implementation"

PR = "r0"
inherit debian-package
PV = "0.09"

LICENSE = "Artistic-2.0"
LIC_FILES_CHKSUM = "\
	file://LICENSE;md5=1d2e1c522435425bc51123e3d4782081"
inherit cpan
# Souce format is 3.0 but there is no debian patch files
DEBIAN_QUILT_PATCHES = ""

RDEPENDS_${PN} += "libtry-tiny-perl libmodule-runtime-perl"
