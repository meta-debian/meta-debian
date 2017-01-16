SUMMARY = "module for loading modules by name"
DESCRIPTION = ""require EXPR" only accepts Class/Name.pm style module names not\n\
 Class::Name. For that, Class::Load provides "load_class 'Class::Name'".\n\
 .\n\
 It's often useful to test whether a module can be loaded, instead of throwing\n\
 an error when it's not available. For that, Class::Load provides\n\
 "try_load_class 'Class::Name'".\n\
 .\n\
 Finally, sometimes it is important to know whether a particular class has\n\
 been loaded. Asking %INC is an option, but that will miss inner packages and\n\
 any class for which the filename does not correspond to the package name. For\n\
 that, this module provides "is_class_loaded 'Class::Name'"."
HOMEPAGE = "https://metacpan.org/release/Class-Load"

PR = "r0"
inherit debian-package
PV = "0.22"

LICENSE = "Artistic-1.0 | GPL-1.0+"
LIC_FILES_CHKSUM = "file://README;beginline=8;endline=11;md5=748d1f710d2987fcb5fecd942e080e9a \
                    file://LICENSE;md5=966d3b1584d9b8ec20d84d9fd9ce645c"
inherit cpan_build
# source format is 3.0 (quilt) but there is no debian patch
DEBIAN_QUILT_PATCHES = ""

RDEPENDS_${PN} += "libmodule-implementation-perl libmodule-runtime-perl libtry-tiny-perl \
                   libdata-optlist-perl  libnamespace-clean-perl libpackage-stash-perl"
