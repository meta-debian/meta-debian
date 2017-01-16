SUMMARY = "module to associate user-defined magic to variables from Perl"
DESCRIPTION = "Variable::Magic is Perl way of enhancing objects. This mechanism lets the user\n\
 add extra data to any variable and hook syntaxical operations (such as access,\n\
 assignment or destruction) that can be applied to it.\n\
 .\n\
 With this module, you can add your own magic to any variable without having \n\
 to write a single line of XS."

HOMEPAGE = "https://metacpan.org/release/Variable-Magic"
PR = "r0"
inherit debian-package
PV = "0.55"

LICENSE = "Artistic-1.0 | GPL-1.0+"
LIC_FILES_CHKSUM = "file://README;beginline=604;endline=609;md5=b67af4ee1a35af96e5b6efe8e720c24c"

inherit cpan
# source format is 3.0 (quilt) but there is no debian patch
DEBIAN_QUILT_PATCHES = ""
