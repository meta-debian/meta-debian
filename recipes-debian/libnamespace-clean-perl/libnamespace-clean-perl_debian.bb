SUMMARY = "module for keeping imports and functions out of the current namespace"
DESCRIPTION = "namespace::clean is a Perl pragma for keeping imported functions out of the\n\
 current namespace. This is especially important because Perl functions are\n\
 naturally available as methods, which can complicate subclassing.\n\
 .\n\
 Using the namespace::clean pragma will remove all previously declared or\n\
 imported symbols at the end of the current package's compile cycle. This\n\
 means that functions called in the package itself will still be bound by\n\
 their name, but they won't show up as methods on your class or instances."
HOMEPAGE = "https://metacpan.org/release/namespace-clean"

PR = "r0"
inherit debian-package
PV = "0.25"

LICENSE = "Artistic-1.0 | GPL-1.0+"
LIC_FILES_CHKSUM = "file://lib/namespace/clean.pm;beginline=439;endline=441;md5=069f71d1a0008a6d8198f21e052d0b86"
inherit cpan
# source format is 3.0 (quilt) but there is no debian patch
DEBIAN_QUILT_PATCHES = ""

RDEPENDS_${PN} += "libpackage-stash-perl libb-hooks-endofscope-perl libsub-identify-perl \
                   libsub-name-perl"
