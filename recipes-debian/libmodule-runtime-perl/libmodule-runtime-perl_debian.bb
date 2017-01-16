SUMMARY = "Perl module for runtime module handling"
DESCRIPTION = "Module::Runtime deals with runtime handling of Perl modules, which are normally\n\
 handled at compile time.\n\
 .\n\
 This module provide functions that can, for example, require or use modules at\n\
 runtime, check for module names validity, compose module names and more."
HOMEPAGE = "https://metacpan.org/release/Module-Runtime/"

PR = "r0"
inherit debian-package
PV = "0.014"

LICENSE = "Artistic-1.0 | GPL-1.0+"
LIC_FILES_CHKSUM = "file://README;beginline=43;endline=44;md5=62e24a93342fede7221d66335c716f34"
inherit cpan_build
# Souce format is 3.0 but there is no debian patch files
DEBIAN_QUILT_PATCHES = ""
