#
# base recipe: meta/recipes-devtools/git/git_2.18.0.bb
# base branch: master
# base commit: c7d9010a071573da7959b96cda953c1fa091ac4b
#

require recipes-devtools/git/git.inc

inherit debian-package
PV = "2.18.0"
DPR = "-1"
DSC_URI = "${DEBIAN_MIRROR}/main/g/${BPN}/${BPN}_${PV}${DPR}.dsc;md5sum=6af7b2ca0c1d2343926d710d714a7ec5"

# source format is 3.0 (quilt) but there is no debian/patches
DEBIAN_QUILT_PATCHES = ""

DEPENDS += "asciidoc-native xmlto-native"

EXTRA_OECONF += "ac_cv_snprintf_returns_bogus=no \
                 ac_cv_fread_reads_directories=${ac_cv_fread_reads_directories=yes} \
                 "

EXTRA_OEMAKE += "NO_GETTEXT=1"

# Overwrite do_install of git.inc.
# Poky install manpages from another source package 'git-manpages',
# but Debian does not provide this package.
do_compile_append() {
	oe_runmake -CDocumentation man
}
do_install() {
	oe_runmake install install-doc DESTDIR="${D}" bindir=${bindir} \
		template_dir=${datadir}/git-core/templates

	install -d ${D}/${datadir}/bash-completion/completions/
	install -m 644 ${S}/contrib/completion/git-completion.bash \
	    ${D}/${datadir}/bash-completion/completions/git
}
