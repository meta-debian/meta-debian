SUMMARY = "module for executing code after a scope finished compilation"
DESCRIPTION = "B::Hooks::EndOfScope allows you to execute code when perl finished \
 compiling the surrounding scope. It exports a single function, \
 'on_scope_end $codeblock', which can be used e.g. for introspection in \
 the constructor of your class."
HOMEPAGE = "https://metacpan.org/release/B-Hooks-EndOfScope/"

PR = "r0"
inherit debian-package
PV = "0.13"

LICENSE = "Artistic-1.0 | GPL-1.0+"
LIC_FILES_CHKSUM = "file://LICENSE;md5=ce97f081bbc40ea667ea5a42e1851697"
inherit cpan

# source format is 3.0 (quilt) but there is no debian patch
DEBIAN_QUILT_PATCHES = ""

RDEPENDS_${PN} += "libmodule-implementation-perl libmodule-runtime-perl \
                   libsub-exporter-progressive-perl libvariable-magic-perl"
