SUMMARY = "module to retrieve names of code references"
DESCRIPTION = "Sub::Identify allows you to retrieve the real name of code references. For\n\
 this, it uses perl's introspection mechanism, provided by the B module.\n\
 .\n\
 It provides four functions : sub_name returns the name of the\n\
 subroutine (or __ANON__ if it's an anonymous code reference),\n\
 stash_name returns its package, and sub_fullname returns the\n\
 concatenation of the two.\n\
 .\n\
 The fourth function, get_code_info, returns a list of two elements,\n\
 the package and the subroutine name (in case of you want both and are worried\n\
 by the speed.)\n\
 .\n\
 In case of subroutine aliasing, those functions always return the original name."
HOMEPAGE = "https://metacpan.org/release/Sub-Identify"

PR = "r0"
inherit debian-package
PV = "0.08"

LICENSE = "Artistic-1.0 | GPL-1.0+"
LIC_FILES_CHKSUM = "file://ppport.h;beginline=361;endline=370;md5=08031c92992f15c4d1629f7c5781e61f"

inherit cpan
# source format is 3.0 (quilt) but there is no debian patch
DEBIAN_QUILT_PATCHES = ""
