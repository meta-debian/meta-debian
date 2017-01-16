SUMMARY = "module for installing subroutines into packages easily"
DESCRIPTION = "Sub::Install module makes it easy to install subroutines into packages \
 without the unsightly mess of no strict or typeglobs lying about where \
 just anyone can see them."
HOMEPAGE = "https://github.com/rjbs/Sub-Install"

PR = "r0"
inherit debian-package
PV = "0.928"


LICENSE = "Artistic-1.0 | GPL-1.0+"
LIC_FILES_CHKSUM = "file://README;beginline=8;endline=11;md5=01b2efefd8a3e18fe5565c030b0d099f \
                    file://LICENSE;md5=f1866c63d501cdec18d81defddc67221"
inherit cpan
# source format is 3.0 (quilt) but there is no debian patch
DEBIAN_QUILT_PATCHES = ""
