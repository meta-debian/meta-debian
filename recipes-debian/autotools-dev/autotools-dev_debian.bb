SUMMARY = "Update infrastructure for config.{guess,sub} files"
DESCRIPTION = "\
 This package installs an up-to-date version of config.guess and\
 config.sub, used by the automake and libtool packages.  It provides\
 the canonical copy of those files for other packages as well.\
 .\
 It also documents in /usr/share/doc/autotools-dev/README.Debian.gz\
 best practices and guidelines for using autoconf, automake and\
 friends on Debian packages.  This is a must-read for any developers\
 packaging software that uses the GNU autotools, or GNU gettext.\
 .\
 Additionally this package provides seamless integration into Debhelper\
 or CDBS, allowing maintainers to easily update config.{guess,sub} files\
 in their packages.\
"
HOMEPAGE = "http://savannah.gnu.org/projects/config/"
LICENSE = "GPLv3"
SECTION = "devel"
LIC_FILES_CHKSUM = "file://debian/copyright;md5=446c4cd854b8364d9eba40e36507fd0f"

PR = "r0"

inherit debian-package
PV = "20140911.1"

inherit allarch

# Makefile doesn't have target clean
CLEANBROKEN = "1"

do_install() {
	install -d ${D}${datadir}/misc
	install config.guess config.sub ${D}${datadir}/misc
	install -d ${D}${bindir}
	install debian/dh_autotools-dev_updateconfig debian/dh_autotools-dev_restoreconfig ${D}${bindir}
	install -d ${D}${datadir}/perl5/Debian/Debhelper/Sequence
	install debian/autotools_dev.pm ${D}${datadir}/perl5/Debian/Debhelper/Sequence
}

FILES_${PN} += "${datadir}"

BBCLASSEXTEND = "native nativesdk"
