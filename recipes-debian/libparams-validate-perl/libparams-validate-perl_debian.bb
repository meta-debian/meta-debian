SUMMARY = "Perl module to validate parameters to Perl method/function calls"
DESCRIPTION = "\
Params::Validate is a Perl module providing a flexible way to validate method \
and function call parameters. The validation can be as simple as checking for \
the presence of required parameters, or more complex, like validating object \
classes (via isa) or capabilities (via can) and checking parameter types. It \
also provides extensibility through customized validation callbacks. \
. \
The module has been designed to work equally well with positional or named \
parameters (via a hash or hash reference) and includes experimental support \
for attributes (see Attribute::Params::Validate for details). \
"
HOMEPAGE = "https://metacpan.org/release/Params-Validate"
PR = "r0"
inherit debian-package

LICENSE = "Artistic-2.0"
LIC_FILES_CHKSUM = "\
	file://LICENSE;md5=4e73e61c349000cce50c5f285641fdd4"
inherit cpan_build
DEBIAN_QUILT_PATCHES = ""

do_install_append() {
	perl_version=${PERLVERSION}
	short_perl_version=`echo ${perl_version%.*}`
	install -d ${D}${libdir}/perl5/$short_perl_version
	mv ${D}${libdir}/perl/vendor_perl/${PERLVERSION}/* \
		${D}${libdir}/perl5/$short_perl_version
}

FILES_${PN} += "${libdir}/*"
FILES_${PN}-dbg += "${libdir}/perl5/*/auto/Params/Validate/XS/.debug"
