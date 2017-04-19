SUMMARY = "module for assigning a new name to referenced sub"
DESCRIPTION = "Sub::Name has only one function, which is also exported by default:\n\
 .\n\
 subname NAME, CODEREF\n\
 .\n\
 Assigns a new name to referenced sub. If package specification is\n\
 omitted in the name, then the current package is used. The return\n\
 value is the sub.\n\
 .\n\
 The name is only used for informative routines (caller, Carp, etc).\n\
 You won't be able to actually invoke the sub by the gven name. To\n\
 allow that, you need to do glob-assignment yourself."
HOMEPAGE = "https://metacpan.org/release/Sub-Name"

PR = "r0"
inherit debian-package
PV = "0.12"

LICENSE = "Artistic-1.0 | GPL-1.0+"
LIC_FILES_CHKSUM = "file://LICENSE;md5=af446873c060ced245d25c922117d0ec"

inherit cpan
# source format is 3.0 (quilt) but there is no debian patch
DEBIAN_QUILT_PATCHES = ""
